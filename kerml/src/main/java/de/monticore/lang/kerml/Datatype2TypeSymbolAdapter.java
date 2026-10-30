package de.monticore.lang.kerml;

import de.monticore.lang.kerml._symboltable.IKerMLScope;
import de.monticore.lang.kermlelements._ast.ASTFeature;
import de.monticore.lang.kermlelements._symboltable.DatatypeSymbol;
import de.monticore.lang.kermlelements._symboltable.IKerMLElementsScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;

import java.util.List;
import java.util.stream.Collectors;

public class Datatype2TypeSymbolAdapter extends TypeSymbol {

  private DatatypeSymbol adaptee;

  private IBasicSymbolsScope spannedScope;

  public Datatype2TypeSymbolAdapter(DatatypeSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;

    // typeVars hinzufügen
    var typeVars = convertFeature2TypeVar(adaptee.getSpannedScope());
    this.spannedScope = BasicSymbolsMill.scope();
    this.spannedScope.setSpanningSymbol(this);
    typeVars.forEach(typeVar -> this.spannedScope.add(typeVar));
  }

  @Override
  public IBasicSymbolsScope getSpannedScope() {
    return this.spannedScope;
  }

  /**
   * Sammelt features mit Type "Base::Anything" und konvertiert sie zu
   * TypeVariablen. Es handelt sich um eine Heuristik: KerML kennt keine
   * generischen Typen wie sie es in MontiCore gibt. Die beste Erkennung von
   * etwas "generischem" scheint bisher der Typ eines Features zu sein.
   */
  private List<TypeVarSymbol> convertFeature2TypeVar(IKerMLElementsScope scope) {
    var features = scope.getLocalFeatureSymbols();
    var anythingFeatures = features.stream()
        .filter(f -> f.getTypesList().stream()
            .anyMatch(t -> t.printFullName().equals("Anything"))
        );
    var typeVars = anythingFeatures.map(f ->
        BasicSymbolsMill.typeVarSymbolBuilder()
          .setName(f.getName())
          .setSpannedScope(BasicSymbolsMill.scope()) // muss gesetzt werden
          .build()
    );
    return typeVars.collect(Collectors.toList());
  }

}
