package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartDefSymbol extends PartDefSymbolTOP {

  public PartDefSymbol(String name) {
    super(name);
  }

  /**
   * @return List of all direct refinements that can be resolved. Will not crash for non-resolvable entries.
   */
  public List<PartDefSymbol> getDirectRefinements() {
    return getDirectRefinementsList().stream()
        .filter(SymTypeExpression::hasTypeInfo)
        .map(SymTypeExpression::getTypeInfo)
        .map(TypeSymbol::getFullName)
        .map(name -> getEnclosingScope().resolvePartDef(name))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  /**
   * @return List of all refinements that can be resolved, including transitive ones. Will not crash for non-resolvable
   * entries.
   */
  public List<PartDefSymbol> getTransitiveRefinements() {
    var res = this.getTransitiveRefinementsRecursively(new ArrayList<>());
    // Das "this" sollte nicht rein
    res.remove(this);
    return res;
  }

  /**
   * Recursive helper function with pseudo-state
   *
   * @param visited Already visited part defs
   */
  protected List<PartDefSymbol> getTransitiveRefinementsRecursively(List<PartDefSymbol> visited) {
    // Sich selbst markieren
    visited.add(this);

    // Die finden, die noch in Frage kommen
    var candidates = getDirectRefinements().stream().filter(r -> !visited.contains(r));

    // Rekursiv weiter
    candidates.forEach(p -> p.getTransitiveRefinementsRecursively(visited));

    // Alle haben sich in visited eingetragen
    return visited;
  }

  /**
   * @return List of all direct refiners. Will only return ones that can be resolved, fails silently otherwise.
   * Direct refiners are those that explicitly specify they ("are a refinement of") {@code this}.
   *
   * Inverse of {@link #getDirectRefinements()}.
   */
  public List<PartDefSymbol> getDirectRefiners() {
      return getAllPartDefs()
          .filter(partDef -> partDef.getDirectRefinements().contains(this))
          .distinct()
          .collect(Collectors.toList());
  }

  /**
   * @return List of all direct and transitive refiners. Will only return ones that can be resolved, fails silently
   * otherwise. Compared to {@link #getDirectRefiners()}, this includes transitive relations.
   *
   * Inverse of {@link #getTransitiveRefinements()}.
   */
  public List<PartDefSymbol> getTransitiveRefiners() {
    return getAllPartDefs()
        .filter(partDef -> partDef.getTransitiveRefinements().contains(this))
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * @return true if the given PartDef has the same interface
   * i.e. all ports have one counterpart with the same type and access modifier
   */
  public boolean matchesInterfaceOf(PartDefSymbol symbol) {
    var unmatchedPorts1 = this.getAstNode().getSysMLElementList()
        .stream()
        .filter(p -> p instanceof ASTPortUsage)
        .map(p -> ((ASTPortUsage) p).getSymbol())
        .collect(Collectors.toList());

    var unmatchedPorts2 = symbol.getAstNode().getSysMLElementList()
        .stream()
        .filter(p -> p instanceof ASTPortUsage)
        .map(p -> ((ASTPortUsage) p).getSymbol())
        .collect(Collectors.toList());

    if (unmatchedPorts1.size() != unmatchedPorts2.size()) {
      return false;
    }

    for (var port1 : new ArrayList<>(unmatchedPorts1)) {
      for (var port2 : new ArrayList<>(unmatchedPorts2)) {
        if (port1.matches(port2)) {
          unmatchedPorts1.remove(port1);
          unmatchedPorts2.remove(port2);
          break;
        }
      }
    }
    return unmatchedPorts1.isEmpty() && unmatchedPorts2.isEmpty();
  }

  /**
   * @return Stream of all PartDefs in the given scope and sub scopes
   */
  public static Stream<PartDefSymbol> getAllPartDefs() {
    var gs = SysMLv2Mill.globalScope();
    return getAllPartDefsRecursively(gs);
  }

  /**
   * Recursive helper function with pseudo-state
   */
  protected static Stream<PartDefSymbol> getAllPartDefsRecursively(ISysMLv2Scope scope) {
    var result = scope.getLocalPartDefSymbols().stream();
    result = Stream.concat(result, scope.getSubScopes().stream().flatMap(PartDefSymbol::getAllPartDefsRecursively));
    return result;
  }

  /**
   * Calculates a score for how well this PartDefSymbol refines another PartDefSymbol.
   * This method DOES NOT consider semantics but only structural properties.
   * @return A higher score means higher structural similarity.
   */
  public int getRefinementScore(PartDefSymbol rough) {
    var score = 0;

    // We prefer refinements that go got the next abstraction level
    if (this.getRequirementType() == ASTSysMLReqType.LLR && rough.getRequirementType() == ASTSysMLReqType.LLR) {
        score += 10;
    } else if (this.getRequirementType() == ASTSysMLReqType.MIXED) {
      if (rough.getRequirementType() == ASTSysMLReqType.MIXED) {
        score += 5;
      } else if (rough.getRequirementType() == ASTSysMLReqType.LLR) {
        score += 10;
      }
    } else if (this.getRequirementType() == ASTSysMLReqType.HLR) {
      if (rough.getRequirementType() == ASTSysMLReqType.HLR && rough.getRequirementType() == ASTSysMLReqType.LLR) {
        score += 5;
      } else if (rough.getRequirementType() == ASTSysMLReqType.MIXED) {
        score += 10;
      }
    }


    // Prefer refinements with the same number of connections
    if (rough.getAstNode() != null){
      var connectionUsages = this.getAstNode().getSysMLElementList().stream()
          .filter(e -> e instanceof ASTConnectionUsage)
          .collect(Collectors.toList());

      var refConnectionUsages = rough.getAstNode().getSysMLElementList().stream()
          .filter(e -> e instanceof ASTConnectionUsage)
          .collect(Collectors.toList());

      if (connectionUsages.size() == refConnectionUsages.size()){
        score += 10;
      }
    }
    return score;
  }

  /**
   * Is used to find partDefsSymbols with the same port interface as the given partDefSymbol.
   */
  public List<PartDefSymbol> getRefinementOrRoughCandidates(boolean matchConnectionUsages){
    var parts = PartDefSymbol.getAllPartDefs()
        .filter(p -> p.matchesInterfaceOf(this))
        .filter(p -> p.getAstNode().getRefinements().stream()
            .noneMatch(r -> r.getAstNode().equals(this.getAstNode())))
        .filter(p -> !p.getName().equals(this.getName()));

    if (matchConnectionUsages){
      var connectionUsages = this.getAstNode().getSysMLElements(ASTConnectionUsage.class);

      parts = parts.filter(p -> {
        var refConnectionUsages = p.getAstNode().getSysMLElements(ASTConnectionUsage.class);
        return refConnectionUsages.size() == connectionUsages.size();
      });
    }

    return parts.collect(Collectors.toList());
  }

  public List<PartDefSymbol> getBasicRefinementCandidates(){
    var result = getRefinementOrRoughCandidates(false);
    result = result.stream()
        .filter(p -> p.getAstNode().getSysMLElements(ASTConnectionUsage.class).size() == 0)
        .collect(Collectors.toList());

    return result;
  }
}
