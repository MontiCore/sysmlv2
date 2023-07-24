package de.monticore.lang.sysmlv2._lsp.features.code_action.utils;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDefBuilder;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModelBuilder;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2ScopeManager;
import de.monticore.lang.sysmlv2._prettyprint.SysMLv2FullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import org.antlr.v4.runtime.misc.Pair;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.CreateFile;
import org.eclipse.lsp4j.CreateFileOptions;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ResourceOperation;
import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class CodeActionFactory {

  public static Optional<CodeAction> buildL2HBasicTemplateCodeAction(ASTPartDef partDef, VersionedTextDocumentIdentifier existingModelDocument, String folder, DocumentManager documentManager){

    var newPartDefName = DocumentUtils.getNewPartDefName(partDef.getName(), new Pair<>("Llr", "Hlr"), null);

    var codeAction = new CodeAction("Generate Hlr template");
    codeAction.setKind(CodeActionKind.QuickFix);

    var templateBuilder = new ASTPartDefBuilder(partDef)
        .removeProperties(ASTSysMLReqType.LLR)
        .setName(newPartDefName);

    SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(scope -> templateBuilder.transformPartUsages(ASTSysMLReqType.HLR));

    templateBuilder.ifReference(
            reference -> !reference.getSysMLElements(ASTStateUsage.class).isEmpty(),
            builder -> builder.addEmptyConstraint(partDef.getSysMLElements(ASTStateUsage.class).get(0).getName()));

    var setRefinement = new TextDocumentEdit();
    setRefinement.setEdits(List.of(buildAddRefinementTextEdit(newPartDefName, partDef.getSymbol())));
    setRefinement.setTextDocument(existingModelDocument);

    var workspaceEdit = new WorkspaceEdit();
    var changes = new ArrayList<>(buildModelCreationEdits(DocumentUtils.getFreeDocumentUri(documentManager, folder, newPartDefName), templateBuilder.build()));
    changes.add(0, Either.forLeft(setRefinement));
    workspaceEdit.setDocumentChanges(changes);
    codeAction.setEdit(workspaceEdit);


    return Optional.of(codeAction);
  }

  public static Optional<CodeAction> buildH2LBasicTemplateCodeAction(ASTPartDef reference, String folder, DocumentManager documentManager){

    var newPartDefName = DocumentUtils.getNewPartDefName(reference.getName(), new Pair<>("Hlr", "Llr"), null);

    var codeAction = new CodeAction("Generate Llr template");
    codeAction.setKind(CodeActionKind.QuickFix);


    var llrBuilder = new ASTPartDefBuilder(reference)
        .removeProperties(ASTSysMLReqType.HLR)
        .setName(newPartDefName);

    SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(scope -> llrBuilder.transformPartUsages(ASTSysMLReqType.LLR));

    llrBuilder
        .ifReference(
          ref -> !ref.getSysMLElements(ASTRequirementUsage.class).isEmpty() || !ref.getSysMLElements(ASTConstraintUsage.class).isEmpty(),
          builder -> builder.addEmptyStateUsage(reference.getName() + "Automaton"))
        .addRefinement(reference);

    var workspaceEdit = new WorkspaceEdit();
    var changes = new ArrayList<>(buildModelCreationEdits(DocumentUtils.getFreeDocumentUri(documentManager,folder, newPartDefName), llrBuilder.build()));
    workspaceEdit.setDocumentChanges(changes);
    codeAction.setEdit(workspaceEdit);

    return Optional.of(codeAction);
  }

  public static Optional<CodeAction> buildH2LDecompositionTemplateCodeAction(ASTPartDef reference, String folder, DocumentManager documentManager){

    var decompName = DocumentUtils.getNewPartDefName(reference.getName(), null, "Decomp");
    var comp1Name = DocumentUtils.getNewPartDefName(decompName, null, "Comp1");
    var comp2Name = DocumentUtils.getNewPartDefName(decompName, null, "Comp2");

    var codeAction = new CodeAction("Decompose with new components");
    codeAction.setKind(CodeActionKind.QuickFix);

    var comp1 = new ASTPartDefBuilder(reference)
        .setName(comp1Name)
        .removeProperties(ASTSysMLReqType.HLR)
        .removeProperties(ASTSysMLReqType.LLR)
        .addEmptyConstraint("constraint1")
        .build();

    var comp2 = new ASTPartDefBuilder(reference)
        .setName(comp2Name)
        .removeProperties(ASTSysMLReqType.HLR)
        .removeProperties(ASTSysMLReqType.LLR)
        .addEmptyConstraint("constraint1")
        .build();

    var decompBuilder = new ASTPartDefBuilder(reference)
        .removeProperties(ASTSysMLReqType.HLR)
        .setName(decompName);

    SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(scope -> decompBuilder.transformPartUsages(ASTSysMLReqType.LLR));

    decompBuilder
        .addDecomposition(comp1, comp2)
        .addRefinement(reference);

    var workspaceEdit = new WorkspaceEdit();
    var changes = new ArrayList<Either<TextDocumentEdit, ResourceOperation>>();

    changes.addAll(buildModelCreationEdits(DocumentUtils.getFreeDocumentUri(documentManager, folder, decompName), decompBuilder.build()));
    changes.addAll(buildModelCreationEdits(DocumentUtils.getFreeDocumentUri(documentManager, folder, comp1Name), comp1));
    changes.addAll(buildModelCreationEdits(DocumentUtils.getFreeDocumentUri(documentManager, folder, comp2Name), comp2));

    workspaceEdit.setDocumentChanges(changes);
    codeAction.setEdit(workspaceEdit);

    return Optional.of(codeAction);
  }

  public static List<CodeAction> buildH2LDecompositionCodeAction(ASTPartDef reference, String folder, DocumentManager documentManager){
    var codeActions = new ArrayList<CodeAction>();
    SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(gs -> {
        var candidates = DecompositionUtils.getDecompositionCandidates(reference, ASTSysMLReqType.HLR)
          .limit(5)
          .collect(Collectors.toList());

      for (var decompCandidate : candidates){
        var decompName = DocumentUtils.getNewPartDefName(reference.getName(), null, "Decomp");

        var codeAction = new CodeAction("Decompose with " + decompCandidate.a.getName() + " o " + decompCandidate.b.getName());
        codeAction.setKind(CodeActionKind.QuickFix);

        var decompBuilder = new ASTPartDefBuilder(reference)
            .removeProperties(ASTSysMLReqType.HLR)
            .setName(decompName);

        SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(scope -> decompBuilder.transformPartUsages(ASTSysMLReqType.LLR));

        decompBuilder
            .addDecomposition(decompCandidate.a, decompCandidate.b)
            .addRefinement(reference);

        var workspaceEdit = new WorkspaceEdit();
        var changes = new ArrayList<>(buildModelCreationEdits(DocumentUtils.getFreeDocumentUri(documentManager, folder, decompName), decompBuilder.build()));

        workspaceEdit.setDocumentChanges(changes);
        codeAction.setEdit(workspaceEdit);
        codeActions.add(codeAction);
      }
    });

    return codeActions;
  }

  public static List<Either<TextDocumentEdit, ResourceOperation>> buildModelCreationEdits(VersionedTextDocumentIdentifier newDocument, ASTPartDef partDef){
    ASTSysMLModel model = new ASTSysMLModelBuilder().addSysMLElement(partDef).build();
    return buildModelCreationEdits(newDocument, model);
  }

  public static List<Either<TextDocumentEdit, ResourceOperation>> buildModelCreationEdits(VersionedTextDocumentIdentifier newDocument, ASTSysMLModel model){
    var createFile = new CreateFile();
    createFile.setUri(newDocument.getUri());
    createFile.setOptions(new CreateFileOptions(false, false));

    var setModelText = new TextEdit();
    setModelText.setRange(new Range(new Position(0, 0), new Position(0, 0)));

    var pp = new SysMLv2FullPrettyPrinter(new IndentPrinter());
    setModelText.setNewText(pp.prettyprint(model));

    return List.of(
        Either.forRight(createFile),
        Either.forLeft(new TextDocumentEdit(newDocument, List.of(setModelText)))
    );
  }

  /**
   * Creates a code action to add a refinement to a partDef.
   * @param documentUri The uri of the document that contains refinement.
   * @param roughName Name of the rough component that is refined.
   * @param refinement The new refinement rough.
   */
  public static CodeAction buildAddRefinementCodeAction(String documentUri, String roughName, PartDefSymbol refinement){
    CodeAction codeAction = new CodeAction("Make " + refinement.getName() + " refine " + roughName);
    codeAction.setKind(CodeActionKind.QuickFix);

    var workspaceEdit = new WorkspaceEdit();
    workspaceEdit.setChanges(Map.of(
        documentUri,
        List.of(buildAddRefinementTextEdit(roughName, refinement))
    ));

    codeAction.setEdit(workspaceEdit);

    return codeAction;
  }

  /**
   * Creates a TextEdit to add a refinement to a partDef.
   * @param roughName Name of the rough component that is refined.
   * @param refinement The new refinement rough.
   */
  public static TextEdit buildAddRefinementTextEdit(String roughName, PartDefSymbol refinement){
    var addRefinement = new TextEdit();
    var refinementStartPos = new Position();
    refinementStartPos.setLine(refinement.getAstNode().get_SourcePositionStart().getLine() - 1);
    refinementStartPos.setCharacter(refinement.getAstNode().get_SourcePositionStart().getColumn() + refinement.getName().length() + "part def ".length());

    if (refinement.getAstNode().getSpecializationList().isEmpty()){
      addRefinement.setNewText(" refines " + roughName);
    } else {
      addRefinement.setNewText(roughName + ", ");
      refinementStartPos.setCharacter(refinementStartPos.getCharacter() + " refines ".length());
    }

    addRefinement.setRange(new Range(refinementStartPos, refinementStartPos));

    return addRefinement;
  }


}
