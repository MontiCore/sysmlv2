/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._prettyprint.SysMLv2FullPrettyPrinter;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.prettyprint.AstPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Optional;

public class SysMLv2LanguageAccess extends SysMLv2LanguageAccessTOP {
  private final SysMLv2Traverser traverser = SysMLv2Mill.traverser();

  /** Convenience */
  public SysMLv2LanguageAccess()
  {
    super(new DocumentManager(), new SysMLv2ScopeManager());
  }

  public SysMLv2LanguageAccess(
      DocumentManager documentManager,
      SysMLv2ScopeManager scopeManager)
  {
    super(documentManager, scopeManager);
  }

  @Override
  public void runCoCos(ASTSysMLModel ast) {
    ast.accept(traverser);
    super.runCoCos(ast);
  }

  @Override
  public Optional<AstPrettyPrinter<ASTSysMLModel>> getPrettyPrinter() {
    AstPrettyPrinter<ASTSysMLModel> pp = node -> new SysMLv2FullPrettyPrinter(new IndentPrinter()).prettyprint(node);
    return Optional.of(pp);
  }
}
