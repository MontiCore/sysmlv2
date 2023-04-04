/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.language_access;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.prettyprint.AstPrettyPrinter;

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
    // TODO "Complete Symbol Table" gehört nicht hierhin, sondern wird von Language gemacht (Interface nutzen!)
    //traverser.add4SysMLParametrics(new ConstraintDefinitionSymbolTableCompleter());
    //traverser.add4OCLExpressions(new OCLExpressionsSymbolTableCompleter(null, null));
    ast.accept(traverser);

    super.runCoCos(ast);
  }

  @Override
  public Optional<AstPrettyPrinter<ASTSysMLModel>> getPrettyPrinter() {
    AstPrettyPrinter printer = node -> node.toString();
    return Optional.of(printer);
  }
}
