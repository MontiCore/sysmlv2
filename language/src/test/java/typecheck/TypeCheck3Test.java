package typecheck;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeVisitor;
import de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.ocl.oclexpressions.types3.OCLExpressionsTypeVisitor;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TypeCheck3Test {

  String MODEL = "attribute a: Stream<int>; constraint { a.len() > 5 }";

  class MyPrinter implements CommonExpressionsVisitor2 {
    String content = "";

    @Override
    public void visit(ASTFieldAccessExpression node) {
      var type = TypeCheck3.typeOf(node);

      // Wie prüfe ich, dass es sich um Aufruf der length-Funktion handelt?
      if(type.equals("len")) {
        content = "Übersetzung der length-Funktion";
      }
    }
  }

  @Disabled
  @Test
  void testTypeCheck3() throws IOException {
    /*
     * Initialisiere Sprache
     */
    var tool = new SysMLv2Tool();
    // Lädt u.A. die Streams aus der JAR
    tool.init();

    /*
     * TypeCheck3 Setup. Von hier übernommen und and SysML angepasst:
     * https://monticore.github.io/monticore/monticore-grammar/src/main/java/de/monticore/types3/TypeSystem3#how-to-initialize-the-type-visitors
     */
    var type4Ast = new Type4Ast();
    var typeTraverser = SysMLv2Mill.traverser();

    var forLiterals = new MCCommonLiteralsTypeVisitor();
    forLiterals.setType4Ast(type4Ast);
    typeTraverser.add4MCCommonLiterals(forLiterals);

    var forCommon = new CommonExpressionsTypeVisitor();
    forCommon.setType4Ast(type4Ast);
    typeTraverser.add4CommonExpressions(forCommon);

    var forOcl = new OCLExpressionsTypeVisitor();
    forOcl.setType4Ast(type4Ast);
    typeTraverser.add4OCLExpressions(forOcl);

    var forBasicTypes = new MCBasicTypesTypeVisitor();
    forBasicTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCBasicTypes(forBasicTypes);

    var forStreams = new StreamExpressionsTypeVisitor();
    forStreams.setType4Ast(type4Ast);
    typeTraverser.add4StreamExpressions(forStreams);

    new MapBasedTypeCheck3(typeTraverser, type4Ast).setThisAsDelegate();

    /*
     * Lasse den Visitor auf das Modell los
     */
    var ast = SysMLv2Mill.parser().parse_String(MODEL).get();
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var traverser = SysMLv2Mill.traverser();
    var printer = new MyPrinter();
    traverser.add4CommonExpressions(printer);
    ast.accept(traverser);

    /*
     * Erwartetes Resultat
     */
    assertThat(printer.content).isEqualTo("Übersetzung der length-Funktion");
  }

  @Test
  public void testConditionalAnd2Type() throws IOException {
    var tool = new SysMLv2Tool();
    Log.getFindings().clear();
    tool.init();

    var astExp = SysMLv2Mill.parser().parse_String("constraint { true & false }").get();
    tool.createSymbolTable(astExp);
    tool.completeSymbolTable(astExp);

    SymTypeExpression type = TypeCheck3.typeOf(((ASTConstraintUsage)astExp.getSysMLElement(0)).getExpression());

    assertThat(Log.getFindings().isEmpty()).isTrue();
    assertThat(type.isPrimitive()).isTrue();
  }

  @Test
  public void testConditionalAnd2TypeExpressionOnly() throws IOException {
    var tool = new SysMLv2Tool();
    Log.getFindings().clear();
    tool.init();

    var astExp = SysMLv2Mill.parser().parse_StringExpression("true & false").get();

    SymTypeExpression type = TypeCheck3.typeOf(astExp);

    assertThat(Log.getFindings().isEmpty()).isTrue();
    assertThat(type.isPrimitive()).isTrue();
  }

}
