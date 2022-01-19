package de.monticore.lang.sysmlblockdiagrams._symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShadowingScopeTest {

  @BeforeAll
  public static void initMill() {
    SysMLv2Mill.init();
  }

  /**
   * Validates that the scope spanned by a part def is shadowing,
   * i.e. it can hide elements defined outside.
   *
   * @throws IOException
   */
  @Test
  public void partDef_ShadowingScopeTest() throws IOException {
    String model = "src/test/resources/sysmlblockdiagrams/_symboltable/ShadowingScopeOfPart.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    Optional<PartPropertySymbol> engineDefinedOutside = packageScope.resolvePartProperty("engine");
    assertTrue(engineDefinedOutside.isPresent());

    Optional<PartDefSymbol> partDef_Vehicle = packageScope.resolvePartDef("Vehicle");
    assertTrue(partDef_Vehicle.isPresent());

    Optional<PartPropertySymbol> engineDefinedInside = partDef_Vehicle.get().getSpannedScope().resolvePartProperty(
        "engine");
    assertTrue(engineDefinedInside.isPresent());

    assertNotEquals(engineDefinedOutside.get(), engineDefinedInside.get());
  }

  /**
   * Validates that the scope spanned by a part property is shadowing,
   * i.e. it can hide elements defined outside.
   *
   * @throws IOException
   */
  @Test
  public void partProperty_ShadowingScopeTest() throws IOException {
    String model = "src/test/resources/sysmlblockdiagrams/_symboltable/ShadowingScopeOfPart.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    Optional<PartPropertySymbol> engineDefinedOutside = packageScope.resolvePartProperty("engine");
    assertTrue(engineDefinedOutside.isPresent());

    Optional<PartPropertySymbol> partProperty_vehicle = packageScope.resolvePartProperty("vehicle");
    assertTrue(partProperty_vehicle.isPresent());

    Optional<PartPropertySymbol> engineDefinedInside = partProperty_vehicle.get().getSpannedScope().resolvePartProperty(
        "engine");
    assertTrue(engineDefinedInside.isPresent());

    assertNotEquals(engineDefinedOutside.get(), engineDefinedInside.get());
  }

  /**
   * Validates that the scope spanned by a port def is shadowing,
   * i.e. it can hide elements defined outside.
   *
   * @throws IOException
   */
  @Test
  public void portDef_ShadowingScopeTest() throws IOException {
    String model = "src/test/resources/sysmlblockdiagrams/_symboltable/ShadowingScopeOfPort.sysml";
    ASTSysMLModel ast = SysMLv2Mill.parser().parse(model).get();
    SysMLv2Mill.scopesGenitorDelegator().createFromAST(ast);
    ISysMLv2Scope packageScope = ast.getEnclosingScope().getSubScopes().get(0);

    Optional<FieldSymbol> massDefinedOutside = packageScope.resolveField("mass");
    assertTrue(massDefinedOutside.isPresent());

    Optional<SysMLPortDefSymbol> portDef_Port = packageScope.resolveSysMLPortDef("Port");
    assertTrue(portDef_Port.isPresent());

    Optional<FieldSymbol> massDefinedInside = portDef_Port.get().getSpannedScope().resolveField("mass");
    assertTrue(massDefinedInside.isPresent());

    assertNotEquals(massDefinedOutside.get(), massDefinedInside.get());
  }
}
