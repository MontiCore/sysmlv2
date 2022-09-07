package de.monticore.lang.sysmlv2.codeAction;

import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlv2._lsp.LanguageServerCLI;

import de.monticore.lang.sysmlbasis._symboltable.SysMLBasisScope;
import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import org.eclipse.lsp4j.*;

import java.util.*;
import java.util.function.Consumer;

public class UpperCaseBlockNameCoCoCodeActionProvider extends CoCoCodeActionProvider {
  private DocumentManager documentManager;

  public UpperCaseBlockNameCoCoCodeActionProvider(DocumentManager documentManager) {
    this.documentManager = documentManager;
  }

  @Override
  public List<CodeAction> createFixesFor(TextDocumentItem document, Diagnostic diagnostic) {
    List<CodeAction> res = new ArrayList<>();
    Optional<DocumentInformation> di = documentManager.getDocumentInformation(document);

    if (!di.isPresent())
      return res;
    ASTNode ast = di.get().ast;

    traverse((ISysMLv2Scope) ast.getEnclosingScope(), symbol -> {
      if (symbol == null)
        return;
      if (symbol.getAstNode() == null)
        return;
      if (symbol.getAstNode() instanceof ASTPartDef) {
        ASTPartDef partDef = (ASTPartDef) symbol.getAstNode();
        String newName = partDef.getName().substring(0, 1).toUpperCase() + partDef.getName().substring(1);
        CodeAction ca = new CodeAction(
            String.format("Capitalize first letter of block declaration name. (%s -> %s)", partDef.getName(), newName));
        ca.setKind(CodeActionKind.QuickFix);

        TextEdit textEdit = new TextEdit();
        Position pStart = new Position(partDef.get_SourcePositionStart().getLine() - 1,
            partDef.get_SourcePositionStart().getColumn() + 6);
        Position pEnd = new Position(partDef.get_SourcePositionStart().getLine() - 1,
            partDef.get_SourcePositionStart().getColumn() + 6 + newName.length());
        textEdit.setRange(new Range(pStart, pEnd));

        textEdit.setNewText(newName);
        ca.setEdit(new WorkspaceEdit(Collections.singletonMap(document.getUri(), Collections.singletonList(textEdit))));

        res.add(ca);
        LanguageServerCLI.logger.info("Found quickfix for " + diagnostic.getCode().get().toString() + " on line "
            + partDef.get_SourcePositionStart().getLine());
      }
    });

    return res;
  }

  private static void traverse(ISysMLv2Scope scope, Consumer<SysMLTypeSymbol> f) {
    if (scope != null) {
      if (scope instanceof SysMLBasisScope) {
        scope.getSysMLTypeSymbols().entries().forEach(entry -> f.accept(entry.getValue()));
      }
      scope.getSubScopes().forEach(s -> traverse(s, f));
    }
  }

  @Override
  public boolean providesActionFor(Diagnostic diagnostic) {
    return diagnostic != null && diagnostic.getCode() != null && "0xSML01".equals(diagnostic.getCode().get());
  }
}
