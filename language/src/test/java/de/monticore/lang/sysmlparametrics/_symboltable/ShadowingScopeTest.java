package de.monticore.lang.sysmlparametrics._symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ShadowingScopeTest {

  @BeforeClass
  public static void initMill() {
    SysMLv2Mill.init();
  }

  /**
   * Validates that the scope spanned by a constraint def is shadowing,
   * i.e. it can hide elements defined outside.
   *
   * @throws IOException
   */
  @Test
  public void constraintDef_ShadowingScopeTest() throws IOException {
    String model = "src/test/resources/sysmlparametrics/_symboltable/ShadowingScopeOfConstraint.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    Optional<FieldSymbol> vehicleMassDefinedOutside = packageScope.resolveField("vehicleMass");
    assertTrue(vehicleMassDefinedOutside.isPresent());

    Optional<ConstraintDefSymbol> constraintDef_VehicleMaxMass = packageScope.resolveConstraintDef("VehicleMaxMass");
    assertTrue(constraintDef_VehicleMaxMass.isPresent());

    Optional<FieldSymbol> vehicleMassDefinedInside = constraintDef_VehicleMaxMass.get().getSpannedScope().resolveField(
        "vehicleMass");
    assertTrue(vehicleMassDefinedInside.isPresent());

    assertNotEquals(vehicleMassDefinedOutside.get(), vehicleMassDefinedInside.get());
  }

  /**
   * Validates that the scope spanned by a constraint usage is shadowing,
   * i.e. it can hide elements defined outside.
   *
   * @throws IOException
   */
  @Test
  public void constraintUsage_ShadowingScopeTest() throws IOException {
    String model = "src/test/resources/sysmlparametrics/_symboltable/ShadowingScopeOfConstraint.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    Optional<FieldSymbol> vehicleMassDefinedOutside = packageScope.resolveField("vehicleMass");
    assertTrue(vehicleMassDefinedOutside.isPresent());

    Optional<ConstraintUsageSymbol> constraintUsage_vehicleMaxMass = packageScope.resolveConstraintUsage(
        "vehicleMaxMass");
    assertTrue(constraintUsage_vehicleMaxMass.isPresent());

    Optional<FieldSymbol> vehicleMassDefinedInside = constraintUsage_vehicleMaxMass.get().getSpannedScope().resolveField(
        "vehicleMass");
    assertTrue(vehicleMassDefinedInside.isPresent());

    assertNotEquals(vehicleMassDefinedOutside.get(), vehicleMassDefinedInside.get());
  }
}
