package de.monticore.lang.sysml.parser;

import de.monticore.lang.sysml.basics.interfaces.sharedbasis._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._parser.SysMLParser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLParserMultipleFiles {

  public List<ASTUnit> parseAllFilesInDirectory(String path) throws IOException {
    List<ASTUnit> parsedArtifacts = new ArrayList<>();
    try (Stream<Path> walk = Files.walk(Paths.get(path))) {

      List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());

      Log.info("Found " + result.size() + " Files.", SysMLParserMultipleFiles.class.getName());
      List<String> onlySysMLFiles = new ArrayList<>();
      for (String fullFileName : result) {
        if (fullFileName.endsWith(".sysml")) {
          onlySysMLFiles.add(fullFileName);
        }
      }
      Log.info("Found " + onlySysMLFiles.size() + " \".sysml\" Files.", SysMLParserMultipleFiles.class.getName());
      for (String pathToSysMLFile : onlySysMLFiles) {
        parsedArtifacts.add(parseSingleFile(pathToSysMLFile));
      }
      return parsedArtifacts;
    }catch (IOException e){
      Log.error("Error: The provided path lead to an IOException in " + SysMLParserMultipleFiles.class.getName());
      e.printStackTrace();
      throw e;
    }
  }

  public ASTUnit parseSingleFile(String path) throws IOException {
    SysMLParser parser = new SysMLParser();
    Path model = Paths.get(path);
    Optional<ASTUnit> astUnitOpt =  parser.parse(model.toString());
    if(!astUnitOpt.isPresent()){
      Log.error("Error: The model path "+ path + " had an empty model in " +
          SysMLParserMultipleFiles.class.getName());
      throw new IOException("Error: The model path "+ path + " had an empty model in " +
          SysMLParserMultipleFiles.class.getName());
    }
    return astUnitOpt.get();
  }
}

