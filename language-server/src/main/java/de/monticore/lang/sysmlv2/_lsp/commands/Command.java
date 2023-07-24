package de.monticore.lang.sysmlv2._lsp.commands;

import de.monticore.lang.sysmlv2._lsp.SysMLv2LanguageServer;

public abstract class Command<T> {
  protected  final SysMLv2LanguageServer languageServer;

  public Command(SysMLv2LanguageServer languageServer) {
    this.languageServer = languageServer;
  }

  public abstract String getCommandName();
  public abstract T execute(String[] args);

}
