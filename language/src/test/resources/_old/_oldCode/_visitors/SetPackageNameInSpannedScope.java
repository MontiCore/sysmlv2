package schrott._visitors;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLType;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import schrott._symboltable.ISysML4VerificationArtifactScope;
import schrott._symboltable.ISysML4VerificationScope;
import de.monticore.lang.sysml.sysml4verification._visitor.SysML4VerificationVisitor2;

/**
 * Setzt den Namen des vom `package` gespannten Scopes (i.e., des vom "Body" gepannten Scopes) auf den Namen des Pakets.
 * Dieser Schritt ist nötig, damit das resolven von Elementen in (verschachtelten) Paketen später möglich ist und gehört
 * zu Phase2+ ("Filling Symbols and Scopes with Values", siehe MC handbook Kapitel 9.6.2) der Symboltable-Creation.
 *
 * Beispiel:
 * package avionic {
 *   package hlr {
 *     part def Clock { ... }
 *   }
 * }
 *
 * Vorher: avionic.getBody().getSpannedScope().name == null
 * Nachher: avionic.getBody().getSpannedScope().name == "avionic"
 */
public class SetPackageNameInSpannedScope implements SysML4VerificationVisitor2 {

  /**
   * Um den Namen des gespannten Scopes des Bodys zu setzen, braucht man das ASTPackage. Leider ist die Navigation von
   * Body zu Package nur schwer (ineffizient - body -> enclosingScope -> typeSymbols -> filter(equalsBody)) möglich.
   * Deswegen visitieren wir die "Kinder" (`getLocalSysMLTypeSymbols`) eines Scopes um die Packages (nicht die Bodys) zu
   * erreichen. Ausgehen vom Package navigieren wir dann zum Body und setzen dort den Namen des gespannten Scopes.
   */
  @Override
  public void visit(ISysML4VerificationScope scope) {
    for (SysMLTypeSymbol child : scope.getLocalSysMLTypeSymbols()) {
      ASTSysMLType childAST = child.getAstNode();

      if (childAST instanceof ASTPackage) {
        ((ASTPackage) childAST).getPackageBody().getSpannedScope().setName(childAST.getName());
      }
    }
  }

  /**
   * Da wir wie in {@link SetPackageNameInSpannedScope#visit(ISysML4VerificationScope)} beschrieben die "Kinder" eines
   * Scopes "manuell visitieren", müssen wir bei ArtifactScopes anfangen, um auch die top-level Elemente abzugrasen.
   */
  @Override
  public void visit(ISysML4VerificationArtifactScope node) {
    this.visit((ISysML4VerificationScope) node);
  }

}
