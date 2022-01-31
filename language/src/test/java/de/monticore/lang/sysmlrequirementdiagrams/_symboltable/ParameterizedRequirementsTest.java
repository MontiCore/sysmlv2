package de.monticore.lang.sysmlrequirementdiagrams._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpressionBuilder;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static de.monticore.lang.sysmlv2.SysMLv2Language.createAndValidateSymbolTableAndCoCos;
import static org.junit.jupiter.api.Assertions.*;

public class ParameterizedRequirementsTest {

  private static ISysMLv2GlobalScope scope;

  @BeforeAll
  public static void initScope() {
    SysMLv2Mill.init();
    scope = SysMLv2Mill.globalScope();
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  /**
   * Creates a SysMLModel by performing the following steps:
   * 1. parsing
   * 2. symbol table creation
   * 3. CoCo checking
   * 4. postprocessing
   *
   * @param model String
   * @return ASTSysMLModel
   * @throws IOException
   */
  private ASTSysMLModel getModel(String model) throws IOException {
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    createAndValidateSymbolTableAndCoCos(false, Collections.singletonList(ast));
    return ast;
  }

  private void validateReqDefInheritedParameters(ISysMLv2Scope scope, String requirementName,
                                                 ArrayList<Map.Entry<String, String>> values) {
    RequirementDefSymbol reqDef = scope.resolveRequirementDef(requirementName).get();
    for (int i = 0; i < reqDef.getAstNode().sizeInheritedParameters(); ++i) {
      ASTSysMLParameter inheritedParameter = reqDef.getAstNode().getInheritedParameter(i);
      assertEquals(inheritedParameter.getName(), values.get(i).getKey());
      if(values.get(i).getValue() == null) {
        assertNull(inheritedParameter.getSymbol().getType());
      }
      else {
        assertEquals(
            inheritedParameter.getSymbol().getType().getTypeInfo().getName(),
            values.get(i).getValue());
      }
      assertTrue(reqDef.getSpannedScope().resolveField(values.get(i).getKey()).isPresent());
    }
  }

  private void validateReqUsageInheritedParameters(ISysMLv2Scope scope, String requirementName,
                                                   ArrayList<Map.Entry<String, Map.Entry<String, ASTExpression>>> values) {
    RequirementUsageSymbol reqUsage = scope.resolveRequirementUsage(requirementName).get();
    for (int i = 0; i < reqUsage.getAstNode().sizeInheritedParameters(); ++i) {
      ASTSysMLParameter inheritedParameter = reqUsage.getAstNode().getInheritedParameter(i);
      assertEquals(inheritedParameter.getName(), values.get(i).getKey());
      if(values.get(i).getValue() == null) {
        assertNull(inheritedParameter.getSymbol().getType());
      }
      else if(values.get(i).getValue().getKey() == null) {
        assertNull(inheritedParameter.getSymbol().getType());
      }
      else {
        assertEquals(
            inheritedParameter.getSymbol().getType().getTypeInfo().getName(),
            values.get(i).getValue().getKey());
        if(values.get(i).getValue().getValue() == null) {
          assertFalse(inheritedParameter.isPresentBinding());
        }
        else {
          assertTrue(inheritedParameter.getBinding().deepEquals(values.get(i).getValue().getValue()));
        }
      }
      assertTrue(reqUsage.getSpannedScope().resolveField(values.get(i).getKey()).isPresent());
    }
  }

  private void validateReqDefParameters(ISysMLv2Scope scope, String requirementName,
                                        boolean paramPresent, boolean inheritedParamsPresent,
                                        ArrayList<Map.Entry<String, String>> values) {
    RequirementDefSymbol reqDef = scope.resolveRequirementDef(requirementName).get();
    assertEquals(reqDef.getAstNode().isPresentParameterList(), paramPresent);
    assertEquals(reqDef.getAstNode().isPresentInheritedParameters(), inheritedParamsPresent);
    for (int i = 0; i < values.size(); ++i) {
      assertEquals(reqDef.getAstNode().getParameterList().getSysMLParameter(i).getName(), values.get(i).getKey());
      if(values.get(i).getValue() == null) {
        assertNull(reqDef.getAstNode().getParameterList().getSysMLParameter(i).getSymbol().getType());
      }
      else {
        assertEquals(
            reqDef.getAstNode().getParameterList().getSysMLParameter(i).getSymbol().getType().getTypeInfo().getName(),
            values.get(i).getValue());
      }
    }
  }

  private void validateReqUsageParameters(ISysMLv2Scope scope, String requirementName,
                                          boolean paramPresent, boolean inheritedParamsPresent,
                                          ArrayList<Map.Entry<String, Map.Entry<String, ASTExpression>>> values) {
    RequirementUsageSymbol reqUsage = scope.resolveRequirementUsage(requirementName).get();
    assertEquals(reqUsage.getAstNode().isPresentParameterList(), paramPresent);
    assertEquals(reqUsage.getAstNode().isPresentInheritedParameters(), inheritedParamsPresent);
    for (int i = 0; i < values.size(); ++i) {
      assertEquals(reqUsage.getAstNode().getParameterList().getSysMLParameter(i).getName(), values.get(i).getKey());
      if(values.get(i).getValue() == null) {
        assertNull(reqUsage.getAstNode().getParameterList().getSysMLParameter(i).getSymbol().getType());
        assertFalse(reqUsage.getAstNode().getParameterList().getSysMLParameter(i).isPresentBinding());
      }
      else if(values.get(i).getValue().getKey() == null) {
        assertNull(reqUsage.getAstNode().getParameterList().getSysMLParameter(i).getSymbol().getType());
      }
      else {
        assertEquals(
            reqUsage.getAstNode().getParameterList().getSysMLParameter(i).getSymbol().getType().getTypeInfo().getName(),
            values.get(i).getValue().getKey());
        if(values.get(i).getValue().getValue() == null) {
          assertFalse(reqUsage.getAstNode().getParameterList().getSysMLParameter(i).isPresentBinding());
        }
        else {
          assertTrue(reqUsage.getAstNode().getParameterList().getSysMLParameter(i).getBinding().deepEquals(
              values.get(i).getValue().getValue()));
        }
      }
    }
  }

  /**
   * Tests requirement parameters for requirement definitions.
   * Validates type compatibility and inheritance behavior.
   *
   * @throws IOException
   */
  @Test
  public void testRequirement_10() throws IOException {
    String model = "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_10.sysml";
    ASTSysMLModel ast = getModel(model);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);
    ArrayList<Map.Entry<String, String>> values = new ArrayList<>();

    validateReqDefParameters(packageScope, "ReqDef1", false, false, values);
    validateReqDefParameters(packageScope, "ReqDef2", false, false, values);

    values.add(new AbstractMap.SimpleEntry<>("a1", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("a2", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("a3", "Vehicle"));
    validateReqDefParameters(packageScope, "ReqDef3", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("b1", "Car"));
    values.add(new AbstractMap.SimpleEntry<>("b2", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b3", "Car"));
    validateReqDefParameters(packageScope, "ReqDef4", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("c1", "Vehicle"));
    validateReqDefParameters(packageScope, "ReqDef5", true, true, values);
    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a2", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("a3", "Vehicle"));
    validateReqDefInheritedParameters(packageScope, "ReqDef5", values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("c1", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("c2", "Car"));
    validateReqDefParameters(packageScope, "ReqDef6", true, true, values);
    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a3", "Vehicle"));
    validateReqDefInheritedParameters(packageScope, "ReqDef6", values);

    values.clear();
    validateReqDefParameters(packageScope, "ReqDef7", false, true, values);
    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("a2", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("a3", "Vehicle"));
    validateReqDefInheritedParameters(packageScope, "ReqDef7", values);

    values.clear();
    validateReqDefParameters(packageScope, "ReqDef9", false, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("a2", null));
    validateReqDefParameters(packageScope, "ReqDef16", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("b1", "Car"));
    values.add(new AbstractMap.SimpleEntry<>("b2", "Car"));
    values.add(new AbstractMap.SimpleEntry<>("b3", "Car"));
    validateReqDefParameters(packageScope, "ReqDef10", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", null));
    values.add(new AbstractMap.SimpleEntry<>("a2", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("a3", null));
    validateReqDefParameters(packageScope, "ReqDef11", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("b1", "Car"));
    values.add(new AbstractMap.SimpleEntry<>("b2", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b3", "Vehicle"));
    validateReqDefParameters(packageScope, "ReqDef12", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("b1", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b2", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b3", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b4", null));
    validateReqDefParameters(packageScope, "ReqDef13", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("b1", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b2", "Car"));
    values.add(new AbstractMap.SimpleEntry<>("b3", "Vehicle"));
    validateReqDefParameters(packageScope, "ReqDef14", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("b1", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b2", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b3", "Vehicle"));
    values.add(new AbstractMap.SimpleEntry<>("b4", "Vehicle"));
    validateReqDefParameters(packageScope, "ReqDef15", true, false, values);

  }

  /**
   * Tests requirement parameters for requirement usages.
   * Validates type compatibility and inheritance behavior.
   *
   * @throws IOException
   */
  @Test
  public void testRequirement_11() throws IOException {
    String model = "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_11.sysml";
    ASTSysMLModel ast = getModel(model);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    // data - start
    ArrayList<Map.Entry<String, Map.Entry<String, ASTExpression>>> values = new ArrayList<>();
    AbstractMap.SimpleEntry<String, ASTExpression> null_null = new AbstractMap.SimpleEntry<>(null, null);
    AbstractMap.SimpleEntry<String, ASTExpression> vehicle_null = new AbstractMap.SimpleEntry<>(
        "Vehicle", null);
    AbstractMap.SimpleEntry<String, ASTExpression> null_vehicle = new AbstractMap.SimpleEntry<>(
        null,
        new ASTNameExpressionBuilder().setName("vehicle").build());
    AbstractMap.SimpleEntry<String, ASTExpression> car_null = new AbstractMap.SimpleEntry<>(
        "Car", null);
    AbstractMap.SimpleEntry<String, ASTExpression> vehicle_vehicle = new AbstractMap.SimpleEntry<>(
        "Vehicle",
        new ASTNameExpressionBuilder().setName("vehicle").build());
    AbstractMap.SimpleEntry<String, ASTExpression> vehicle_car = new AbstractMap.SimpleEntry<>(
        "Vehicle",
        new ASTNameExpressionBuilder().setName("car").build());
    AbstractMap.SimpleEntry<String, ASTExpression> car_car = new AbstractMap.SimpleEntry<>(
        "Car",
        new ASTNameExpressionBuilder().setName("car").build());
    // data - end

    validateReqUsageParameters(packageScope, "reqUsage1", false, false, values);

    values.add(new AbstractMap.SimpleEntry<>("a1", vehicle_null));
    values.add(new AbstractMap.SimpleEntry<>("a2", vehicle_vehicle));
    values.add(new AbstractMap.SimpleEntry<>("a3", vehicle_car));
    validateReqUsageParameters(packageScope, "reqUsage2", true, false, values);

    values.clear();
    validateReqUsageParameters(packageScope, "reqUsage3", false, false, values);

    values.add(new AbstractMap.SimpleEntry<>("a1", car_car));
    validateReqUsageParameters(packageScope, "reqUsage4", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", vehicle_null));
    validateReqUsageParameters(packageScope, "reqUsage5", true, true, values);
    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a2", null_null));
    validateReqUsageInheritedParameters(packageScope, "reqUsage5", values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", vehicle_null));
    values.add(new AbstractMap.SimpleEntry<>("a2", vehicle_vehicle));
    validateReqUsageParameters(packageScope, "reqUsage6", true, false, values);

    values.add(new AbstractMap.SimpleEntry<>("a3", car_null));
    validateReqUsageParameters(packageScope, "reqUsage7", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", null_vehicle));
    validateReqUsageParameters(packageScope, "reqUsage8", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", car_car));
    validateReqUsageParameters(packageScope, "reqUsage9", true, false, values);

    values.clear();
    validateReqUsageParameters(packageScope, "reqUsage10", false, false, values);

    values.add(new AbstractMap.SimpleEntry<>("a1", vehicle_null));
    values.add(new AbstractMap.SimpleEntry<>("a2", null_null));
    validateReqUsageParameters(packageScope, "reqUsage11", true, false, values);

    values.clear();
    validateReqUsageParameters(packageScope, "reqUsage12", false, true, values);
    values.add(new AbstractMap.SimpleEntry<>("a1", vehicle_null));
    values.add(new AbstractMap.SimpleEntry<>("a2", vehicle_vehicle));
    values.add(new AbstractMap.SimpleEntry<>("a3", vehicle_car));
    validateReqUsageInheritedParameters(packageScope, "reqUsage12", values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", car_car));
    values.add(new AbstractMap.SimpleEntry<>("a2", vehicle_vehicle));
    validateReqUsageParameters(packageScope, "reqUsage13", true, true, values);
    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a3", vehicle_car));
    validateReqUsageInheritedParameters(packageScope, "reqUsage13", values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", car_car));
    values.add(new AbstractMap.SimpleEntry<>("a2", vehicle_vehicle));
    values.add(new AbstractMap.SimpleEntry<>("a3", vehicle_null));
    validateReqUsageParameters(packageScope, "reqUsage14", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", car_car));
    values.add(new AbstractMap.SimpleEntry<>("a2", vehicle_vehicle));
    values.add(new AbstractMap.SimpleEntry<>("a3", vehicle_null));
    values.add(new AbstractMap.SimpleEntry<>("a4", car_null));
    validateReqUsageParameters(packageScope, "reqUsage15", true, false, values);

    values.clear();
    validateReqUsageParameters(packageScope, "reqUsage16", false, false, values);

    values.add(new AbstractMap.SimpleEntry<>("a1", null_vehicle));
    values.add(new AbstractMap.SimpleEntry<>("a2", car_null));
    validateReqUsageParameters(packageScope, "reqUsage17", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", vehicle_vehicle));
    values.add(new AbstractMap.SimpleEntry<>("a2", car_car));
    validateReqUsageParameters(packageScope, "reqUsage19", true, false, values);

    values.add(new AbstractMap.SimpleEntry<>("a3", car_null));
    values.add(new AbstractMap.SimpleEntry<>("a4", null_vehicle));
    validateReqUsageParameters(packageScope, "reqUsage20", true, false, values);

    values.clear();
    values.add(new AbstractMap.SimpleEntry<>("a1", vehicle_vehicle));
    values.add(new AbstractMap.SimpleEntry<>("a2", car_car));
    values.add(new AbstractMap.SimpleEntry<>("a3", car_null));
    validateReqUsageParameters(packageScope, "reqUsage21", true, false, values);

    values.add(new AbstractMap.SimpleEntry<>("a4", null_vehicle));
    validateReqUsageParameters(packageScope, "reqUsage22", true, false, values);
  }

  /**
   * Tests invalid requirement parameters for requirement definitions & usages.
   *
   * @throws IOException
   */
  @Test
  public void testRequirement_12() throws IOException {
    try {
      Log.enableFailQuick(false);
      Log.clearFindings();
      String model = "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_12.sysml";
      ASTSysMLModel ast = getModel(model);
      String error1 = Log.getFindings().get(0).getMsg();
      String error2 = Log.getFindings().get(1).getMsg();
      String error3 = Log.getFindings().get(2).getMsg();
      String error4 = Log.getFindings().get(3).getMsg();
      String error5 = Log.getFindings().get(4).getMsg();
      String error6 = Log.getFindings().get(5).getMsg();
      String error7 = Log.getFindings().get(6).getMsg();
      String error8 = Log.getFindings().get(7).getMsg();
      String error9 = Log.getFindings().get(8).getMsg();
      String error10 = Log.getFindings().get(9).getMsg();

      assertEquals(error1, "RequirementDefinition 'ReqDefWithFeatureValue' has a "
          + "parameter 'a' with a FeatureValue. FeatureValues are not allowed in definitions.");
      assertEquals(error2, "RequirementDefinition 'ReqDefWithNoMandatoryRedefinition'"
          + " specializes multiple parameterized requirements, but does not redefine all of the inherited parameters.");
      assertEquals(error3, "RequirementUsage 'reqUsageWithNoMandatoryRedefinition1' featuretypes/subsets multiple"
          + " parameterized requirements, but does not redefine all of the inherited parameters.");
      assertEquals(error4, "RequirementUsage 'reqUsageWithNoMandatoryRedefinition2' featuretypes/subsets multiple"
          + " parameterized requirements, but does not redefine all of the inherited parameters.");
      assertEquals(error5, "RequirementUsage 'reqUsageWithMissingMandatoryRedefinition' featuretypes/subsets "
          + "multiple parameterized requirements, but does not redefine all of the inherited parameters.");
      assertEquals(error6, "RequirementParameter 'a' has type 'Vehicle', but was redefined with type 'double', "
          + "which is not compatible.");
      assertEquals(error7, "RequirementParameter 'a' has type 'Vehicle', but was redefined with type 'double', "
          + "which is not compatible.");
      assertEquals(error8, "RequirementParameter 'a' has type 'Vehicle', but was redefined with type 'double', "
          + "which is not compatible.");
      assertEquals(error9, "RequirementParameter 'a' has type 'Vehicle', but was assigned a value of type 'double', "
          + "which is not compatible.");
      assertEquals(error10, "RequirementParameter 'a' has type 'Car', but was assigned a value of type 'Vehicle', "
          + "which is not compatible.");
    }
    finally {
      Log.clearFindings();
      Log.enableFailQuick(true);
    }
  }

}
