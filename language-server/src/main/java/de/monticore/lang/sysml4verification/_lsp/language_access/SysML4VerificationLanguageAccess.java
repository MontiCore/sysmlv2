package de.monticore.lang.sysml4verification._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.lang.sysml4verification._symboltable.SysML4VerificationGlobalScope;
import de.monticore.lang.sysml4verification._visitor.SysML4VerificationTraverser;
//import de.monticore.lang.sysml4verification.symboltable.ConstraintDefinitionSymbolTableCompleter;
//import de.monticore.ocl.oclexpressions._symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;

public class SysML4VerificationLanguageAccess extends SysML4VerificationLanguageAccessTOP {
  private final SysML4VerificationTraverser traverser = SysML4VerificationMill.traverser();

  /** Convenience */
  public SysML4VerificationLanguageAccess()
  {
    super(new DocumentManager(), new SysML4VerificationScopeManager());
  }

  public SysML4VerificationLanguageAccess(
      DocumentManager documentManager,
      SysML4VerificationScopeManager scopeManager)
  {
    super(documentManager, scopeManager);
  }

  @Override
  public void runCoCos(ASTSysMLModel ast) {
    // TODO "Complete Symbol Table" gehört nicht hierhin, sondern wird von Language gemacht (Interface nutzen!)
    //traverser.add4SysMLParametrics(new ConstraintDefinitionSymbolTableCompleter());
    //traverser.add4OCLExpressions(new OCLExpressionsSymbolTableCompleter(null, null));
    ast.accept(traverser);

    super.runCoCos(ast);
  }
}
