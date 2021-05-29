/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.parser.examples.basics;

import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.bdd._ast.ASTBlockUnit;
import de.monticore.lang.sysml.utils.SysMLParserForTesting;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests SysML keywords like "block" or "part def"
 *
 * @author Mathias Pfeiffer
 * @version 1.0
 */
public class KeywordsTest {

  /**
   * Checks "block" to be an alternative of "part def"
   * https://drive.google.com/drive/folders/1L0E3RwO9ch3Ta5Ye4EIdmn15yRPgwS4B (slide 9)
   */
  @Test
  public void testBlockVsPartDef() {
    ASTUnit block = new SysMLParserForTesting()
            .parseSysMLFromString("block Test {}")
            .get();
    ASTUnit partDef = new SysMLParserForTesting()
            .parseSysMLFromString("part def Test {}")
            .get();
    // Both are BlockUnits
    Assert.assertTrue(block instanceof ASTBlockUnit);
    Assert.assertTrue(partDef instanceof ASTBlockUnit);
    // They are even the exact same, as its just a synonym
    Assert.assertEquals(block.getClass(), partDef.getClass());
  }

}
