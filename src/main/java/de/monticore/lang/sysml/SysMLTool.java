package de.monticore.lang.sysml;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml._symboltable.HelperSysMLSymbolTableCreator;
import de.monticore.lang.sysml._symboltable.SysMLLanguageSub;
import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.parser.SysMLParserMultipleFiles;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLLanguage;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLTool {
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("Please specify only one single path to the input directory containing the input models.");
      return;
    }
    String dir = args[0];
    SysMLParserMultipleFiles sysMLParserMultipleFiles = new SysMLParserMultipleFiles();
    File checkingIfDirOrFileExists = new File(dir);
    if(!checkingIfDirOrFileExists.exists()){
      Log.error("The provided input path " + dir + " does not exists. Exiting.");
      return;
    }

    Log.info("Parsing models in directory or file " + dir, SysMLTool.class.getName());
    List<ASTUnit> models = new ArrayList<>();
    List<String> filePaths = getSysMLFilePathsInDirectory(dir);
    try {
      for(String path: filePaths){
        models.add(sysMLParserMultipleFiles.parseSingleFile(path));
      }
    }
    catch (IOException e) {
      //e.printStackTrace();
      Log.error("Could not parse all provided models.");
      return;
    }

    Log.info("Creating Symbol Table.", SysMLTool.class.getName());
    //TODO Finding: 0xA6100x1387190359 GlobalScope
    // SysMLGlobalScope has no EnclosingScope, so you cannot call methodgetEnclosingScope
    /*HelperSysMLSymbolTableCreator symbolTableHelper = new HelperSysMLSymbolTableCreator();
    SysMLLanguage sysMLLanguage = new SysMLLanguageSub("SysML", ".sysml");
    for (ASTUnit astUnit : models) {
      symbolTableHelper.buildSymbolTableWithGlobalScope(astUnit, new ModelPath(), sysMLLanguage);
    }*/

    //Setup Symboltable TODO
    //SysMLLanguage language = new SysMLLanguage();


    Log.info("Checking Context Conditions.", SysMLTool.class.getName());
    for (ASTUnit astUnit : models) {
      runDefaultCocos(astUnit);
    }



    Log.info("Parsed and checked all models successfully.", SysMLTool.class.getName());

  }

  public static List<String> getSysMLFilePathsInDirectory (String dir) {
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
      return result;
    }
    catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<String>();
    }
  }

    public static void runDefaultCocos(ASTUnit unit){
    SysMLCoCos cocos = new SysMLCoCos();
    cocos.getCheckerForAllCoCos().checkAll(unit);
  }
}
