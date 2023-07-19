package de.monticore.lang.sysmlparts._ast;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRefinement;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ASTPartDef extends ASTPartDefTOP {

  /***
   * @param type The type of SysML elements to search for.
   * @return List of SysML elements of the given type.
   */
  public <T extends ASTSysMLElement> List<T> getSysMLElements(Class<T> type){
    return this.isEmptySysMLElements() ?
        new ArrayList<>() : this.getSysMLElementList().stream()
        .filter(type::isInstance)
        .map(e -> (T) e)
        .collect(Collectors.toList());
  }

  /**
   * Löst die als Specialization verpackten Refinement-Relationen auf. Ist dabei nicht transitiv, sondern rein AST-
   * orientiert: Wenn etwas nicht explizit im AST steht, wird es auch nicht zurückgegeben.
   */
  public List<PartDefSymbol> getRefinements() {
    List<PartDefSymbol> refinements = new ArrayList<>();
    for(ASTSpecialization spec: this.getSpecializationList()) {
      if(spec instanceof ASTSysMLRefinement) {
        for(ASTMCType superType: spec.getSuperTypesList()) {
          String superTypeName = superType.printType(
              new de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter(
                  new de.monticore.prettyprint.IndentPrinter()));
          Optional<PartDefSymbol> refinementDef =
              this.getEnclosingScope().resolvePartDef(superTypeName);
          // Defensive programming, let CoCos handle errors
          if(refinementDef.isPresent()) {
            refinements.add(refinementDef.get());
          }
        }
      }
    }
    return refinements;
  }
}
