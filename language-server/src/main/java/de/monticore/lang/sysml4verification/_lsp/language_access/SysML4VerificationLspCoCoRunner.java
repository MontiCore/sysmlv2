package de.monticore.lang.sysml4verification._lsp.language_access;

/*import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTConstraintUsageCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;*/
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.lang.sysml4verification._cocos.*;

public class SysML4VerificationLspCoCoRunner extends SysML4VerificationLspCoCoRunnerTOP {
  private final SysML4VerificationCoCoChecker runner = new SysML4VerificationCoCoChecker();

  public SysML4VerificationLspCoCoRunner(DocumentManager documentManager) {
    super(documentManager);
    // Register CoCos
    /*runner.addCoCo(new ConstraintExpressionTypeIsBooleanCoCo());
    runner.addCoCo(new ConstraintParameterTypeIsValidCoCo());
    runner.addCoCo((SysMLConstraintsASTConstraintUsageCoCo) new ParameterUsageIsValidCoCo());
    runner.addCoCo((SysMLConstraintsASTConstraintUsageCoCo) new ConstraintDefinitionIsPresentCoCo());*/
  }

  @Override
  public void runAllCoCos(ASTSysMLModel ast) {
    runner.checkAll(ast);
  }

}
