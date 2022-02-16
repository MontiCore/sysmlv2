package schrott.types.check;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;

import java.util.ArrayList;
import java.util.List;

public class SymTypeOfFunction extends SymTypeExpression {

  protected SymTypeExpression returnType;

  protected List<SymTypeExpression> parameters;

  public SymTypeOfFunction(TypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
  }

  public SymTypeOfFunction(TypeSymbol typeSymbol, FunctionSymbol functionSymbol) {
    this.typeSymbol = typeSymbol;
    returnType = functionSymbol.getReturnType();
    parameters = new ArrayList<>();
    for (VariableSymbol var : functionSymbol.getParameterList()) {
      parameters.add(var.getType());
    }
  }

  public SymTypeExpression getReturnType() {
    return this.returnType;
  }

  public List<SymTypeExpression> getParameters() {
    return this.parameters;
  }

  public String getName() {
    return typeSymbol.getFullName();
  }

  @Override
  public String print() {
    return typeSymbol.getFullName();
  }

  @Override
  public String printFullName() {
    return typeSymbol.getFullName();
  }

  @Override
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, "schrott.types.check.SymTypeOfFunction");
    jp.member("objName", print());
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public SymTypeExpression deepClone() {
    TypeSymbol typeSymbol = new TypeSymbolSurrogate(this.typeSymbol.getName());
    typeSymbol.setEnclosingScope(this.typeSymbol.getEnclosingScope());
    return new SymTypeOfObject(typeSymbol);
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (!(sym instanceof SymTypeOfFunction)) {
      return false;
    }
    SymTypeOfFunction symCon = (SymTypeOfFunction) sym;
    if (this.typeSymbol == null || symCon.typeSymbol == null) {
      return false;
    }
    if (!this.typeSymbol.getEnclosingScope().equals(symCon.typeSymbol.getEnclosingScope())) {
      return false;
    }
    if (!this.typeSymbol.getName().equals(symCon.typeSymbol.getName())) {
      return false;
    }
    return this.print().equals(symCon.print());
  }
}
