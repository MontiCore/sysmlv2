package de.monticore.lang.sysml;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.parser.SysMLParserMultipleFiles;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    List<ASTUnit> models;
    try {
      models = sysMLParserMultipleFiles.parseAllFilesInDirectory(dir);
    }
    catch (IOException e) {
      //e.printStackTrace();
      Log.error("Could not parse all provided models.");
      return;
    }

    Log.info("Creating Symbol Table.", SysMLTool.class.getName());
    //Setup Symboltable TODO
    //SysMLLanguage language = new SysMLLanguage();


    Log.info("Checking Context Conditions.", SysMLTool.class.getName());
    for (ASTUnit astUnit : models) {
      runDefaultCocos(astUnit);
    }



    Log.info("Parsed and checked all models successfully.", SysMLTool.class.getName());

  }


  public static void runDefaultCocos(ASTUnit unit){
    SysMLCoCos cocos = new SysMLCoCos();
    cocos.getCheckerForAllCoCos().checkAll(unit);
  }
}
