/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.features.code_action;

import de.mclsg.lsp.document_management.DocumentManager;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.TextDocumentItem;

import java.util.ArrayList;
import java.util.List;

public abstract class CoCoCodeActionProvider {


  protected final List<String> matchingErrorCodes;
  protected final DocumentManager documentManager;


  public CoCoCodeActionProvider(DocumentManager documentManager, String... matchingErrorCodes){
    this.matchingErrorCodes = List.of(matchingErrorCodes);
    this.documentManager = documentManager;
  }

  public abstract List<CodeAction> createFixesFor(TextDocumentItem document, Diagnostic diagnostic);

  public boolean providesActionFor(Diagnostic diagnostic){
    return false;
    //return diagnostic != null && diagnostic.getCode() != null && matchingErrorCodes.contains(diagnostic.getCode().get().toString());
  }
}
