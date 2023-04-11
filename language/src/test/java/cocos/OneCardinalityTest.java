/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTDefSpecializationCoCo;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTUsageSpecializationCoCo;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2.cocos.OneCardinality;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OneCardinalityTest {
  private final SysMLv2Parser parser = new SysMLv2Parser();
  private final SysMLv2Tool st = new SysMLv2Tool();

  @BeforeAll
  public static void init(){
    LogStub.init();
    SysMLv2Mill.init();
  }

  @BeforeEach
  public void reset(){
    Log.getFindings().clear();
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port A: B;",
      "port A: B[1];",
      "port A: B[1..1];",
      "port A: B, C[2];"
  })
  void testValidCardinalityAndNoErrors(String candidate) throws IOException {
    var ast = parser.parse_String(candidate);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLBasisASTUsageSpecializationCoCo) new OneCardinality());
      checker.addCoCo((SysMLBasisASTDefSpecializationCoCo) new OneCardinality());
      checker.checkAll(ast.get());
      assertTrue(Log.getFindings().isEmpty());
    }else {
      Assertions.fail("AST is not present");
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "port A: B[*];",
      "port A: B[1..*];",
      "port A: B[1..2];",
      "port A: B, C[1..2];",
      "port A: B, C[8..2];"
  })
  void testInvalidCardinalityAndAssertErrors(String candidate) throws IOException {
    var ast = parser.parse_String(candidate);
    if(ast.isPresent()) {
      st.createSymbolTable(ast.get());
      SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
      checker.addCoCo((SysMLBasisASTUsageSpecializationCoCo) new OneCardinality());
      checker.addCoCo((SysMLBasisASTDefSpecializationCoCo) new OneCardinality());
      checker.checkAll(ast.get());
      assertFalse(Log.getFindings().isEmpty());
    }else {
      Assertions.fail("AST is not present");
    }
  }
}
