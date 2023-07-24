package de.monticore.lang.sysmlv2._lsp.features.code_action;

import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlv2._lsp.features.code_action.utils.DocumentUtils;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.CodeActionFactory.buildAddRefinementCodeAction;
import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.CodeActionFactory.buildL2HBasicTemplateCodeAction;

public class MissingRefinement extends CoCoCodeActionProvider {

  public MissingRefinement(DocumentManager documentManager){
    super(documentManager, "0x90010", "0x90011", "0x90022");
  }

  /**
   * Wenn ein LLR nichts verfeinert, wird eine Warnung produziert.
   * Diese CodeAction versucht mithilfe von übereinstimmenden Interfaces PartDefs zu finden, die für eine Verfeinerung
   * infrage kommen und bietet diese als QuickFix an.
   */
  @Override
  public List<CodeAction> createFixesFor(TextDocumentItem document, Diagnostic diagnostic) {
    Optional<DocumentInformation> di = documentManager.getDocumentInformation(document);
    if (di.isEmpty()){
      return new ArrayList<>();
    }

    var refPartDef = ((ISysMLv2Scope) di.get().ast.getEnclosingScope()).getLocalPartDefSymbols()
        .stream()
        .filter(p -> di.get().ast.get_SourcePositionStart().equals(p.getSourcePosition()))
        .findFirst()
        .orElse(null);

    if (refPartDef == null){
      return new ArrayList<>();
    }

    var roughCandidates = new ArrayList<PartDefSymbol>();
    di.get().syncAccessGlobalScope(gs -> {
        if (diagnostic.getCode().get().toString().equals("0x90020")) {
          roughCandidates.addAll(refPartDef.getRefinementOrRoughCandidates(false));
        } else if (diagnostic.getCode().get().toString().equals("0x90021")) {
          roughCandidates.addAll(refPartDef.getRefinementOrRoughCandidates(true));
        } else if (diagnostic.getCode().get().toString().equals("0x90022")) {
          roughCandidates.addAll(refPartDef.getBasicRefinementCandidates());
        }
    });

    var basicCodeActions = new HashMap<PartDefSymbol, CodeAction>();
    roughCandidates.forEach((p) -> basicCodeActions.put(p, buildAddRefinementCodeAction(document.getUri(), p.getName(), refPartDef)));

    // Set preferred code action based on the calculated score
    basicCodeActions.keySet().stream()
        .max(Comparator.comparingInt(refPartDef::getRefinementScore))
        .ifPresent(max -> basicCodeActions.get(max).setIsPreferred(true));


    var modelFolder = DocumentUtils.getFolder(document.getUri());
    var basicTemplateCodeAction = buildL2HBasicTemplateCodeAction(refPartDef.getAstNode(),
        new VersionedTextDocumentIdentifier(document.getUri(), document.getVersion()),
        modelFolder,
        documentManager);

    var result = new ArrayList<>(basicCodeActions.values());
    basicTemplateCodeAction.ifPresent(result::add);

    return result;
  }
}
