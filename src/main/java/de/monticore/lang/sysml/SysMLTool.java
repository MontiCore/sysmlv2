package de.monticore.lang.sysml;

import com.google.common.collect.Sets;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml._symboltable.HelperSysMLSymbolTableCreator;
import de.monticore.lang.sysml._symboltable.SysMLLanguageSub;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.parser.SysMLParserMultipleFiles;
import de.monticore.lang.sysml.sysml._symboltable.*;
import de.monticore.lang.sysml.sysml._symboltable.serialization.SysMLScopeDeSer;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    if (args.length == 0 ||args.length>3) {
      printUsage();
      return null;
    }
    Optional<String> libDir = Optional.empty();
    boolean cocosOff = false;
    if(args.length == 2 || args.length == 3){
      for(String arg:args){
        if(arg.length()>5){
          if(arg.startsWith("-lib=")){
            libDir = Optional.of(arg.substring(5));
            Log.info("Searching for a lib directory here: " + libDir.get(), SysMLTool.class.getName());
          }
        }
        if(arg.equals("-cocosOff")){
          cocosOff=true;
        }
      }




      if(args.length == 3 && (!libDir.isPresent() || !cocosOff)){
        printUsage();
        return null;
      }if(args.length == 2 && !(libDir.isPresent() || cocosOff)){
        printUsage();
        return null;
      }
    }
    String dir = args[0];

    // Parsing
    List<ASTUnit> models = parseDirectory(dir);

    // Symboltable
    SysMLGlobalScope sysMLGlobalScope = buildSymbolTable(dir, models);

    //Do it again for the SysML Lib.
    if(libDir.isPresent()){
      List<ASTUnit> libModels = parseDirectory(libDir.get()); // Parsing
      SysMLGlobalScope libModelsGlobalScope = buildSymbolTable(libDir.get(), libModels);  // Symboltable
      for (ISysMLScope libArtifactScope : libModelsGlobalScope.getSubScopes()) {  //Merge with the globalscope
        sysMLGlobalScope.addSubScope(libArtifactScope);
      }
    }


    if(cocosOff){
      Log.info("Context Conditions are deactivated by \"-cocosOff\".", SysMLTool.class.getName());
    }else {
      // Context Conditions
      Log.info("Checking Context Conditions.", SysMLTool.class.getName());
      if (libDir.isPresent()) {
        Log.info("Currently the Context Conditions for the library is not checked, because KerML is not yet supported.", SysMLTool.class.getName());
      }
      for (ASTUnit astUnit : models) {
        runDefaultCocos(astUnit);
      }
    }

    Log.info("Models processed successfully.", SysMLTool.class.getName());
    return models;
  }

  private static void printUsage(){
    Log.error("Please specify one single path to the input directory containing the input models."
        + "\n - Optional: Add a directory for libraries with -lib=<path>"
        + "\n - Optional: Turn off Context Conditions with -cocosOff");
  }

  public static SysMLArtifactScope buildSymbolTablePathToSingleFile(String pathToFile, ASTUnit model){
    Log.info("Creating Symbol Table.", SysMLTool.class.getName());
    final ModelPath mp = createModelpath(pathToFile);
    HelperSysMLSymbolTableCreator helperSysMLSymbolTableCreator = new HelperSysMLSymbolTableCreator();
    SysMLArtifactScope scope = helperSysMLSymbolTableCreator.createSymboltableSingleASTUnit(model,mp);
    return scope;
  }

  public static SysMLGlobalScope buildSymbolTable(String dir, List<ASTUnit> models){
    Log.info("Creating Symbol Table.", SysMLTool.class.getName());
    final ModelPath mp = createModelpath(getSysMLFilePathsInDirectory(dir));
    HelperSysMLSymbolTableCreator helperSysMLSymbolTableCreator = new HelperSysMLSymbolTableCreator();
    SysMLGlobalScope sysMLGlobalScope = helperSysMLSymbolTableCreator.createSymboltableMultipleASTUnit(models,mp);
    return sysMLGlobalScope;
  }

  public static List<ASTUnit> parseDirectory(String dir){
    SysMLParserMultipleFiles sysMLParserMultipleFiles = new SysMLParserMultipleFiles();
    File checkingIfDirOrFileExists = new File(dir);
    if (!checkingIfDirOrFileExists.exists()) {
      Log.error("The provided input path " + dir + " does not exists. Exiting.");
      return new ArrayList<>();
    }

    Log.info("Parsing models in directory or file " + dir, SysMLTool.class.getName());
    // Gathering all models in given directory "dir".
    List<String> filePaths = getSysMLFilePathsInDirectory(dir);

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

  public static List<String> getSysMLFilePathsInDirectory(String dir) {
    try (Stream<Path> walk = Files.walk(Paths.get(dir))) {

      List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

      Log.info("Found " + result.size() + " Files.", SysMLParserMultipleFiles.class.getName());
      List<String> onlySysMLFiles = new ArrayList<>();
      for (String fullFileName : result) {
        if (fullFileName.endsWith(".sysml")) {
          onlySysMLFiles.add(fullFileName);
        }
      }
      Log.info("Found " + onlySysMLFiles.size() + " \".sysml\" Files.", SysMLParserMultipleFiles.class.getName());
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
    List<String> filePathAsList = getSysMLFilePathsInDirectory(dirOrFile);
    return createModelpath(filePathAsList);
  }
}
