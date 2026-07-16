/* (c) https://github.com/MontiCore/monticore */
package astrules;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
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
  public void shouldKeepRealSpecializationOfDefinition() throws IOException {
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "attribute def Lebewesen; "
            + "attribute def Person :> Lebewesen;"
    );

    assertThat(ast).isPresent();

    ASTAttributeDef person = (ASTAttributeDef) ast.get().getSysMLElement(1);

    assertThat(person.getSpecializationList()).hasSize(1);
    assertThat(person.getSpecialization(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(person.getSpecialization(0)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.traverser();
    traverser.add4SysMLParts(visitor);
    ast.get().accept(traverser);

    assertThat(person.getSpecializationList()).hasSize(1);
    assertThat(person.getSpecialization(0)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(person.getSpecialization(0)).isNotInstanceOf(ASTSysMLSubsetting.class);
  }

  @Test
  public void shouldRewriteWrongSpecializationOfUsageToSubsetting() throws IOException {
    Optional<ASTSysMLModel> ast = SysMLv2Mill.parser().parse_String(
        "attribute def Person; "
            + "attribute alleMenschenDieserErde; "
            + "attribute p : Person :> alleMenschenDieserErde;"
    );

    assertThat(ast).isPresent();

    ASTAttributeUsage p = (ASTAttributeUsage) ast.get().getSysMLElement(2);

    List<ASTSpecialization> before = p.getSpecializationList();

    // Parser-Verhalten ohne Visitor:
    // ": Person" ist korrekt ein Typing.
    // ":> alleMenschenDieserErde" wird wegen der Grammatik zunächst falsch als Specialization geparst.
    assertThat(before).hasSize(2);
    assertThat(before.get(0)).isInstanceOf(ASTSysMLTyping.class);
    assertThat(before.get(1)).isInstanceOf(ASTSysMLSpecialization.class);
    assertThat(before.get(1)).isNotInstanceOf(ASTSysMLSubsetting.class);

    SpecializationToSubsettingVisitor visitor =
        new SpecializationToSubsettingVisitor();
    var traverser = SysMLv2Mill.traverser();
    traverser.add4SysMLParts(visitor);
    ast.get().accept(traverser);

    List<ASTSpecialization> after = p.getSpecializationList();

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
