

package de.monticore.lang.sysml.utils;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.ast.ASTNode;
import de.monticore.lang.sysml.sysmlbasics._ast.ASTUnitPrefix;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * TODO implement me
 *
 * @author Robin Muenstermann
 * @version 1.0
 */
public class GenericParser<ParserOfLanguage extends MCConcreteParser, ASTRoot extends  de.monticore.ast.ASTNode> {

  private Path model;
  private ParserOfLanguage parserOfLanguage;
  public GenericParser(Path pathToFile, ParserOfLanguage parserOfLanguage){
    this.model = pathToFile;
    this.parserOfLanguage = parserOfLanguage;
  }

   /*public Optional<ASTRoot> parse(){

    try {
      Optional<ASTRoot> astRoot = parserOfLanguage.parse(model.toString()); //Why would this give an error?
      assertFalse(parserOfLanguage.hasErrors());
      assertTrue(astRoot.isPresent());
      return astRoot;
    }
    catch (IOException e) {
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
      return Optional.empty(); //TODO
    }
  }*/
}
