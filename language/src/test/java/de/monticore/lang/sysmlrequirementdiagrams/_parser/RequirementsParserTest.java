package de.monticore.lang.sysmlrequirementdiagrams._parser;

import de.monticore.lang.sysmlcommons._ast.ASTParameterList;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Checks successful parsing of requirements
 */
public class RequirementsParserTest {

  @BeforeAll
  public static void initScope() {
    SysMLv2Mill.init();
  }

  /**
   * Checks if the MCType of the SysMLParameter is the one given
   *
   * @param parameter ASTSysMLParameter, whose MCType should be checked
   * @param mcType    String, MCType value
   */
  private void checkMCType(ASTSysMLParameter parameter, String mcType) {
    assertEquals(mcType, ((ASTMCQualifiedType) parameter.getMCType()).getMCQualifiedName().toString());
  }

  /**
   * Checks if the provided SysMLParameter has a bound expression
   *
   * @param parameter ASTSysMLParameter, that should be checked for the presence of bound expression
   */
  private void hasBindingExpression(ASTSysMLParameter parameter) {
    assertNotEquals(null, parameter.getBinding());
  }

  /**
   * The following test validates that parameterized requirement definitions and usages are parsed correctly.
   *
   * @throws IOException
   */
  @Test
  public void testParameterizedRequirements() throws IOException {
    String model = "src/test/resources/sysmlrequirementdiagrams/_parser/ParameterizedRequirements.sysml";
    ASTParameterList parameterList;

    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    ASTSysMLPackage pkg = (ASTSysMLPackage) ast.getSysMLElement(0);

    // Requirement definition with only typed params

    parameterList = ((ASTRequirementDef) pkg.getSysMLElement(0)).getParameterList();
    assertEquals(2, parameterList.sizeSysMLParameters());
    checkMCType(parameterList.getSysMLParameter(0), "Real");
    checkMCType(parameterList.getSysMLParameter(1), "Real");

    // Requirement definition with typed params and bound expressions

    parameterList = ((ASTRequirementDef) pkg.getSysMLElement(1)).getParameterList();
    assertEquals(2, parameterList.sizeSysMLParameters());
    checkMCType(parameterList.getSysMLParameter(0), "Real");
    checkMCType(parameterList.getSysMLParameter(1), "Real");
    hasBindingExpression(parameterList.getSysMLParameter(1));

    // A specialized requirement definition with typed params and bound expressions

    parameterList = ((ASTRequirementDef) pkg.getSysMLElement(2)).getParameterList();
    assertEquals(2, parameterList.sizeSysMLParameters());
    checkMCType(parameterList.getSysMLParameter(1), "Real");
    hasBindingExpression(parameterList.getSysMLParameter(0));
    hasBindingExpression(parameterList.getSysMLParameter(1));

    // Requirement usage with only bound expressions

    parameterList = ((ASTRequirementUsage) pkg.getSysMLElement(3)).getParameterList();
    assertEquals(2, parameterList.sizeSysMLParameters());
    hasBindingExpression(parameterList.getSysMLParameter(0));
    hasBindingExpression(parameterList.getSysMLParameter(1));

    // Requirement usage with typed params and bound expressions

    parameterList = ((ASTRequirementUsage) pkg.getSysMLElement(4)).getParameterList();
    assertEquals(2, parameterList.sizeSysMLParameters());
    checkMCType(parameterList.getSysMLParameter(1), "Real");
    hasBindingExpression(parameterList.getSysMLParameter(0));
    hasBindingExpression(parameterList.getSysMLParameter(1));
  }
}
