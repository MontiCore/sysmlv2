package de.monticore.lang.sysmlv2._lsp.commands;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2._lsp.SysMLv2LanguageServer;
import de.monticore.lang.sysmlv2._lsp.features.code_action.utils.DocumentUtils;
import org.eclipse.lsp4j.services.LanguageServer;

public abstract class Command<T> {
  protected  final SysMLv2LanguageServer languageServer;

  public Command(SysMLv2LanguageServer languageServer) {
    this.languageServer = languageServer;
  }

  public abstract String getCommandName();
  public abstract T execute(String[] args);

}
