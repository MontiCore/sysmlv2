/* (c) https://github.com/MontiCore/monticore */
package astrules;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
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
import de.monticore.lang.sysmlv2._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlv2._ast.ASTSysMLSubsetting;
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
    // Checks that attribute definition specializations are not rewritten.
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "attribute def Lebewesen; "
            + "attribute def Person :> Lebewesen;"
    );
    assertThat(ast).isPresent();

    ASTSysMLModel model = ast.get();
    ASTAttributeDef person = (ASTAttributeDef) model.getSysMLElement(1);
    List<ASTSpecialization> before = person.getSpecializationList();
    assertThat(before).hasSize(1);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    model.accept(traverser);

    List<ASTSpecialization> after = person.getSpecializationList();
    assertThat(after).hasSize(1);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(after.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);
  }

  @Test
  public void attributeUsageChange() throws IOException {
    // Checks that attribute usage specializations are rewritten to subsettings.
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "attribute def Person; "
            + "attribute alleMenschenDieserErde; "
            + "attribute p : Person :> alleMenschenDieserErde;"
    );
    assertThat(ast).isPresent();

    ASTSysMLModel model = ast.get();
    ASTAttributeUsage p = (ASTAttributeUsage) model.getSysMLElement(2);
    List<ASTSpecialization> before = p.getSpecializationList();
    assertThat(before).hasSize(2);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(before.get(1)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(1)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    model.accept(traverser);

    List<ASTSpecialization> after = p.getSpecializationList();
    assertThat(after).hasSize(2);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(after.get(1)).isInstanceOf(ASTSysMLSubsetting.class);
    assertThat(after.get(1)).isNotInstanceOf(ASTSysMLSpecialization.class);
  }

  @Test
  public void partDefNoChange() throws IOException {
    // Checks that part definition specializations are not rewritten.
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "part def Vehicle; "
            + "part def Car :> Vehicle;"
    );
    assertThat(ast).isPresent();

    ASTSysMLModel model = ast.get();
    ASTPartDef carDef = (ASTPartDef) model.getSysMLElement(1);
    List<ASTSpecialization> before = carDef.getSpecializationList();
    assertThat(before).hasSize(1);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    model.accept(traverser);

    List<ASTSpecialization> after = carDef.getSpecializationList();
    assertThat(after).hasSize(1);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(after.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);
  }

  @Test
  public void partUsageChange() throws IOException {
    // Checks that part usage specializations are rewritten to subsettings.
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "part def Car; "
            + "part fleetCar; "
            + "part car : Car :> fleetCar;"
    );
    assertThat(ast).isPresent();

    ASTSysMLModel model = ast.get();
    ASTPartUsage carUsage = (ASTPartUsage) model.getSysMLElement(2);
    List<ASTSpecialization> before = carUsage.getSpecializationList();
    assertThat(before).hasSize(2);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(before.get(1)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(1)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    model.accept(traverser);

    List<ASTSpecialization> after = carUsage.getSpecializationList();
    assertThat(after).hasSize(2);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(after.get(1)).isInstanceOf(ASTSysMLSubsetting.class);
    assertThat(after.get(1)).isNotInstanceOf(ASTSysMLSpecialization.class);
  }

  @Test
  public void itemDefNoChange() throws IOException {
    // Checks that item definition specializations are not rewritten.
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "item def Payload; "
            + "item def SensorData :> Payload;"
    );
    assertThat(ast).isPresent();

    ASTSysMLModel model = ast.get();
    ASTItemDef dataDef = (ASTItemDef) model.getSysMLElement(1);
    List<ASTSpecialization> before = dataDef.getSpecializationList();
    assertThat(before).hasSize(1);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    model.accept(traverser);

    List<ASTSpecialization> after = dataDef.getSpecializationList();
    assertThat(after).hasSize(1);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(after.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);
  }

  @Test
  public void itemUsageChange() throws IOException {
    // Checks that item usage specializations are rewritten to subsettings.
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "item def SensorData; "
            + "item storedPayload; "
            + "item data : SensorData :> storedPayload;"
    );
    assertThat(ast).isPresent();

    ASTSysMLModel model = ast.get();
    ASTItemUsage dataUsage = (ASTItemUsage) model.getSysMLElement(2);
    List<ASTSpecialization> before = dataUsage.getSpecializationList();
    assertThat(before).hasSize(2);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(before.get(1)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(1)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    model.accept(traverser);

    List<ASTSpecialization> after = dataUsage.getSpecializationList();
    assertThat(after).hasSize(2);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(after.get(1)).isInstanceOf(ASTSysMLSubsetting.class);
    assertThat(after.get(1)).isNotInstanceOf(ASTSysMLSpecialization.class);
  }

  @Test
  public void portDefNoChange() throws IOException {
    // Checks that port definition specializations are not rewritten.
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "port def InterfaceEnd; "
            + "port def DataPort :> InterfaceEnd;"
    );
    assertThat(ast).isPresent();

    ASTSysMLModel model = ast.get();
    ASTPortDef dataPortDef = (ASTPortDef) model.getSysMLElement(1);
    List<ASTSpecialization> before = dataPortDef.getSpecializationList();
    assertThat(before).hasSize(1);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    model.accept(traverser);

    List<ASTSpecialization> after = dataPortDef.getSpecializationList();
    assertThat(after).hasSize(1);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(after.get(0)).isNotInstanceOf(ASTSysMLSubsetting.class);
  }

  @Test
  public void portUsageChange() throws IOException {
    // Checks that port usage specializations are rewritten to subsettings.
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "port def DataPort; "
            + "port externalPort; "
            + "port dataPort : DataPort :> externalPort;"
    );
    assertThat(ast).isPresent();

    ASTSysMLModel model = ast.get();
    ASTPortUsage dataPortUsage = (ASTPortUsage) model.getSysMLElement(2);
    List<ASTSpecialization> before = dataPortUsage.getSpecializationList();

    assertThat(before).hasSize(2);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(before.get(1)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(1)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(visitor);
    model.accept(traverser);

    List<ASTSpecialization> after = dataPortUsage.getSpecializationList();

    assertThat(after).hasSize(2);
    assertThat(after.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(after.get(1)).isInstanceOf(ASTSysMLSubsetting.class);
    assertThat(after.get(1)).isNotInstanceOf(ASTSysMLSpecialization.class);
  }

}
