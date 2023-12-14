package de.monticore.lang.sysmlv2._lsp.commands;

import de.monticore.lang.sysmlbasis._ast.ASTModifierBuilder;
import de.monticore.lang.sysmlparts._ast.ASTEnumDefBuilder;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDefBuilder;
import de.monticore.lang.sysmlparts._ast.ASTPortDefBuilder;
import de.monticore.lang.sysmlparts._ast.ASTSysMLEnumConstantBuilder;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModelBuilder;
import de.monticore.lang.sysmlv2._lsp.SysMLv2LanguageServer;
import de.monticore.lang.sysmlv2._lsp.features.code_action.utils.CodeActionFactory;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.CreateFilesParams;
import org.eclipse.lsp4j.FileCreate;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.services.LanguageServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreateModelCommand extends Command<Boolean> {

  public CreateModelCommand(LanguageServer languageServer) {
    super((SysMLv2LanguageServer) languageServer);
  }

  @Override
    public String getCommandName() {
      return "createModel";
    }

    @Override
    public Boolean execute(String[] args) {
      if (args.length < 3) {
        return false;
      }

      var modelUri = args[0];
      var modelName = args[1];
      var modelType = args[2];

      var model = new ASTSysMLModelBuilder();
      var booleanType = SysMLv2Mill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
      if (modelType.equalsIgnoreCase("Specification")) {
        ASTPartDef partDef = new ASTPartDefBuilder()
            .setName(modelName)
            .addPortUsage("port1", booleanType)
            .build();
        model.addSysMLElement(new ASTPartDefBuilder(partDef).addEmptyConstraint(modelName).build());

      } else if (modelType.equalsIgnoreCase("Automaton")) {
        ASTPartDef partDef = new ASTPartDefBuilder().setName(modelName).build();
        var automatonBuilder = new ASTPartDefBuilder(partDef)
            .addPortUsage("port1", booleanType)
            .addEmptyStateUsage(modelName);
        if (args.length == 4) {
          automatonBuilder.addRefinement(new ASTPartDefBuilder().setName(args[3]).setModifier(new ASTModifierBuilder().build()).build());
        }
        partDef = automatonBuilder.build();
        model.addSysMLElement(new ASTPartDefBuilder(partDef).build());

      } else if (modelType.equalsIgnoreCase("Port Definition")) {
        var portDef = new ASTPortDefBuilder().setName(modelName).setModifier(new ASTModifierBuilder().build()).build();
        model.addSysMLElement(portDef);

      } else if (modelType.equalsIgnoreCase("Enum Definition")) {
        var enumDef = new ASTEnumDefBuilder()
              .setName(modelName)
              .addSysMLEnumConstant(new ASTSysMLEnumConstantBuilder().setName("A").build())
              .addSysMLEnumConstant(new ASTSysMLEnumConstantBuilder().setName("B").build())
              .build();

        model.addSysMLElement(enumDef);
      }

      var workspaceEditParams = new ApplyWorkspaceEditParams();
      workspaceEditParams.setEdit(new WorkspaceEdit());
      workspaceEditParams.getEdit().setDocumentChanges(new ArrayList<>());

      var edits = CodeActionFactory.buildModelCreationEdits(new VersionedTextDocumentIdentifier(modelUri,null), model.build());
      workspaceEditParams.getEdit().getDocumentChanges().addAll(edits);
      var future = languageServer.getLanguageClient().applyEdit(workspaceEditParams);

      try {
        var r = future.get().isApplied();
        languageServer.getWorkspaceService().setTriggerIndexing(true);
        languageServer.getWorkspaceService().didCreateFiles(new CreateFilesParams(List.of(new FileCreate(modelUri))));
        return r;
      }
      catch (InterruptedException | ExecutionException e) {
        return false;
      }
    }
}
