package de.monticore.lang.sysmlv2;

import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.CodeActionProvider;
import de.monticore.lang.sysmlv2.codeAction.UpperCaseBlockNameCoCoCodeActionProvider;
import de.monticore.lang.sysmlv2.codeAction.CoCoCodeActionProvider;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.List;

public class SysML4VerificationCodeActionProvider implements CodeActionProvider {

  private List<CoCoCodeActionProvider> coCoCodeActionProviders = new ArrayList<>();

  public SysML4VerificationCodeActionProvider(DocumentManager documentManager) {
    coCoCodeActionProviders.add(new UpperCaseBlockNameCoCoCodeActionProvider(documentManager));
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
