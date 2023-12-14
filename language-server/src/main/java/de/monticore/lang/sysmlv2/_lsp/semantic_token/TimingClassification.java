package de.monticore.lang.sysmlv2._lsp.semantic_token;

import de.mclsg.lsp.extensions.syntax_highlighting.lexer.Token;
import de.mclsg.lsp.features.sematic_tokens.impl.SemanticTokenTypesWrapper;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassification;
import de.mclsg.lsp.features.sematic_tokens.impl.TokenClassificationRule;

import java.util.Optional;

/**
 * Findet das "timing"-Keyword und weist ihm eine eigene Semantic Token Klasse zu,
 * damit der Client das Keyword anders einfärben kann.
 */
public class TimingClassification
    implements TokenClassificationRule
{
  @Override
  public Optional<TokenClassification> apply(Token token) {
    return Optional.of(
        new TokenClassification(SemanticTokenTypesWrapper.Operator));
  }

  @Override
  public boolean matches(Token token) {
    return token
        .getMatchedToken()
        .filter(mt -> mt.tokenPath.getTokenPath()
            .matches(".*sysMLCausality$"))
        .isPresent();
  }
}
