package parser;

import de.monticore.lang.kerml.KerMLMill;
import de.monticore.lang.kerml.KerMLTool;
import de.monticore.lang.kermlelements._ast.ASTDatatype;
import de.monticore.lang.kermlelements._ast.ASTFeature;
import de.monticore.lang.kermlelements._ast.ASTKerMLTyping;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testet den Parser mit einfachen, handgeschriebenen Beispielen von uns bevor
 * es weitergeht mit dem Library-Modellen aus der KerML-Library.
 */
public class KerMLParserTest {

  @Test
  public void testParseCompositeFeature() throws IOException {
    LogStub.init();
    new KerMLTool().init();
    var parser = KerMLMill.parser();
    var ast = parser.parse_String(
        "derived composite var feature ownedAnnotatingRelationship : Annotation[0..*] "
            + "ordered subsets annotation, ownedRelationship;");

    assertThat(parser.hasErrors()).isFalse();
    assertThat(ast).isPresent();
    assertThat(ast.get().getKerMLElement(0)).isInstanceOf(ASTFeature.class);
    var feature = (ASTFeature) ast.get().getKerMLElement(0);
    assertThat(feature.getName()).isEqualTo("ownedAnnotatingRelationship");
  }

  @Test
  public void testParseCompositeStep() throws IOException {
    LogStub.init();
    new KerMLTool().init();
    var parser = KerMLMill.parser();
    var ast = parser.parse_String("composite step action;");

    assertThat(parser.hasErrors()).isFalse();
    assertThat(ast).isPresent();
    assertThat(ast.get().getKerMLElement(0))
        .isInstanceOf(de.monticore.lang.kermlelements._ast.ASTStep.class);
  }

  @Test
  public void testParseDatatype() throws IOException {
    LogStub.init();
    var tool = new KerMLTool();
    tool.init();

    var ast = KerMLMill.parser()
        .parse_String("datatype Test { feature a: Anything; }");

    assertThat(ast).isPresent();
    assertThat(ast.get().getKerMLElementList()).hasSize(1);
    assertThat(ast.get().getKerMLElement(0)).isInstanceOf(ASTDatatype.class);

    var datatype = (ASTDatatype) ast.get().getKerMLElement(0);
    assertThat(datatype.getName()).isEqualTo("Test");
    assertThat(datatype.getKerMLElementList()).hasSize(1);
    assertThat(datatype.getKerMLElement(0)).isInstanceOf(ASTFeature.class);

    var feature = (ASTFeature) datatype.getKerMLElement(0);
    assertThat(feature.getName()).isEqualTo("a");
    assertThat(feature.getSpecializationList()).hasSize(1);
    assertThat(feature.getSpecialization(0)).isInstanceOf(ASTKerMLTyping.class);
    var typing = (ASTKerMLTyping) feature.getSpecialization(0);
    assertThat(typing.getSuperTypesList()).hasSize(1);
    assertThat(typing.getSuperTypes(0).printType()).isEqualTo("Anything");
  }
}
