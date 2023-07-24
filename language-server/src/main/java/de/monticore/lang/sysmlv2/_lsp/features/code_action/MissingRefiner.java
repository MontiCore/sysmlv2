package de.monticore.lang.sysmlv2._lsp.features.code_action;

import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._lsp.features.code_action.utils.DocumentUtils;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.TextDocumentItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.CodeActionFactory.buildAddRefinementCodeAction;
import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.CodeActionFactory.buildH2LBasicTemplateCodeAction;
import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.CodeActionFactory.buildH2LDecompositionCodeAction;
import static de.monticore.lang.sysmlv2._lsp.features.code_action.utils.CodeActionFactory.buildH2LDecompositionTemplateCodeAction;

public class MissingRefiner extends CoCoCodeActionProvider {

  public MissingRefiner(DocumentManager documentManager){
    super(documentManager, "0x90020", "0x90021");
  }

  /**
   * Wenn ein HLR nicht verfeinert wird, wird eine Warnung produziert.
   * Diese CodeAction versucht mithilfe von übereinstimmenden Interfaces PartDefs zu finden, die als Verfeinerung
   * infrage kommen und bietet diese als QuickFix an.
   */
  @Override
  public List<CodeAction> createFixesFor(TextDocumentItem document, Diagnostic diagnostic) {
    Optional<DocumentInformation> di = documentManager.getDocumentInformation(document);
    if (di.isEmpty()){
      new ArrayList<>();
    }

    var roughPartDef = ((ISysMLv2Scope) di.get().ast.getEnclosingScope()).getLocalPartDefSymbols()
        .stream()
        .filter(p -> di.get().ast.get_SourcePositionStart().equals(p.getSourcePosition()))
        .findFirst()
        .orElse(null);

    if (roughPartDef == null){
      return new ArrayList<>();
    }

    var refCandidates = new ArrayList<PartDefSymbol>();
    di.get().syncAccessGlobalScope(gs -> {
      if (diagnostic.getCode().get().toString().equals("0x90020")) {
        refCandidates.addAll(roughPartDef.getRefinementOrRoughCandidates(false));
      } else if (diagnostic.getCode().get().toString().equals("0x90021")) {
        refCandidates.addAll(roughPartDef.getRefinementOrRoughCandidates(true));
      }
    });

    var partDefToDocument = mapPartDefsToDocument();
    var basicCodeActions = new HashMap<PartDefSymbol, CodeAction>();
    refCandidates.forEach((p) -> basicCodeActions.put(p, buildAddRefinementCodeAction(partDefToDocument.get(p.getAstNode()).uri, roughPartDef.getName(), p)));

    // Set preferred code action based on the calculated score
    basicCodeActions.keySet().stream()
        .max(Comparator.comparingInt(refCandidate -> refCandidate.getRefinementScore(roughPartDef)))
        .ifPresent(max -> basicCodeActions.get(max).setIsPreferred(true));


    var modelFolder = DocumentUtils.getFolder(document.getUri());
    var llrTemplate = buildH2LBasicTemplateCodeAction(roughPartDef.getAstNode(), modelFolder, documentManager);
    var newDecompositionTemplate = buildH2LDecompositionTemplateCodeAction(roughPartDef.getAstNode(), modelFolder, documentManager);
    var existingComponentsDecomposition = buildH2LDecompositionCodeAction(roughPartDef.getAstNode(), modelFolder, documentManager);

    var result = new ArrayList<>(basicCodeActions.values());
    llrTemplate.ifPresent(result::add);
    newDecompositionTemplate.ifPresent(result::add);
    result.addAll(existingComponentsDecomposition);

    return result;
  }

  private HashMap<ASTPartDef, DocumentInformation> mapPartDefsToDocument(){
    var partDefToDocument = new HashMap<ASTPartDef, DocumentInformation>();
    documentManager.getAllDocumentInformation(d -> true).forEach(d -> {
      var traverser = SysMLv2Mill.traverser();
      traverser.add4SysMLParts(new SysMLPartsVisitor2() {
        @Override
        public void visit(ASTPartDef node) {
          partDefToDocument.put(node, d);
        }
      });
      traverser.traverse((ASTSysMLModel) d.ast);
    });
    return partDefToDocument;
  }
}
