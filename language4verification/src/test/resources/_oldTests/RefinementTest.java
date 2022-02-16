package schrotttests;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._cocos.RefinementParameter;
import de.monticore.lang.sysml4verification._cocos.SysML4VerificationCoCoChecker;
import de.monticore.lang.sysml4verification._symboltable.SysML4VerificationSymbolTableCreator;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *  Tests if refinements work the way they should.
 */
public class RefinementTest {

  @BeforeAll
  public static void initMill() {SysML4VerificationMill.init();
    // TODO the basic types should always be in the scope when creating a symboltable
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
    Log.init();}

  @BeforeEach
  public void init() {
    Log.clearFindings();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Constraints",
      "RefinementsInvalid",
      "Requirements",
      "Statemachines"
  })
  public void parserTest(String model) throws IOException {
    Optional<ASTUnit> optAst = SysML4VerificationMill.parser().parse(
        "src/test/resources/refinement/" + model + ".sysml");
    assertThat(optAst).isNotEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "Statemachines",
      "Constraints",
      "Requirements"
  })
  public void parameterTypeCheck(String model) throws IOException {
    Optional<ASTUnit> optAst = SysML4VerificationMill.parser().parse(
        "src/test/resources/refinement/" + model + ".sysml");
    assertThat(optAst).isNotEmpty();
    SysML4VerificationSymbolTableCreator creator = new SysML4VerificationSymbolTableCreator();
    creator.createSymboltable(List.of(optAst.get()), new ModelPath());
    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo(new RefinementParameter());
    checker.checkAll(optAst.get());
  }

  @Test
  public void parameterInvalidTypeCheck() throws IOException {
    Log.enableFailQuick(false);
    Log.clearFindings();
    Optional<ASTUnit> optAst = SysML4VerificationMill.parser().parse(
        "src/test/resources/refinement/" + "RefinementsInvalid" + ".sysml");
    assertThat(optAst).isNotEmpty();
    SysML4VerificationSymbolTableCreator creator = new SysML4VerificationSymbolTableCreator();
    creator.createSymboltable(List.of(optAst.get()), new ModelPath());
    SysML4VerificationCoCoChecker checker = new SysML4VerificationCoCoChecker();
    checker.addCoCo(new RefinementParameter());
    checker.checkAll(optAst.get());

    // There are four wrong refinements in RefinementsInvalid.sysml, so we can expect at least 4 errors
    assertThat(Log.getFindingsCount()).isGreaterThan(4);
  }
}
