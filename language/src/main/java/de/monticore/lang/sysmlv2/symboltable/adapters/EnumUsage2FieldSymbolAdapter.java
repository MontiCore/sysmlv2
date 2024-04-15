package de.monticore.lang.sysmlv2.symboltable.adapters;

import de.monticore.lang.sysmlparts._symboltable.EnumUsageSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

public class EnumUsage2FieldSymbolAdapter extends FieldSymbol {

  protected EnumUsageSymbol adaptee;

  protected OOTypeSymbol type;

  public EnumUsage2FieldSymbolAdapter(String name) {
    super(name);
  }

  /**
   * @param adaptee The enum usage to be adapted to field symbol
   * @param type    The enum definition that the usage belongs to/is typed by
   */
  public EnumUsage2FieldSymbolAdapter(EnumUsageSymbol adaptee, OOTypeSymbol type) {
    super(adaptee.getName());
    this.adaptee = adaptee;
    this.type = type;
  }

  @Override
  public SymTypeExpression getType() {
    return SymTypeExpressionFactory.createTypeObject(type);
  }
}
