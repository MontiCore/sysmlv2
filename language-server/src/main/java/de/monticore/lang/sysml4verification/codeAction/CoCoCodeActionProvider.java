package de.monticore.lang.sysml4verification.codeAction;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.TextDocumentItem;

import java.util.List;

public abstract class CoCoCodeActionProvider {

  public abstract List<CodeAction> createFixesFor(TextDocumentItem document, Diagnostic diagnostic);

  public abstract boolean providesActionFor(Diagnostic diagnostic);
}
