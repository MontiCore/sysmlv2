package de.monticore.lang.sysmlv2.symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.*;

import java.util.List;

import static de.monticore.lang.sysmlbasis.symboltable.SerializationUtil.deserializeListMember;

public class FieldSymbolDeSer extends de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer {
  /**
   * See {@link de.monticore.lang.sysmlbasis.symboltable.SerializationUtil#deserializeListMember}
   */
  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson) {
    if (symbolJson.hasMember("type")) {
      JsonElement e = symbolJson.getMember("type");
      if (e.isJsonObject()) {
        JsonObject o = e.getAsJsonObject();
        ISysMLv2GlobalScope enclosingScope = SysMLv2Mill.globalScope();

        switch (JsonDeSers.getKind(o)) {
          case SymTypePrimitiveDeSer.SERIALIZED_KIND: {
            if (o.hasStringMember("primitiveName")) {
              String constName = o.getStringMember("primitiveName");
              return SymTypeExpressionFactory.createPrimitive(enclosingScope.resolveType(constName).get());
            }
          }
          case SymTypeOfObjectDeSer.SERIALIZED_KIND: {
            if (o.hasStringMember("objName")) {
              String objName = o.getStringMember("objName");
              return SymTypeExpressionFactory.createTypeObject(objName, enclosingScope);
            }
          }
          case SymTypeOfGenericsDeSer.SERIALIZED_KIND: {
            if (o.hasStringMember("typeConstructorFullName") &&
                o.hasArrayMember("arguments")) {
              String typeConstructorFullName = o.getStringMember("typeConstructorFullName");
              List<SymTypeExpression> arguments = deserializeListMember("arguments", o, enclosingScope);

              return SymTypeExpressionFactory
                  .createGenerics(typeConstructorFullName, enclosingScope, arguments);
            }
          }
        }
      }
    }
    return SymTypeExpressionFactory.createObscureType();
  }
}
