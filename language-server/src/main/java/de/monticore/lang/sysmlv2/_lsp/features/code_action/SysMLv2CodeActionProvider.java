/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.features.code_action;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.prettyprint.AstPrettyPrinter;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.List;

public class SysMLv2CodeActionProvider extends SysMLv2CodeActionProviderTOP {

  private final List<CoCoCodeActionProvider> coCoCodeActionProviders = new ArrayList<>();

  public SysMLv2CodeActionProvider(DocumentManager documentManager,
                                   AstPrettyPrinter<ASTSysMLModel> astSysMLModelAstPrettyPrinter) {
    super(documentManager, astSysMLModelAstPrettyPrinter);
    coCoCodeActionProviders.add(new UpperCaseBlockName(documentManager));
    coCoCodeActionProviders.add(new MissingRefinement(documentManager));
    coCoCodeActionProviders.add(new MissingRefiner(documentManager));
  }

  @Override
  public List<Either<Command, CodeAction>> codeAction(TextDocumentItem document, CodeActionContext context,
                                                      Range range) {
    List<Either<Command, CodeAction>> res = new ArrayList<>();
    if (context.getDiagnostics() == null || context.getDiagnostics().isEmpty()) {
      return res;
    }

    for (Diagnostic diagnostic : context.getDiagnostics()) {
      for (CoCoCodeActionProvider p : coCoCodeActionProviders) {
        if (p.providesActionFor(diagnostic)) {
          for (CodeAction codeAction : p.createFixesFor(document, diagnostic)) {
            res.add(Either.forRight(codeAction));
          }
        }
      }
    }

    return res;
  }

}
