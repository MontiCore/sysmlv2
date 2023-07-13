package de.monticore.lang.sysmlv2._lsp;

import com.google.gson.JsonPrimitive;
import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.IIndexingManager;
import de.monticore.lang.sysmlv2._lsp.commands.Command;
import de.monticore.lang.sysmlv2._lsp.commands.CreateModelCommand;
import de.monticore.lang.sysmlv2._lsp.commands.CreateRefinementCommand;

import java.util.ArrayList;

public class SysMLv2WorkspaceService extends SysMLv2WorkspaceServiceTOP {

  public SysMLv2WorkspaceService(CommonLanguageServer languageServer, IIndexingManager indexingManager) {
    super(languageServer, indexingManager);

    var commands = new ArrayList<Command>();
    commands.add(new CreateModelCommand(languageServer));
    commands.add(new CreateRefinementCommand(languageServer));

    for (var command : commands) {
      this.registerCommand(command.getCommandName(), params -> {
        var args = params.getArguments().stream().map(x -> ((JsonPrimitive) x).getAsString()).toArray(String[]::new);
        return command.execute(args);
      });
    }
  }
}
