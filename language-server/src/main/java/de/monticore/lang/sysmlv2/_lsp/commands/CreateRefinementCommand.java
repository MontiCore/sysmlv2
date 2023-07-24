package de.monticore.lang.sysmlv2._lsp.commands;

import de.monticore.lang.sysmlparts._ast.ASTPartDefBuilder;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlv2._lsp.SysMLv2LanguageServer;
import de.monticore.lang.sysmlv2._lsp.features.code_action.utils.CodeActionFactory;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2ScopeManager;
import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.CreateFilesParams;
import org.eclipse.lsp4j.FileCreate;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CreateRefinementCommand extends Command<Boolean> {

  public CreateRefinementCommand(LanguageServer languageServer) {
    super((SysMLv2LanguageServer) languageServer);
  }

  @Override
  public String getCommandName() {
    return "createRefinement";
  }

  @Override
  public Boolean execute(String[] args) {
    if (args.length < 4) {
      return false;
    }
    var baseModelName = args[0];
    var refinementType = args[1];
    var modelName = args[2];
    var modelUri = args[3];

    AtomicReference<Optional<PartDefSymbol>> baseSymbol = new AtomicReference<>(Optional.empty());
    SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(scope -> baseSymbol.set(scope.resolvePartDef(baseModelName)));
    if (baseSymbol.get().isEmpty()) {
      return false;
    }

    var builder = new ASTPartDefBuilder(baseSymbol.get().get().getAstNode());
    builder.setName(modelName);
    builder.addRefinement(baseSymbol.get().get().getAstNode());

    if (refinementType.equalsIgnoreCase("Specification")) {
      builder.addEmptyConstraint(modelName);
    } else if (refinementType.equalsIgnoreCase("Automaton")) {
      builder.addEmptyStateUsage(modelName);
    } else if (refinementType.equalsIgnoreCase("Empty Model")){
      builder.removeProperties(ASTSysMLReqType.MIXED);
    }

    var workspaceEditParams = new ApplyWorkspaceEditParams();
    workspaceEditParams.setEdit(new WorkspaceEdit());
    workspaceEditParams.getEdit().setDocumentChanges(new ArrayList<>());

    var edits = CodeActionFactory.buildModelCreationEdits(new VersionedTextDocumentIdentifier(modelUri,null), builder.build());
    workspaceEditParams.getEdit().getDocumentChanges().addAll(edits);
    var future = languageServer.getLanguageClient().applyEdit(workspaceEditParams);


    try {
      var r = future.get().isApplied();
      languageServer.getWorkspaceService().didCreateFiles(new CreateFilesParams(List.of(new FileCreate(modelUri))));
      return r;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
