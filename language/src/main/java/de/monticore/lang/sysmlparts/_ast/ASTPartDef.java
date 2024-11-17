package de.monticore.lang.sysmlparts._ast;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRefinement;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
          );
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

  /**
   * Returns the complexity difference between this and one given component.
   * Complexity difference means the number of ports, attributes, parts and connections that are used in the components.
   */
  public int complexityDifference(ASTPartDef target){
    int score = 0;

    var comp1_ports = this.getSysMLElements(ASTPortUsage.class);
    var comp2_ports = target.getSysMLElements(ASTPortUsage.class);
    score += Math.abs(comp1_ports.size() - comp2_ports.size()) * 1.5;

    var comp1_attributes = this.getSysMLElements(ASTAttributeUsage.class);
    var comp2_attributes = target.getSysMLElements(ASTAttributeUsage.class);
    score += Math.abs(comp1_attributes.size() - comp2_attributes.size());

    var comp1_parts = this.getSysMLElements(ASTPartUsage.class);
    var comp2_parts = target.getSysMLElements(ASTPartUsage.class);
    score += Math.abs(comp1_parts.size() - comp2_parts.size());

    var comp1_connections = this.getSysMLElements(ASTConnectionUsage.class);
    var comp2_connections = target.getSysMLElements(ASTConnectionUsage.class);
    score += Math.abs(comp1_connections.size() - comp2_connections.size()) * 0.5;

    return score;
  }

  /**
   * Returns the estimated complexity of a composition that is creating using given two components.
   * Calculation is based on {@link #complexityDifference(ASTPartDef)
   */
  public int compositionComplexity(ASTPartDef comp1, ASTPartDef comp2){
    int score = 0;

    score += complexityDifference(comp1);
    score += complexityDifference(comp2);
    score += comp1.complexityDifference(comp2);

    return score;
  }

  /**
   * Returns a list of all possible 2-part-decompositions.
   * @param type The RequirementType of the decompositions to search for.
   * @return A list of all possible 2-part-decompositions. The list is sorted according to {@link #compositionComplexity(ASTPartDef, ASTPartDef)}
   */
  public Stream<Pair<ASTPartDef, ASTPartDef>> getDecompositionCandidates(ASTSysMLReqType type){
    var decompositions = new HashMap<Pair<ASTPartDef, ASTPartDef>, Map<Pair<ASTPartDef, ASTPartDef>, Map<ASTPortUsage, ASTPortUsage>>>();
    var parts = PartDefSymbol.getAllPartDefs()
        .filter(p -> p.getRequirementType() == type)
        .map(PartDefSymbol::getAstNode)
        .filter(p -> !p.equals(this))
        .collect(Collectors.toList());

    for (var comp1 : parts) {
      for (var comp2 : parts){
        // Try to calculate mapping.
        // If that fails with an IllegalStateException, (comp1 o comp2)  is not a valid decomposition of reference.
        try {
          var mapping = getDecompositionMapping(comp1.deepClone(), comp2.deepClone());
          decompositions.put(new Pair<>(comp1.deepClone(), comp2.deepClone()), mapping);
        } catch (IllegalStateException ignored) { }
      }
    }

    return new ArrayList<>(decompositions.keySet()).stream()
        .sorted(Comparator.comparingInt(p -> compositionComplexity(p.a, p.b)));
  }

  /**
   * Returns a decomposition that represents "this".
   * @param comp1 The first component of the decomposition.
   * @param comp2 The second component of the decomposition.
   * @throws IllegalStateException If there was no valid decomposition mapping found.
   * @return A decomposition that represents "this".
   */
  protected Map<Pair<ASTPartDef, ASTPartDef>, Map<ASTPortUsage, ASTPortUsage>> getDecompositionMapping(ASTPartDef comp1, ASTPartDef comp2){
    var mappings = new HashMap<Pair<ASTPartDef, ASTPartDef>, Map<ASTPortUsage, ASTPortUsage>>();
    // We don't really care how to exactly connect the components for now.
    // But this will be the place to do it in future.
    return mappings;
  }
}
