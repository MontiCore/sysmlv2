package de.monticore.lang.sysmlv2._lsp;

import de.mclsg.lsp.util.LanguageServerContext;
import de.monticore.lang.sysmlv2._lsp.features.syntax_highlighting.SysMLv2LexerProvider;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2LanguageAccess;
import de.monticore.lang.sysmlv2._lsp.semantic_token.TimingClassification;
import de.monticore.lang.sysmlv2._lsp.semantic_token.TypeClassification;

public class SysMLv2SyntaxHighlightingService extends SysMLv2SyntaxHighlightingServiceTOP {

  public SysMLv2SyntaxHighlightingService(SysMLv2LanguageAccess languageAccess, LanguageServerContext context) {
    super(languageAccess, context);
  }

  protected void registerProviders(SysMLv2LanguageAccess languageAccess) {
    var lexerProvider = new SysMLv2LexerProvider(languageAccess);
    lexerProvider.addClassificationRule(new TimingClassification());
    lexerProvider.addClassificationRule(new TypeClassification());
    register(lexerProvider);
  }

}
