/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.features.code_action;

import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.eclipse.lsp4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * CodeAction for UniqueSubPartNamesInParentCoCo with ErrorCode 0x10AA7. Suggest
 * a new name by appending "A".
 */
public class UniqueSubPartName extends CoCoCodeActionProvider {

  public UniqueSubPartName(DocumentManager documentManager) {
    super(documentManager, "0x10AA7");
  }

  @Override
  public List<CodeAction> createFixesFor(TextDocumentItem document,
                                         Diagnostic diagnostic) {
    List<CodeAction> res = new ArrayList<>();
    Optional<DocumentInformation> di = documentManager.getDocumentInformation(
        document);

    if (di.isEmpty()) {
      return res;
    }

    ASTNode ast = di.get().ast;

    Range diagnosticRange = diagnostic.getRange();
    int diagnosticLine = diagnosticRange.getStart().getLine();
    int diagnosticColumn = diagnosticRange.getStart().getCharacter();

    traverse((ISysMLv2Scope) ast.getEnclosingScope(), symbol -> {
      if (symbol == null || symbol.getAstNode() == null) {
        return;
      }

      ASTPartUsage node = symbol.getAstNode();

      if (!isContainedInPosition(node, diagnosticLine, diagnosticColumn)) {
        return; // Skip if this node doesn't contain the diagnostic position
      }

      if (triggeredCoCo(node) && node.isPresentName()) {
        String currentName = node.getName();
        String newName = suggestedName(currentName);

        CodeAction ca = new CodeAction(
            String.format("Change name to '%s'", newName));
        ca.setKind(CodeActionKind.QuickFix);

        TextEdit textEdit = new TextEdit();
        Position pStart = calculateStartPosition(document, node, currentName);
        Position pEnd = new Position(pStart.getLine(),
            pStart.getCharacter() + currentName.length());

        textEdit.setRange(new Range(pStart, pEnd));
        textEdit.setNewText(newName);

        ca.setEdit(new WorkspaceEdit(Collections.singletonMap(document.getUri(),
            Collections.singletonList(textEdit))));
        res.add(ca);
      }
    });

    return res;
  }

  // Simply appends "A", can be changed in the future
  private String suggestedName(String oldName){
    return oldName + "A";
  }

  private static void traverse(ISysMLv2Scope scope,
                               Consumer<PartUsageSymbol> f) {
    if (scope != null) {
      scope.getPartUsageSymbols().entries().forEach(
          entry -> f.accept(entry.getValue()));
      scope.getSubScopes().forEach(s -> traverse(s, f));
    }
  }

  // Returns true if this node has triggered 0x10AA7
  private boolean triggeredCoCo(ASTPartUsage node) {
    var scope = node.getEnclosingScope();
    String partName = node.getName();

    var allMatches = scope.resolvePartUsageLocallyMany(false, partName,
        AccessModifier.ALL_INCLUSION, p -> true);

    return allMatches.size() > 1;
  }

  /**
   * Finds the exact start position of the part name.
   *
   * Skips the 'part' keyword and any following whitespaces
   * to avoid editing keywords and other syntax elements.
   */
  protected Position calculateStartPosition(TextDocumentItem document,
                                            ASTPartUsage partUsage,
                                            String currentName) {
    int lineIdx = partUsage.get_SourcePositionStart().getLine() - 1;
    int nameIdx = partUsage.get_SourcePositionStart().getColumn() - 1;

    String[] lines = document.getText().split("\\r?\\n");
    if (lineIdx < lines.length) {
      String line = lines[lineIdx];

      int startIdx = Math.max(0, nameIdx);

      int partIdx = line.indexOf("part", startIdx);
      if (partIdx != -1) {
        startIdx = partIdx + 4; // Skip "part"

        // Skip any whitespace after "part"
        while (startIdx < line.length() && Character.isWhitespace(line.charAt(startIdx))) {
          startIdx++;
        }
      }

      nameIdx = line.indexOf(currentName, startIdx);

    }
    return new Position(lineIdx, nameIdx);
  }

  /**
   * Verifies that the diagnostic is pointing at the exact node as the same line
   * is not enough.
   * Example: "part a : type; part b : type;"
   */
  private static boolean isContainedInPosition(ASTPartUsage node,
                                               int diagnosticLine,
                                               int diagnosticColumn) {

    int startLine = node.get_SourcePositionStart().getLine() - 1;
    int endLine = node.get_SourcePositionEnd().getLine() - 1;

    boolean containsPosition = false;
    if (diagnosticLine >= startLine && diagnosticLine <= endLine) {
      if (diagnosticLine == startLine) {
        containsPosition = diagnosticColumn >=
            node.get_SourcePositionStart().getColumn() - 1;
      }
      else if (diagnosticLine == endLine) {
        containsPosition = diagnosticColumn <=
            node.get_SourcePositionEnd().getColumn() - 1;
      }
      else {
        containsPosition = true;
      }
    }
    return containsPosition;
  }

}
