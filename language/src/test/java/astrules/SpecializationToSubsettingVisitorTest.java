/* (c) https://github.com/MontiCore/monticore */
package astrules;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLUsage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTItemDef;
import de.monticore.lang.sysmlparts._ast.ASTItemUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2.visitors.SpecializationToSubsettingVisitor;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SpecializationToSubsettingVisitorTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @Test
  public void attributeDefNoChange() throws IOException {
    ASTSysMLModel ast = parse(
        "attribute def Lebewesen; "
            + "attribute def Person :> Lebewesen;"
    );

    ASTAttributeDef person = (ASTAttributeDef) ast.getSysMLElement(1);
    assertDefinitionSpecialization(person.getSpecializationList());
    applyVisitor(ast);
    assertDefinitionSpecialization(person.getSpecializationList());
  }

  @Test
  public void attributeUsageChange() throws IOException {
    ASTSysMLModel ast = parse(
        "attribute def Person; "
            + "attribute alleMenschenDieserErde; "
            + "attribute p : Person :> alleMenschenDieserErde;"
    );

    ASTAttributeUsage p = (ASTAttributeUsage) ast.getSysMLElement(2);
    assertUsageBeforeRewrite(p);
    applyVisitor(ast);
    assertUsageAfterRewrite(p);
  }

  @Test
  public void partDefNoChange() throws IOException {
    ASTSysMLModel ast = parse(
        "part def Vehicle; "
            + "part def Car :> Vehicle;"
    );

    ASTPartDef carDef = (ASTPartDef) ast.getSysMLElement(1);
    assertDefinitionSpecialization(carDef.getSpecializationList());
    applyVisitor(ast);
    assertDefinitionSpecialization(carDef.getSpecializationList());
  }

  @Test
  public void partUsageChange() throws IOException {
    ASTSysMLModel ast = parse(
        "part def Car; "
            + "part fleetCar; "
            + "part car : Car :> fleetCar;"
    );

    ASTPartUsage carUsage = (ASTPartUsage) ast.getSysMLElement(2);
    assertUsageBeforeRewrite(carUsage);
    applyVisitor(ast);
    assertUsageAfterRewrite(carUsage);
  }

  @Test
  public void itemDefNoChange() throws IOException {
    ASTSysMLModel ast = parse(
        "item def Payload; "
            + "item def SensorData :> Payload;"
    );

    ASTItemDef dataDef = (ASTItemDef) ast.getSysMLElement(1);
    assertDefinitionSpecialization(dataDef.getSpecializationList());
    applyVisitor(ast);
    assertDefinitionSpecialization(dataDef.getSpecializationList());
  }

  @Test
  public void itemUsageChange() throws IOException {
    ASTSysMLModel ast = parse(
        "item def SensorData; "
            + "item storedPayload; "
            + "item data : SensorData :> storedPayload;"
    );

    ASTItemUsage dataUsage = (ASTItemUsage) ast.getSysMLElement(2);
    assertUsageBeforeRewrite(dataUsage);
    applyVisitor(ast);
    assertUsageAfterRewrite(dataUsage);
  }

  @Test
  public void portDefNoChange() throws IOException {
    ASTSysMLModel ast = parse(
        "port def InterfaceEnd; "
            + "port def DataPort :> InterfaceEnd;"
    );

    ASTPortDef dataPortDef = (ASTPortDef) ast.getSysMLElement(1);
    assertDefinitionSpecialization(dataPortDef.getSpecializationList());
    applyVisitor(ast);
    assertDefinitionSpecialization(dataPortDef.getSpecializationList());
  }

  @Test
  public void portUsageChange() throws IOException {
    ASTSysMLModel ast = parse(
        "port def DataPort; "
            + "port externalPort; "
            + "port dataPort : DataPort :> externalPort;"
    );

    ASTPortUsage dataPortUsage = (ASTPortUsage) ast.getSysMLElement(2);
    assertUsageBeforeRewrite(dataPortUsage);
    applyVisitor(ast);
    assertUsageAfterRewrite(dataPortUsage);
  }

  private static ASTSysMLModel parse(String model) throws IOException {
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(model);
    assertThat(ast).isPresent();
    return ast.get();
  }

  private static void applyVisitor(ASTSysMLModel ast) {
    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    ast.accept(traverser);
  }

  private static void assertDefinitionSpecialization(
      List<ASTSpecialization> specializations) {
    assertThat(specializations).hasSize(1);
    assertThat(specializations.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(specializations.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);
  }

  private static void assertUsageBeforeRewrite(ASTSysMLUsage usage) {
    List<ASTSpecialization> before = usage.getSpecializationList();

    // Parser-Verhalten ohne Visitor:
    // ": Type" ist korrekt ein Typing.
    // ":> baseUsage" wird wegen der Grammatik zunächst falsch als Specialization geparst.
    assertThat(before).hasSize(2);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(before.get(1)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(1)).isNotInstanceOf(ASTSysMLSubsetting.class);
  }

  private static void assertUsageAfterRewrite(ASTSysMLUsage usage) {
    List<ASTSpecialization> after = usage.getSpecializationList();

    // Nach dem Visitor:
    // Typing bleibt erhalten.
    // Die falsche Specialization ist weg.
    // Stattdessen gibt es genau ein Subsetting.
    assertThat(after).hasSize(2);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(after.get(1)).isInstanceOf(ASTSysMLSubsetting.class);
    assertThat(after.get(1)).isNotInstanceOf(ASTSysMLSpecialization.class);
  }

}
