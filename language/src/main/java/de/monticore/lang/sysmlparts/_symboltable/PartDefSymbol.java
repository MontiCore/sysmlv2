package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;

import java.util.stream.Stream;

public class PartDefSymbol extends PartDefSymbolTOP {

  public PartDefSymbol(String name) {
    super(name);
  }

  private Stream<PartDefSymbol> getRefiners(ISysMLv2Scope scope) {
    Stream<PartDefSymbol> result = Stream.empty();

    result = Stream.concat(result, scope.getPartDefSymbols().values().stream()
        .filter(partDef -> !partDef.getAstNode().getRefinements().isEmpty()));

    result = Stream.concat(result, scope.getSubScopes().stream()
        .flatMap(this::getRefiners));

    result = result.filter(partDef -> partDef.getAstNode().getRefinements().stream()
        .anyMatch(ref -> ref.getName().equals(this.getName())));

    return result;
  }

  public Stream<PartDefSymbol> getRefiners() {
    return getRefiners(SysMLv2Mill.globalScope());
  }
}
