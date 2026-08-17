package symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ScalarValuesSymbolTableTest {

  private SysMLv2Tool tool;

  @BeforeEach
  public void setUp() {
    LogStub.init();
    LogStub.enableFailQuick(false);

    tool = new SysMLv2Tool();
    tool.init();

    MCPath symbolPath = new MCPath();
    symbolPath.addEntry(Paths.get("src/test/resources/symbols"));
    SysMLv2Mill.globalScope().setSymbolPath(symbolPath);
  }

  @Test
  public void testResolveScalarValuesBoolean() throws IOException {
    String model = "attribute b: ScalarValues::Boolean;";

    Optional<ASTSysMLModel> optAst = SysMLv2Mill.parser().parse_String(model);
    assertThat(optAst).isPresent();
    ASTSysMLModel ast = optAst.get();

    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);
    tool.finalizeSymbolTable(ast);

    assertThat(Log.getErrorCount()).isEqualTo(0);

    Optional<TypeSymbol> resolvedType = SysMLv2Mill.globalScope().resolveType("ScalarValues.Boolean");
    assertThat(resolvedType).isPresent();
    assertThat(resolvedType.get().getName()).isEqualTo("Boolean");
    assertThat(resolvedType.get().getFullName()).isEqualTo("ScalarValues.Boolean");

    var attr = (ASTAttributeUsage) ast.getSysMLElement(0);
    var superType = attr.getSpecialization(0).getSuperTypes(0);
    var localScope = (ISysMLv2Scope) superType.getEnclosingScope();
    assertThat(localScope.resolveType("ScalarValues.Boolean")).isPresent();
  }
}
