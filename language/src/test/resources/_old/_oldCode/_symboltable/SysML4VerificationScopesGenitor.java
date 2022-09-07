package schrott._symboltable;

import de.monticore.lang.sysml.sysml4verification.SysML4VerificationMill;
import schrott._ast.ASTPortDefinitionStd;
import de.monticore.lang.sysml.sysml4verification._visitor.SysML4VerificationTraverser;
import schrott._visitors.SetPackageNameInSpannedScope;
import schrott._visitors.SubscopeTraverser;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Class handles the creation of modified symbols
 */
public class SysML4VerificationScopesGenitor extends SysML4VerificationScopesGenitorTOP {

  protected MCBasicTypesFullPrettyPrinter typePrinter = MCBasicTypesMill.mcBasicTypesPrettyPrinter();

  /**
   * Creates PortDefinitionStd symbol for a port by additionally setting its type,
   * type is extracted from the model
   */
  @Override
  public void visit(ASTPortDefinitionStd node) {
    PortDefinitionStdSymbol symbol = SysML4VerificationMill.portDefinitionStdSymbolBuilder()
        .setName(node.getName())
        .build();

    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    }
    else {
      Log.warn("0xA5021x48024 Symbol cannot be added to current scope, since no scope exists.");
    }
    symbol.setAstNode(node);
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());

    initPortDefinitionStdHP1(node.getSymbol());
  }

  @Override
  protected void initArtifactScopeHP2(ISysML4VerificationArtifactScope scope) {
    // This hook point ensures that scope names are set before any operations are called. This is especially important
    // for MC resolve operations
    SysML4VerificationTraverser traverser = new SubscopeTraverser();
    traverser.add4SysML4Verification(new SetPackageNameInSpannedScope());
    scope.accept(traverser);
  }
}
