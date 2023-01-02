/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTConstraintDefCoCo;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTSysMLPackageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlrequirements._cocos.SysMLRequirementsASTRequirementDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.NameCompatible4Isabelle;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameCompatible4IsabelleTest {
  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool st = new SysMLv2Tool();

  private final String validPath = "src/test/resources/cocos/NameCompatible4Isabelle/0_valid.sysml";
  private final String invalidPath = "src/test/resources/cocos/NameCompatible4Isabelle/0_invalid.sysml";

  @BeforeAll
  public static void init(){
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset(){
    Log.getFindings().clear();
  }

  @Test
  public void testValidName() throws IOException {
    Optional<ASTSysMLModel> ast = parser.parse(validPath);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLStatesASTStateDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLPartsASTPartDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLPartsASTPortDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLConstraintsASTConstraintDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLActionsASTActionDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLRequirementsASTRequirementDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLImportsAndPackagesASTSysMLPackageCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLPartsASTAttributeDefCoCo) new NameCompatible4Isabelle());
      checker.checkAll(ast.get());
      assertTrue(Log.getFindings().isEmpty());
    }else {
      Assertions.fail("AST is not present");
    }
  }

  @Test
  public void testInvalidName() throws IOException{
    Optional<ASTSysMLModel> ast = parser.parse(invalidPath);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLStatesASTStateDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLPartsASTPartDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLPartsASTPortDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLConstraintsASTConstraintDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLActionsASTActionDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLRequirementsASTRequirementDefCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLImportsAndPackagesASTSysMLPackageCoCo) new NameCompatible4Isabelle());
      checker.addCoCo((SysMLPartsASTAttributeDefCoCo) new NameCompatible4Isabelle());
      checker.checkAll(ast.get());
      assertFalse(Log.getFindings().isEmpty());
    }else {
      Assertions.fail("AST is not present");
    }
  }
}
