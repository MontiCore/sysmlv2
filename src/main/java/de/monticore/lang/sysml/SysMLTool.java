/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml;

import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml._symboltable.HelperSysMLSymbolTableCreator;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.parser.SysMLParserMultipleFiles;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.ISysMLGlobalScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLTool {
  public static final String DEFAULT_SYMBOL_LOCATION = "target";

  public static void main(String[] args) {
    mainForJava(args);
  }

  public static List<ASTUnit> mainForJava(String[] args) {
    if (args.length == 0) {
      printUsage();
      return null;
    }
    List<String> libDirs = new ArrayList<>();
    boolean cocosOff = false;
    if(args.length == 2 || args.length == 3){
      for(int i= 0; i< args.length; i++){
        String arg = args[i];
        if(i!=0) { // expecting modeldirectory at 0
          if (arg.length() > 5) {
            if (arg.startsWith("-lib=")) {
              libDirs.add(arg.substring(5));
              Log.info("Searching for a lib directory here: \"" + arg.substring(5)+ "\".", SysMLTool.class.getName());
            }
          }
          if (arg.equals("-cocosOff")) {
            cocosOff = true;
          }
        }
      }

      int numberOfProcessableArguments = 0;
      numberOfProcessableArguments++; //model directory;
      if(cocosOff){
        numberOfProcessableArguments++;
      }
      numberOfProcessableArguments += libDirs.size();

      if(args.length != numberOfProcessableArguments){
        printUsage();
        return null;
      }
    }
    String dir = args[0];

    // Parsing
    List<ASTUnit> models = parseDirectory(dir);

    List<ASTUnit> allModels = new ArrayList<>(); //We do not execute CoCos on the library
    for (ASTUnit model : models) {
      allModels.add(model);
    }
    //Do it again for the SysML Lib.
    for(int libIndex=0; libIndex<libDirs.size(); libIndex++) {
      List<ASTUnit> libModels = parseDirectory(libDirs.get(libIndex)); // Parsing
      allModels.addAll(libModels);
    }

    // Symboltable
    ISysMLGlobalScope sysMLGlobalScope = buildSymbolTable(dir, allModels);

    if(cocosOff){
      Log.info("Context Conditions are deactivated by \"-cocosOff\".", SysMLTool.class.getName());
    }else {
      // Context Conditions
      Log.info("Checking Context Conditions.", SysMLTool.class.getName());
      if (libDirs.size()>=1) {
        Log.info("Libraries are not checked by the Context Conditions. "
            + "If you want to check them, just parse them with the first argument set to the library path.",
            SysMLTool.class.getName());
      }
      for (ASTUnit astUnit : models) {
        runDefaultCocos(astUnit);
      }
    }

    Log.info("Models processed successfully.", SysMLTool.class.getName());
    return allModels;
  }

  private static void printUsage(){
    Log.error("Please specify one single path to the input directory containing the input models."
        + "\n - Optional: Add directories for libraries with -lib=<path>"
        + "\n - Optional: Turn off Context Conditions with -cocosOff");
  }

  public static ISysMLArtifactScope buildSymbolTablePathToSingleFile(String pathToFile, ASTUnit model){
    Log.info("Creating Symbol Table.", SysMLTool.class.getName());
    final ModelPath mp = createModelpath(pathToFile);
    HelperSysMLSymbolTableCreator helperSysMLSymbolTableCreator = new HelperSysMLSymbolTableCreator();
    ISysMLArtifactScope scope = helperSysMLSymbolTableCreator.createSymboltableSingleASTUnit(model, mp);
    return scope;
  }

  public static ISysMLGlobalScope buildSymbolTable(String dir, List<ASTUnit> models){
    Log.info("Creating Symbol Table.", SysMLTool.class.getName());
    final ModelPath mp = createModelpath(getSysMLFilePathsInDirectory(dir, false));
    HelperSysMLSymbolTableCreator helperSysMLSymbolTableCreator = new HelperSysMLSymbolTableCreator();
    ISysMLGlobalScope sysMLGlobalScope = helperSysMLSymbolTableCreator.createSymboltableMultipleASTUnit(models,mp);
    return sysMLGlobalScope;
  }

  public static List<ASTUnit> parseDirectory(String dir){
    SysMLParserMultipleFiles sysMLParserMultipleFiles = new SysMLParserMultipleFiles();
    File checkingIfDirOrFileExists = new File(dir);
    if (!checkingIfDirOrFileExists.exists()) {
      Log.error("The provided input path " + dir + " does not exists. Exiting.");
      return new ArrayList<>();
    }

    Log.info("Parsing models in directory or file: \"" + dir+ "\".", SysMLTool.class.getName());
    // Gathering all models in given directory "dir".
    List<String> filePaths = getSysMLFilePathsInDirectory(dir, true);

    List<ASTUnit> models = new ArrayList<>();
    try {
      for (String path : filePaths) {
        models.add(sysMLParserMultipleFiles.parseSingleFile(path));
      }
    }
    catch (IOException e) {
      //e.printStackTrace();
      Log.error("Could not parse all provided models.");
      return new ArrayList<>();
    }
    return models;
  }

  public static List<String> getSysMLFilePathsInDirectory(String dir, Boolean printSizeOfFoundFiles) {
    try (Stream<Path> walk = Files.walk(Paths.get(dir))) {

      List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
      if(printSizeOfFoundFiles) {
        Log.info("Found " + result.size() + " Files.", SysMLParserMultipleFiles.class.getName());
      }
      List<String> onlySysMLFiles = new ArrayList<>();
      boolean foundKerML = false;
      for (String fullFileName : result) {
        if (fullFileName.endsWith(".sysml")) {
          onlySysMLFiles.add(fullFileName);
        }
        if(fullFileName.endsWith(".kerml")){
          foundKerML = true;
        }
      }
      if(printSizeOfFoundFiles) {
        if (foundKerML) {
          Log.warn("KerML files are not yet supported.");
        }
        Log.info("Found " + onlySysMLFiles.size() + " \".sysml\" Files.", SysMLParserMultipleFiles.class.getName());
      }
      if(onlySysMLFiles.size()==0){
        Log.error("There was not a single \".sysml\" in the given directory."+
            "It is likely that the directory was wrong or that the files have not the ending \".sysml\".");
      }
      return onlySysMLFiles;
    }
    catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<String>();
    }
  }

  public static void runDefaultCocos(ASTUnit unit) {
    SysMLCoCos cocos = new SysMLCoCos();
    cocos.getCheckerForAllCoCos().checkAll(unit);
  }

  public static ModelPath createModelpath(List<String> filePaths){
    Set<Path> p = Sets.newHashSet();
    for (String mP : filePaths) {
      File f = new File(mP);
      p.add(Paths.get(f.getAbsolutePath()));
    }
    final ModelPath mp = new ModelPath(p);
    return mp;
  }
  
  public static ModelPath createModelpath(String dirOrFile){
    List<String> filePathAsList = getSysMLFilePathsInDirectory(dirOrFile,false);
    return createModelpath(filePathAsList);
  }
}
