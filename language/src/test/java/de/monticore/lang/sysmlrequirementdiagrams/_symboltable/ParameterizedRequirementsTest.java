package de.monticore.lang.sysmlrequirementdiagrams._symboltable;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpressionBuilder;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTBaseRequirement;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteralBuilder;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static de.monticore.lang.sysmlv2.SysMLv2Language.createAndValidateSymbolTableAndCoCos;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
   * 3. postprocessing
   *
   * @param model String
   * @return ASTSysMLModel
   * @throws IOException
   */
  private ASTSysMLModel getModel(String model) throws IOException {
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    createAndValidateSymbolTableAndCoCos(false, Arrays.asList(ast));
    return ast;
  }

  private void validateParameters(
      HashMap<String, ASTSysMLParameter> parameters,
      Map<String, Map.Entry<String, ASTExpression>> types) {
    for (Map.Entry<String, Map.Entry<String, ASTExpression>> entry : types.entrySet()) {
      String key = entry.getKey();
      String type = entry.getValue().getKey();
      ASTExpression expression = entry.getValue().getValue();
      ASTSysMLParameter parameter = parameters.get(key);

      if(type != null) {
        assertEquals(parameter.getType().getTypeInfo().getName(), type);
      }
      else {
        assertNull(parameter.getType());
      }
      if(expression != null) {
        assertTrue(parameter.getExpression().deepEquals(expression));
      }
      else {
        assertNull(parameter.getExpression());
      }
    }
  }

  /**
   * Tests requirement parameters for requirement definitions.
   * Validates duplicates and type compatibility.
   *
   * @throws IOException
   */
  @Test
  public void testRequirement_10() throws IOException {
    String model = "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_10.sysml";
    ASTSysMLModel ast = getModel(model);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);
    HashMap<String, Map.Entry<String, ASTExpression>> types = new HashMap<>();

    // data
    AbstractMap.SimpleEntry<String, ASTExpression> noType = new AbstractMap.SimpleEntry<>(null, null);
    AbstractMap.SimpleEntry<String, ASTExpression> nullVehicle = new AbstractMap.SimpleEntry<>("Vehicle", null);
    AbstractMap.SimpleEntry<String, ASTExpression> nullCar = new AbstractMap.SimpleEntry<>("Car", null);
    AbstractMap.SimpleEntry<String, ASTExpression> nullEngine = new AbstractMap.SimpleEntry<>("Engine", null);
    AbstractMap.SimpleEntry<String, ASTExpression> nullTurboEngine = new AbstractMap.SimpleEntry<>("TurboEngine", null);
    AbstractMap.SimpleEntry<String, ASTExpression> vehicle_car1 = new AbstractMap.SimpleEntry<>(
        "Vehicle",
        new ASTNameExpressionBuilder().setName("car1").build());
    AbstractMap.SimpleEntry<String, ASTExpression> vehicle_vehicle1 = new AbstractMap.SimpleEntry<>(
        "Vehicle",
        new ASTNameExpressionBuilder().setName("vehicle1").build());
    AbstractMap.SimpleEntry<String, ASTExpression> engine_turboEngine1 = new AbstractMap.SimpleEntry<>(
        "Engine",
        new ASTNameExpressionBuilder().setName("turboEngine1").build());
    AbstractMap.SimpleEntry<String, ASTExpression> mass = new AbstractMap.SimpleEntry<>(
        "double",
        new ASTLiteralExpressionBuilder().setLiteral(
            new ASTBasicDoubleLiteralBuilder().setPre("100").setPost("0").build()).build());
    AbstractMap.SimpleEntry<String, ASTExpression> car_car1 = new AbstractMap.SimpleEntry<>("Car",
        new ASTNameExpressionBuilder().setName("car1").build());
    // end

    types.put("vehicle", nullVehicle);
    validateRequirementDefinitionParameters(packageScope, "ReqDef1", types);
    validateRequirementDefinitionParameters(packageScope, "ReqDef2", types);
    validateRequirementDefinitionParameters(packageScope, "ReqDef3", types);

    types.put("vehicle", nullCar);
    validateRequirementDefinitionParameters(packageScope, "ReqDef4", types);
    validateRequirementDefinitionParameters(packageScope, "ReqDef5", types);

    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    validateRequirementDefinitionParameters(packageScope, "ReqDef6", types);
    types.remove("engine");

    types.put("vehicle", vehicle_vehicle1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef7", types);

    types.put("vehicle", vehicle_car1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef8", types);

    types.put("vehicle", car_car1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef9", types);

    types.put("vehicle", vehicle_car1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef10", types);

    types.put("vehicle", car_car1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef11", types);

    types.put("vehicle", vehicle_vehicle1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef12", types);

    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    validateRequirementDefinitionParameters(packageScope, "ReqDef14", types);

    types.put("vehicle", nullCar);
    types.put("engine", engine_turboEngine1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef15", types);

    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    types.put("mass", mass);
    validateRequirementDefinitionParameters(packageScope, "ReqDef16", types);

    types.put("vehicle", vehicle_car1);
    types.put("engine", nullTurboEngine);
    validateRequirementDefinitionParameters(packageScope, "ReqDef17", types);

    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    types.remove("mass");
    validateRequirementDefinitionParameters(packageScope, "ReqDef19", types);

    types.put("engine", engine_turboEngine1);
    types.put("vehicle", nullCar);
    validateRequirementDefinitionParameters(packageScope, "ReqDef20", types);

    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    types.put("mass", mass);
    validateRequirementDefinitionParameters(packageScope, "ReqDef21", types);

    types.put("vehicle", vehicle_car1);
    types.put("engine", nullTurboEngine);
    validateRequirementDefinitionParameters(packageScope, "ReqDef22", types);

    types.put("vehicle", nullCar);
    types.remove("engine");
    types.remove("mass");
    validateRequirementDefinitionParameters(packageScope, "ReqDef24", types);

    types.put("vehicle", car_car1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef25", types);

    types.put("vehicle", nullVehicle);
    validateRequirementDefinitionParameters(packageScope, "ReqDef26", types);

    types.put("vehicle", vehicle_vehicle1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef27", types);

    types.put("vehicle", vehicle_car1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef28", types);

    types.put("vehicle", nullVehicle);
    validateRequirementDefinitionParameters(packageScope, "ReqDef29", types);

    types.put("vehicle", noType);
    validateRequirementDefinitionParameters(packageScope, "ReqDef30", types);

    types.put("vehicle", nullVehicle);
    validateRequirementDefinitionParameters(packageScope, "ReqDef31", types);

    types.put("vehicle", car_car1);
    validateRequirementDefinitionParameters(packageScope, "ReqDef32", types);

    types.put("vehicle", nullVehicle);
    validateRequirementDefinitionParameters(packageScope, "ReqDef33", types);

    types.put("vehicle", nullCar);
    validateRequirementDefinitionParameters(packageScope, "ReqDef34", types);
  }

  /**
   * Tests requirement parameters for requirement usages.
   * Validates duplicates and type compatibility.
   *
   * @throws IOException
   */
  @Test
  public void testRequirement_11() throws IOException {
    String model = "src/test/resources/sysmlrequirementdiagrams/_symboltable/requirement_11.sysml";
    ASTSysMLModel ast = getModel(model);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);
    HashMap<String, Map.Entry<String, ASTExpression>> types = new HashMap<>();

    // data
    AbstractMap.SimpleEntry<String, ASTExpression> nullVehicle = new AbstractMap.SimpleEntry<>("Vehicle", null);
    AbstractMap.SimpleEntry<String, ASTExpression> nullCar = new AbstractMap.SimpleEntry<>("Car", null);
    AbstractMap.SimpleEntry<String, ASTExpression> nullEngine = new AbstractMap.SimpleEntry<>("Engine", null);
    AbstractMap.SimpleEntry<String, ASTExpression> nullTurboEngine = new AbstractMap.SimpleEntry<>("TurboEngine", null);
    AbstractMap.SimpleEntry<String, ASTExpression> vehicle_car1 = new AbstractMap.SimpleEntry<>(
        "Vehicle",
        new ASTNameExpressionBuilder().setName("car1").build());
    AbstractMap.SimpleEntry<String, ASTExpression> engine_turboEngine1 = new AbstractMap.SimpleEntry<>(
        "Engine",
        new ASTNameExpressionBuilder().setName("turboEngine1").build());
    AbstractMap.SimpleEntry<String, ASTExpression> mass = new AbstractMap.SimpleEntry<>(
        "double",
        new ASTLiteralExpressionBuilder().setLiteral(
            new ASTBasicDoubleLiteralBuilder().setPre("100").setPost("0").build()).build());
    AbstractMap.SimpleEntry<String, ASTExpression> nullMass = new AbstractMap.SimpleEntry<>(
        "double", null);
    AbstractMap.SimpleEntry<String, ASTExpression> car_car1 = new AbstractMap.SimpleEntry<>("Car",
        new ASTNameExpressionBuilder().setName("car1").build());
    AbstractMap.SimpleEntry<String, ASTExpression> turboEngine_turboEngine1 = new AbstractMap.SimpleEntry<>(
        "TurboEngine",
        new ASTNameExpressionBuilder().setName("turboEngine1").build());
    AbstractMap.SimpleEntry<String, ASTExpression> vehicle_vehicle1 = new AbstractMap.SimpleEntry<>(
        "Vehicle",
        new ASTNameExpressionBuilder().setName("vehicle1").build());
    // end

    types.put("vehicle", nullVehicle);
    validateRequirementUsageParameters(packageScope, "reqUsage1", types);

    validateRequirementUsageParameters(packageScope, "reqUsage2", types);

    types.put("engine", engine_turboEngine1);
    types.put("vehicle", nullCar);
    validateRequirementUsageParameters(packageScope, "reqUsage3", types);

    types.put("engine", turboEngine_turboEngine1);
    types.put("vehicle", nullVehicle);
    validateRequirementUsageParameters(packageScope, "reqUsage4", types);

    types.put("vehicle", nullCar);
    validateRequirementUsageParameters(packageScope, "reqUsage5", types);

    types.remove("engine");
    types.put("vehicle", nullVehicle);
    validateRequirementUsageParameters(packageScope, "reqUsage6", types);

    types.put("vehicle", nullCar);
    validateRequirementUsageParameters(packageScope, "reqUsage7", types);

    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    validateRequirementUsageParameters(packageScope, "reqUsage8", types);

    types.put("vehicle", car_car1);
    types.put("engine", turboEngine_turboEngine1);
    types.put("mass", nullMass);
    validateRequirementUsageParameters(packageScope, "reqUsage9", types);

    types.remove("mass");
    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    validateRequirementUsageParameters(packageScope, "reqUsage11", types);

    types.put("vehicle", nullCar);
    validateRequirementUsageParameters(packageScope, "reqUsage12", types);

    types.put("mass", mass);
    types.put("vehicle", nullVehicle);
    validateRequirementUsageParameters(packageScope, "reqUsage13", types);

    types.put("mass", mass);
    types.put("vehicle", vehicle_car1);
    types.put("engine", nullTurboEngine);
    validateRequirementUsageParameters(packageScope, "reqUsage14", types);

    types.remove("mass");
    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    validateRequirementUsageParameters(packageScope, "reqUsage16", types);

    types.put("vehicle", nullCar);
    validateRequirementUsageParameters(packageScope, "reqUsage17", types);

    types.put("mass", mass);
    types.put("vehicle", nullVehicle);
    validateRequirementUsageParameters(packageScope, "reqUsage18", types);

    types.put("mass", mass);
    types.put("vehicle", vehicle_car1);
    types.put("engine", nullTurboEngine);
    validateRequirementUsageParameters(packageScope, "reqUsage19", types);

    types.put("mass", nullMass);
    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    validateRequirementUsageParameters(packageScope, "reqUsage22", types);

    types.remove("mass");
    validateRequirementUsageParameters(packageScope, "reqUsage20", types);

    types.put("vehicle", nullCar);
    types.put("engine", nullTurboEngine);
    validateRequirementUsageParameters(packageScope, "reqUsage21", types);

    types.put("mass", mass);
    validateRequirementUsageParameters(packageScope, "reqUsage23", types);

    types.remove("mass");
    types.put("vehicle", nullVehicle);
    types.put("engine", nullEngine);
    validateRequirementUsageParameters(packageScope, "reqUsage24", types);

    types.put("vehicle", nullCar);
    validateRequirementUsageParameters(packageScope, "reqUsage25", types);

    types.put("vehicle", nullVehicle);
    types.put("mass", mass);
    validateRequirementUsageParameters(packageScope, "reqUsage26", types);

    types.put("engine", nullTurboEngine);
    types.put("vehicle", vehicle_car1);
    validateRequirementUsageParameters(packageScope, "reqUsage27", types);

    types.remove("engine");
    types.remove("mass");
    types.put("vehicle", vehicle_vehicle1);
    validateRequirementUsageParameters(packageScope, "reqUsage28", types);

    types.put("vehicle", nullCar);
    validateRequirementUsageParameters(packageScope, "reqUsage29", types);

  }

  private void validateRequirementUsageParameters(ISysMLv2Scope scope, String reqUsage,
                                                  HashMap<String, Map.Entry<String, ASTExpression>> types
  ) {
    ASTBaseRequirement requirement = scope.resolveRequirementUsage(reqUsage).get().getAstNode();
    validateParameters(requirement.getParameters(), types);
  }

  private void validateRequirementDefinitionParameters(ISysMLv2Scope scope, String reqDef,
                                                       HashMap<String, Map.Entry<String, ASTExpression>> types
  ) {
    ASTBaseRequirement requirement = scope.resolveRequirementDef(reqDef).get().getAstNode();
    validateParameters(requirement.getParameters(), types);
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
      String error1 = Log.getFindings().get(0).toString();
      String error2 = Log.getFindings().get(1).toString();
      String error3 = Log.getFindings().get(2).toString();
      String error4 = Log.getFindings().get(3).toString();
      String error5 = Log.getFindings().get(4).toString();
      String error6 = Log.getFindings().get(5).toString();
      String error7 = Log.getFindings().get(6).toString();
      String error8 = Log.getFindings().get(7).toString();
      String error9 = Log.getFindings().get(8).toString();
      String error10 = Log.getFindings().get(9).toString();
      assertEquals(error1,
          "Requirement parameter 'vehicle' cannot be added as it is already present in the parameter list. "
              + "It is possible that duplicate parameters were added in the requirement parameter list.");
      assertEquals(error2,
          "Requirement parameter 'vehicle' has a type 'Car', but it is defined with a new type or assigned "
              + "a value of type 'Vehicle', which is not compatible!");
      assertEquals(error3,
          "Requirement parameter 'vehicle' was inherited multiple times, but the the parameters are not equal. "
              + "Only the same parameters can be inherited more than once.");
      assertEquals(error4,
          "Requirement parameter 'mass' has a type 'double', but it is assigned a value of type 'int', "
              + "which is not compatible!");
      assertEquals(error5,
          "Requirement parameter 'vehicle' cannot be added as it is already present in the parameter list. "
              + "It is possible that duplicate parameters were added in the requirement parameter list.");
      assertEquals(error6,
          "Requirement parameter 'vehicle' has a type 'Car', but it is assigned a value of type 'double', "
              + "which is not compatible!");
      assertEquals(error7,
          "Requirement parameter 'vehicle' has a type 'Car', but it is defined with a new type or assigned "
              + "a value of type 'Vehicle', which is not compatible!");
      assertEquals(error8,
          "Requirement parameter 'vehicle' was inherited multiple times, but the the parameters are not equal. "
              + "Only the same parameters can be inherited more than once.");
      assertEquals(error9,
          "Requirement parameter 'vehicle' has a type 'Car', but it is defined with a new type or assigned "
              + "a value of type 'Vehicle', which is not compatible!");
      assertEquals(error10,
          "Requirement parameter 'vehicle' has a type 'Car', but it is assigned a value of type 'Vehicle', "
              + "which is not compatible!");
    }
    finally {
      Log.clearFindings();
      Log.enableFailQuick(true);
    }
  }

}
