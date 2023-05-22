package de.monticore.lang.sysmlparts._symboltable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartDefSymbol extends PartDefSymbolTOP {

  public PartDefSymbol(String name) {
    super(name);
  }

  /**
   * @return List of all direct refinements that can be found in the given scope.
   */
  public List<PartDefSymbol> getDirectRefinements(ISysMLv2Scope scope) {
    return getDirectRefinementsList().stream()
        .map(symType -> scope.resolvePartDef(symType.getTypeInfo().getFullName()))
        .map(opt -> opt.orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * @return List of all refinements, including transitive ones, that can be found in the given scope.
   */
  public List<PartDefSymbol> getRefinements(ISysMLv2Scope scope){
    ListMultimap<PartDefSymbol, PartDefSymbol> result = ArrayListMultimap.create();
    result.put(this, this);
    traverseRefinements(this, p -> filterCyclicRelation(this, p, result), scope);
    result.remove(this, this);
    return result.get(this).stream().distinct().collect(Collectors.toList());
  }

  public static void filterCyclicRelation(PartDefSymbol origin, Pair<PartDefSymbol, PartDefSymbol> p,
                                          ListMultimap<PartDefSymbol, PartDefSymbol> result){
    if (result.containsEntry(origin, p.b) && result.containsEntry(p.b,origin)){
      // Signal traverser to not continue with this refinement
      throw new IllegalStateException("Cyclic refinement detected: " + origin.getName() + " <-> " + p.a.getName());
    }
    result.put(origin, p.b);
    result.put(p.a, p.b);
  }

  private static void traverseRefinements(PartDefSymbol partDef, Consumer<Pair<PartDefSymbol, PartDefSymbol>> consumer, ISysMLv2Scope scope){
    // Direct refinements
    List<PartDefSymbol> nonCyclicRefinement = new ArrayList<>();
    for (var refinement : partDef.getDirectRefinements(scope)){
      if (refinement == partDef){
        continue;
      }
      try {
        consumer.accept(new Pair<>(partDef, refinement));
        nonCyclicRefinement.add(refinement);
      } catch (IllegalStateException e){
        // Ignore cyclic refinements / refiners
      }
    }

    // Transitive refinements
    for (var refinement : nonCyclicRefinement){
      traverseRefinements(refinement, consumer, scope);
    }
  }

  /**
   * @return List of all direct refiners that can be found in the given scope
   */
  public List<PartDefSymbol> getDirectRefiners(ISysMLv2Scope scope) {
    return getAllPartDefs(scope)
        .filter(partDef -> !partDef.getDirectRefinements(scope).isEmpty())
        .filter(partDef -> partDef.getDirectRefinements(scope)
            .stream().anyMatch(ref -> ref.equals(this)))
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * @return List of all direct and transitive refiners
   */
  public List<PartDefSymbol> getRefiners(ISysMLv2Scope scope){
    return getAllPartDefs(scope)
        .filter(partDef -> !partDef.getRefinements(scope).isEmpty())
        .filter(partDef -> partDef.getRefinements(scope)
            .stream().anyMatch(ref -> ref.equals(this)))
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * @return true if the given PartDef has the same interface
   * i.e. all ports have one counterpart with the same type and access modifier
   */
  public boolean matchesInterfaceOf(PartDefSymbol symbol){
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

    if (unmatchedPorts1.size() != unmatchedPorts2.size()){
      return false;
    }

    for (var port1 : new ArrayList<>(unmatchedPorts1)){
      for (var port2 : new ArrayList<>(unmatchedPorts2)){
        if (matchPort(port1, port2)){
          unmatchedPorts1.remove(port1);
          unmatchedPorts2.remove(port2);
          break;
        }
      }
    }
    return unmatchedPorts1.isEmpty() && unmatchedPorts2.isEmpty();
  }

  /**
   * @return true if the given ports have the same type and access modifier
   */
  private static boolean matchPort(PortUsageSymbol port1, PortUsageSymbol port2){
    if (port1.getAccessModifier() != port2.getAccessModifier() ||
        port1.getTypesList().size() != port2.getTypesList().size()){
      return false;
    }
    for (int i = 0; i < port1.getTypesList().size(); i++){
      if (!Objects.equals(port1.getTypes(i).getTypeInfo().getFullName(), port2.getTypes(i).getTypeInfo().getFullName())){
        return false;
      }
    }
    return true;
  }

  /**
   * @return Stream of all PartDefs in the given scope and sub scopes
   */
  public static Stream<PartDefSymbol> getAllPartDefs(ISysMLv2Scope scope) {
    Stream<PartDefSymbol> result = Stream.empty();
    if (scope != null) {
      result = scope.getPartDefSymbols().values().stream();
      result = Stream.concat(result, scope.getSubScopes().stream().flatMap(PartDefSymbol::getAllPartDefs));
    }
    return result;
  }
}
