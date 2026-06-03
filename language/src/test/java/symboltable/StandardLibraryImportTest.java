package symboltable;

import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class StandardLibraryImportTest {
  @Test()
  public void testFQNResolving() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var model = "attribute a: ScalarValues::Complex;";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var type = ((ASTAttributeUsage) ast.getSysMLElement(0)).getSpecialization(0).getSuperTypes(0);

    type.printType();

    assertThat(type.printType()).isEqualTo("ScalarValues.Complex");
    assertThat(((ISysMLv2Scope)type.getEnclosingScope()).resolveType(type.printType())).isPresent();
  }

  @Test()
  public void testImportResolving() throws IOException {
    LogStub.init();
    var tool = new SysMLv2Tool();
    tool.init();

    var model = "private import Collections::Bag; attribute a: Bag;";

    var ast = SysMLv2Mill.parser().parse_String(model).get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    var type = ((ASTAttributeUsage) ast.getSysMLElement(1)).getSpecialization(0).getSuperTypes(0);

    assertThat(type.printType()).isEqualTo("Bag");
    assertThat(((ISysMLv2Scope)type.getEnclosingScope()).resolveType(type.printType())).isPresent();
    assertThat(((ISysMLv2Scope)type.getEnclosingScope()).resolveType(type.printType()).get().getFullName()).isEqualTo("Collections.Bag");
  }
}
