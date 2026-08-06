/* (c) https://github.com/MontiCore/monticore */
package cocos;

import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBooleanTC3;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;
import symboltable.NervigeSymboltableTests;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks that name references in requirements are resolved within the scope of the subject.
 */
public class RequirementSubjectTest extends NervigeSymboltableTests {

  @Test
  public void testValid() throws IOException {
    var as = process("part def S { attribute a: boolean; } requirement R { subject s: S; constraint t { s.a } }");
    var errors = check((ASTSysMLModel) as.getAstNode());
    assertThat(errors).hasSize(0);
  }

  @Test
  public void testInvalid() throws IOException {
    var as = process("part def S { attribute a: int; } requirement R { subject s: S; constraint t { s.a } }");
    var errors = check((ASTSysMLModel) as.getAstNode());
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0).getMsg()).contains("should be boolean");
  }

  @Test
  public void testInvalidImplizit() throws IOException {
    var as = process("part def S { attribute a: boolean; } requirement R { subject s: S; constraint t { a } }");
    var errors = check((ASTSysMLModel) as.getAstNode());
    assertThat(errors).hasSize(2);
    assertThat(errors.get(0).getMsg()).contains("0xFD118 could not find symbol");
    assertThat(errors.get(1).getMsg()).contains("0x80001 Failed to derive a type");
  }

  private List<Finding> check(ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new ConstraintIsBooleanTC3());
    Log.enableFailQuick(false);
    checker.checkAll(ast);
    return Log.getFindings().stream().filter(f -> f.isError()).collect(Collectors.toList());
  }

}
