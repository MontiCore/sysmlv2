package de.monticore.lang.sysmlparametrics._parser;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparametrics._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * ConstraintsParserTest contains tests regarding valid and invalid constraints and their
 * corresponding (un)successful parsing by the parser.
 */
public class ConstraintsParserTest {

  @BeforeClass
  public static void initScope() {
    SysMLv2Mill.init();
  }

  /**
   * Tests parsing of constraints written in different (& allowed) ways
   */
  @Test
  public void testValidConstraintUsage() throws IOException {
    String model = "src/test/resources/sysmlparametrics/_parser/ValidConstraints.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    ASTSysMLPackage pkg = (ASTSysMLPackage) ast.getSysMLElement(0);
    ASTConstraintUsage constraint;

    // A non-assert constraint with name
    constraint = (ASTConstraintUsage) pkg.getSysMLElement(0);
    assertFalse(constraint.isAssert());
    assertTrue(constraint.isPresentName());

    // A non-assert constraint without name
    constraint = (ASTConstraintUsage) pkg.getSysMLElement(1);
    assertFalse(constraint.isAssert());
    assertFalse(constraint.isPresentName());

    // An assert constraint with 'constraint' keyword and name
    constraint = (ASTConstraintUsage) pkg.getSysMLElement(2);
    assertTrue(constraint.isAssert());
    assertTrue(constraint.isPresentName());

    // An assert constraint without 'constraint' keyword and with name
    constraint = (ASTConstraintUsage) pkg.getSysMLElement(3);
    assertTrue(constraint.isAssert());
    assertTrue(constraint.isPresentName());

    // An assert constraint without 'constraint' keyword and name
    constraint = (ASTConstraintUsage) pkg.getSysMLElement(4);
    assertTrue(constraint.isAssert());
    assertFalse(constraint.isPresentName());
  }

  /**
   * Tests unsuccessful parsing of invalid constraints
   */
  @Ignore
  @Test
  public void testInvalidConstraintUsage() throws IOException {
    String model;

    // A non-assert constraint without 'constraint' keyword should not be allowed
    model = "constraintA { 100 < 200 }";
    SysMLv2Mill.parser().parse_String(model);

    // Such a model should not be allowed
    model = "{ 100 < 200 }";
    SysMLv2Mill.parser().parse_String(model);

  }


}
