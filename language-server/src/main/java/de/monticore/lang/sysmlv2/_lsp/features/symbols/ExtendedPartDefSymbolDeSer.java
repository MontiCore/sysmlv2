package de.monticore.lang.sysmlv2._lsp.features.symbols;

import de.monticore.lang.sysml4verification._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlv2._symboltable.PartDefSymbolDeSer;
import de.monticore.lang.sysmlparts._symboltable.SysMLPartsSymbols2Json;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2ScopeManager;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExtendedPartDefSymbolDeSer extends PartDefSymbolDeSer {

  @Override
  public String serialize(PartDefSymbol toSerialize, SysMLPartsSymbols2Json s2j) {
    JsonPrinter p = s2j.getJsonPrinter();
    p.beginObject();
    p.member(de.monticore.symboltable.serialization.JsonDeSers.KIND, getSerializedKind());
    p.member(de.monticore.symboltable.serialization.JsonDeSers.NAME, toSerialize.getName());

    // serialize symbolrule attributes
    serializeRequirementType(toSerialize.getRequirementType(), s2j);

    serializeDirectRefinements(toSerialize.getDirectRefinementsList(), s2j);

    SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(gs -> {
      serialize("refinements", toSerialize.getTransitiveRefinements(), s2j);
      var refiners = toSerialize.getTransitiveRefiners();
      serialize("refiners", refiners, s2j);
      serialize("directRefiners", toSerialize.getDirectRefiners(), s2j);
    });

    // serialize spanned scope
    if (toSerialize.getSpannedScope().isExportingSymbols()
        && toSerialize.getSpannedScope().getSymbolsSize() > 0) {
      toSerialize.getSpannedScope().accept(s2j.getTraverser());
    }
    s2j.getTraverser().addTraversedElement(toSerialize.getSpannedScope());

    serializeAddons(toSerialize, s2j);
    p.endObject();

    return p.toString();
  }

  protected void serialize(String key, List<PartDefSymbol> symbolsToStore, SysMLPartsSymbols2Json s2j) {
    var validRefinementExpressions = symbolsToStore.stream()
        .map(this::getSymTypeExpressionOfSymbol)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    SymTypeExpressionDeSer.serializeMember(s2j.getJsonPrinter(), key, validRefinementExpressions);
  }

  private Optional<SymTypeExpression> getSymTypeExpressionOfSymbol(PartDefSymbol symbol){
    return Optional.of(SymTypeExpressionFactory.createTypeObject(symbol.getFullName(), symbol.getEnclosingScope()));
  }
}
