/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTConstraintDefCoCo;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTRequirementDefCoCo;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTSysMLPackageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.NameCompatible4Isabelle;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameCompatible4IsabelleTest {
  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool st = new SysMLv2Tool();

  @BeforeAll
  public static void init() {
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset() {
    Log.getFindings().clear();
  }

  @ParameterizedTest()
  @ValueSource(strings = {
    "state def a123{}",
    "part def a222{}",
    "port def a33{}",
    "package aA4{}",
    "attribute def aR5{}",
    "action def aaaR{}",
    "constraint def AB{true}",
    "requirement def Ab{}"
  })
  public void testValidName(String model) throws IOException {
    Optional<ASTSysMLModel> ast = parser.parse_String(model);
    assertThat(ast).isPresent();

    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPartDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPortDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLConstraintsASTConstraintDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLActionsASTActionDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLConstraintsASTRequirementDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLImportsAndPackagesASTSysMLPackageCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTAttributeDefCoCo) new NameCompatible4Isabelle());
    checker.checkAll(ast.get());
    assertTrue(Log.getFindings().isEmpty());
  }

  @ParameterizedTest()
  @ValueSource(strings = {
      "state def $ {}",
      "part def $ {}",
      "port def a$ {}",
      "package _9$ {}",
      "attribute def $$$ {}",
      "action def $a$ {}",
      "constraint def a$1 {true}",
      "requirement def _$ {}"
  })
  public void testInvalidName(String model) throws IOException{
    Optional<ASTSysMLModel> ast = parser.parse_String(model);
    assertThat(ast).isPresent();

    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPartDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPortDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLConstraintsASTConstraintDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLActionsASTActionDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLConstraintsASTRequirementDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLImportsAndPackagesASTSysMLPackageCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTAttributeDefCoCo) new NameCompatible4Isabelle());
    checker.checkAll(ast.get());

    assertThat(Log.getFindings()).isNotEmpty();
    assertThat(Log.getFindings()).allMatch(f -> f.getMsg().contains("0xFF005"));
  }
}
