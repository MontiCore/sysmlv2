package cocos;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.AssignActionTypeCheck3;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBooleanTC3;
import de.monticore.lang.sysmlv2.cocos.SendActionTypeCheck3;
import de.monticore.lang.sysmlv2.cocos.SpecializationExistsTC3;
import de.monticore.lang.sysmlv2.cocos.TypeCheck3TransitionGuards;
import de.monticore.lang.sysmlv2.types3.SysMLTypeCheck3;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ReproduceTC3FindingsTest {

  private static final String MODEL_PATH = "src/test/resources/cocos/repro";

  private SysMLv2Tool tool;

  @BeforeAll public static void init() {
    LogStub.init();
  }

  @BeforeEach public void reset() {
    Log.getFindings().clear();
    SysMLv2Mill.reset();
    SysMLv2Mill.init();
    SysMLv2Mill.prepareGlobalScope();

    tool = new SysMLv2Tool();
    tool.init();
    //SysMLTypeCheck3.init();
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(strings = {
      "model1.sysml",
      "model2.sysml",
      "model3.sysml",
      "model4.sysml",
      "model5.sysml",
      "model6.sysml",
      "model7.sysml",
      "model8.sysml",
      "model9.sysml",
  })
  public void reproducingErrors(String modelName) throws IOException {
    Path file = Paths.get(MODEL_PATH, modelName);
    System.out.println("Testing reproducing model: " + modelName);

    Optional<ASTSysMLModel> optAst = SysMLv2Mill.parser().parse(file.toString());
    assertThat(optAst).isPresent();
    ASTSysMLModel model = optAst.get();

    tool.createSymbolTable(model);
    tool.completeSymbolTable(model);
    tool.finalizeSymbolTable(model);


    var constraintIsBooleanChecker = new SysMLv2CoCoChecker();
    constraintIsBooleanChecker.addCoCo(new ConstraintIsBooleanTC3());

    var specializationExistsTC3Checker = new SysMLv2CoCoChecker();
    specializationExistsTC3Checker.addCoCo(new SpecializationExistsTC3());

    var typeCheck3TransitionGuardsChecker = new SysMLv2CoCoChecker();
    typeCheck3TransitionGuardsChecker.addCoCo(new TypeCheck3TransitionGuards());

    var sendActionTypeCheck3Checker = new SysMLv2CoCoChecker();
    sendActionTypeCheck3Checker.addCoCo(new SendActionTypeCheck3());

    var assignActionTypeCheck3Checker = new SysMLv2CoCoChecker();
    assignActionTypeCheck3Checker.addCoCo(new AssignActionTypeCheck3());

    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBooleanTC3());
    checker.addCoCo(new SpecializationExistsTC3());
    checker.addCoCo(new TypeCheck3TransitionGuards());
    checker.addCoCo(new SendActionTypeCheck3());
    checker.addCoCo(new AssignActionTypeCheck3());


    // checkCoCo(model, typeCheck3TransitionGuardsChecker, "TypeCheck3TransitionGuards", modelName);
    // checkCoCo(model, sendActionTypeCheck3Checker, "SendActionTypeCheck3", modelName);
    // checkCoCo(model, assignActionTypeCheck3Checker, "AssignActionTypeCheck3", modelName);
    // checkCoCo(model, constraintIsBooleanChecker, "ConstraintIsBooleanTC3",modelName);
    // checkCoCo(model, specializationExistsTC3Checker, "SpecializationExistsTC3", modelName);

    checkCoCo(model, checker, "All TC3 CoCos", modelName);
  }

  private void checkCoCo(ASTSysMLModel model, SysMLv2CoCoChecker coCoChecker, String cocoName, String modelName) {

    Log.clearFindings();
    try {
      coCoChecker.checkAll(model);
    } catch (Exception e) {
      System.out.println("[Error] CoCo " + cocoName +
          " error caused by " + modelName +
          ": " + e.getMessage());
      return;
    }
    //new findings
    if (!Log.getFindings().isEmpty()) {
      System.out.println("[FAIL] CoCo " + cocoName +
          " found " + Log.getFindings().size() +
          " issues in " + modelName);
      Log.getFindings().forEach(f -> System.out.println("[LOG]     " + f));
    } else {
      System.out.println("[PASS] CoCo " + cocoName);
    }
  }
}
