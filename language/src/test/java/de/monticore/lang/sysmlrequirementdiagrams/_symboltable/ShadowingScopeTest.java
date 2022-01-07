package de.monticore.lang.sysmlrequirementdiagrams._symboltable;

import de.monticore.lang.sysmlblockdiagrams._symboltable.PartPropertySymbol;
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
   * Validates that the scope spanned by a requirement def is shadowing,
   * i.e. it can hide elements defined outside.
   *
   * @throws IOException
   */
  @Test
  public void requirementDef_ShadowingScopeTest() throws IOException {
    String model = "src/test/resources/sysmlrequirementdiagrams/_symboltable/ShadowingScopeOfRequirement.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    Optional<FieldSymbol> vehicleMaxMassDefinedOutside = packageScope.resolveField("vehicleMaxMass");
    assertTrue(vehicleMaxMassDefinedOutside.isPresent());

    Optional<RequirementDefSymbol> reqDef_VehicleMaxMassReq = packageScope.resolveRequirementDef("VehicleMaxMassRequirement");
    assertTrue(reqDef_VehicleMaxMassReq.isPresent());

    Optional<FieldSymbol> vehicleMaxMassDefinedInside = reqDef_VehicleMaxMassReq.get().getSpannedScope().resolveField("vehicleMaxMass");
    assertTrue(vehicleMaxMassDefinedInside.isPresent());

    assertNotEquals(vehicleMaxMassDefinedOutside.get(), vehicleMaxMassDefinedInside.get());
  }

  /**
   * Validates that the scope spanned by a requirement usage is shadowing,
   * i.e. it can hide elements defined outside.
   *
   * @throws IOException
   */
  @Test
  public void requirementUsage_ShadowingScopeTest() throws IOException {
    String model = "src/test/resources/sysmlrequirementdiagrams/_symboltable/ShadowingScopeOfRequirement.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    Optional<FieldSymbol> vehicleMaxMassDefinedOutside = packageScope.resolveField("vehicleMaxMass");
    assertTrue(vehicleMaxMassDefinedOutside.isPresent());

    Optional<RequirementUsageSymbol> reqUsage_vehicleMaxMassReq = packageScope.resolveRequirementUsage("vehicleMaxMassRequirement");
    assertTrue(reqUsage_vehicleMaxMassReq.isPresent());

    Optional<FieldSymbol> vehicleMaxMassDefinedInside = reqUsage_vehicleMaxMassReq.get().getSpannedScope().resolveField("vehicleMaxMass");
    assertTrue(vehicleMaxMassDefinedInside.isPresent());

    assertNotEquals(vehicleMaxMassDefinedOutside.get(), vehicleMaxMassDefinedInside.get());
  }
}
