package de.monticore.lang.sysml.parser.examples.basics;

/**
 * TODO implement me
 *
 * @author Robin Muenstermann
 * @version 1.0
 */
public class NamesTest {

  /*@Ignore //TODO this test is outdated, there is no interface anymore.
  private void checkParser(Path model){
    try {
      NamesParser parser = new NamesParser();
      Optional<ASTQualifiedName> sysmlPackage = parser.parse(model.toString());
      assertFalse(parser.hasErrors());
      assertTrue(sysmlPackage.isPresent());
    }catch( IOException e){
      e.printStackTrace();
      fail("There was an exception when parsing the model " + model + ": " + e.getMessage());
    }
  }
  @Test
  public void colonNameTest(){
    Path model = Paths.get("src/test/resources/testing/basics/names/colonName.sysml");
    checkParser(model);
  }

  @Test
  public void dotNameTest(){
    Path model = Paths.get("src/test/resources/testing/basics/names/dotName.sysml");
    checkParser(model);
  }
  @Test
  public void simpleNameTest(){
    Path model = Paths.get("src/test/resources/testing/basics/names/simpleName.sysml");
    checkParser(model);
  }*/
}
