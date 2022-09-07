package schrott._symboltable;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.sysml._symboltable.AddVisibilityToSymbolVisitor;
import de.monticore.lang.sysml.sysml._symboltable.doubleimports.AmbigousImportCheck;
import de.monticore.lang.sysml.sysml._symboltable.doubleimports.RemoveDoubleImportsFromScope;
import de.monticore.lang.sysml.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml.sysml4verification._visitor.SysML4VerificationTraverserImplementation;

import java.util.List;

/**
 * This class provides methods to create symboltables. It manages the Mill including the (global) scopes, delegators and
 * traversers
 */
public class SysML4VerificationSymbolTableCreator {

  /**
   * Creates Symboltable for multiple ast's and returns the resulting global scope. Symbols will only be added to the
   * global scope. Clear the scope yourself, if you need an empty scope. Beware of missing basic symbols when you do.
   *
   * @param mp ModelPath in which the models lie.
   * @param astUnits List of Ast for which the symboltable should be build
   */
  public ISysML4VerificationGlobalScope createSymboltable(List<ASTUnit> astUnits, ModelPath mp) {
    // Mill has to be initialized
    SysML4VerificationMill.init();

    // Initialize a global scope. The global scope is filled by scopesGenitorDelegator().createFromAST(astUnit) below
    ISysML4VerificationGlobalScope globalScope = SysML4VerificationMill.globalScope();
    globalScope.setModelPath(mp);

    // Create Symboltables for all AST nodes
    for (ASTUnit astUnit : astUnits) {
      // build symbols + fill global scope
      SysML4VerificationMill.scopesGenitorDelegator().createFromAST(astUnit);
    }

    AddVisibilityToSymbolVisitor addVisibilityToSymbolVisitor = new AddVisibilityToSymbolVisitor(
        new SysML4VerificationTraverserImplementation());
    for (ASTUnit astUnit : astUnits) {
      addVisibilityToSymbolVisitor.startTraversal(astUnit);
    }

    /* We can't use AddImportToScopeVisitor from SysML because it uses ResolveQualifiedNameHelper
     to resolve symbols and the latter class in SysML only resolves SysMLType symbols.
     This was fixed in SysML4Verification.ResolveQualifiedNameHelper.
     Unless the fix is copied over, we will continue to use SysML4Verification.AddImportToScopeVisitor
     */
    AddImportToScopeVisitor addImportToScopeVisitor = new AddImportToScopeVisitor(
        new SysML4VerificationTraverserImplementation());
    for (ASTUnit model : astUnits) {
      addImportToScopeVisitor.memorizeImportsPhase1of5(model);
    }

    AmbigousImportCheck ambigousImportCheck = new AmbigousImportCheck(new SysML4VerificationTraverserImplementation());
    for (ASTUnit model : astUnits) {
      ambigousImportCheck.addWarningForAmbigousImport2of5(model);
    }

    for (ASTUnit model : astUnits) {
      addImportToScopeVisitor.addReexportedSymbolsOfPackagesPhase3of5(model);
    }

    RemoveDoubleImportsFromScope removeDoubleImportsFromScope = new RemoveDoubleImportsFromScope(
        new SysML4VerificationTraverserImplementation());

    for (ASTUnit model : astUnits) {
      removeDoubleImportsFromScope.removeDoubleImportsAndAddWarningPhase4of5(model);
    }

    for (ASTUnit model : astUnits) {
      addImportToScopeVisitor.addImportsToScopePhase5of5(model);
    }

    return globalScope;
  }

}
