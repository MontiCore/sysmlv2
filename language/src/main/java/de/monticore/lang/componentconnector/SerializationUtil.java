package de.monticore.lang.componentconnector;

import de.monticore.lang.componentconnector._symboltable.ComponentConnectorSymbols2Json;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbolDeSer;
import de.monticore.lang.componentconnector._symboltable.MildInstanceSymbol;
import de.monticore.lang.componentconnector._symboltable.MildInstanceSymbolDeSer;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbol;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbolDeSer;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer;
import de.monticore.symbols.compsymbols._symboltable.CompSymbolsSymbols2Json;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolDeSer;
import de.monticore.symbols.compsymbols._symboltable.PortSymbolDeSer;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json;

public class SerializationUtil {

  /**
   * Sets up the neccessary serializers to be able to serialize adapted symbols
   * as their base, e.g., MildComponentSymbols are printed as ComponentSymbols.
   * This is done for cross-compatibility between tools.
   */
  public static void setupComponentConnectorSerialization() {
    MildComponentSymbolDeSer myComponentSymbolDeSer =
        new MildComponentSymbolDeSer() {
      ComponentTypeSymbolDeSer delegate = new ComponentTypeSymbolDeSer();

      @Override
      public String serialize(MildComponentSymbol toSerialize,
                              ComponentConnectorSymbols2Json s2j) {
        return delegate.serialize(toSerialize,
            new CompSymbolsSymbols2Json(s2j.getTraverser(),
                s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers().put(
        "de.monticore.lang.componentconnector._symboltable"
            + ".MildComponentSymbol", myComponentSymbolDeSer);

    MildPortSymbolDeSer myPortSymbolDeSer = new MildPortSymbolDeSer() {
      PortSymbolDeSer delegate = new PortSymbolDeSer();

      @Override
      public String serialize(MildPortSymbol toSerialize,
                              ComponentConnectorSymbols2Json s2j) {
        return delegate.serialize(toSerialize,
            new CompSymbolsSymbols2Json(s2j.getTraverser(),
                s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers().put(
        "de.monticore.lang.componentconnector._symboltable.MildPortSymbol",
        myPortSymbolDeSer);

    MildInstanceSymbolDeSer mySubcomponentSymbolDeSer =
        new MildInstanceSymbolDeSer() {
      SubcomponentSymbolDeSer delegate = new SubcomponentSymbolDeSer();

      @Override
      public String serialize(MildInstanceSymbol toSerialize,
                              ComponentConnectorSymbols2Json s2j) {
        return delegate.serialize(toSerialize,
            new CompSymbolsSymbols2Json(s2j.getTraverser(),
                s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers().put(
        "de.monticore.lang.componentconnector._symboltable"
            + ".MildInstanceSymbol", mySubcomponentSymbolDeSer);

    FieldSymbolDeSer myFieldSymbolDeSer = new FieldSymbolDeSer() {
      VariableSymbolDeSer delegate = new VariableSymbolDeSer();

      @Override
      public String serialize(FieldSymbol toSerialize,
                              OOSymbolsSymbols2Json s2j) {
        return delegate.serialize(toSerialize,
            new BasicSymbolsSymbols2Json(s2j.getTraverser(),
                s2j.getJsonPrinter()));
      }
    };

    SysMLv2Mill.globalScope().getSymbolDeSers().put(
        "de.monticore.symbols.oosymbols._symboltable.FieldSymbol",
        myFieldSymbolDeSer);

  }

}
