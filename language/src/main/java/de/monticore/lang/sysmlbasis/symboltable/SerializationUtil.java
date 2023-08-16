package de.monticore.lang.sysmlbasis.symboltable;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.*;

import java.util.ArrayList;
import java.util.List;

public class SerializationUtil {
  /**
   * This method injects the appropriate global scope into the created type expression list. We do this because the default
   * {@link SymTypeExpressionDeSer} uses the BasicSymbolsMill4SysMLv2 global scope injection which creates buggy behaviour when
   * a different mill inheriting from BasicSymbols is initialized before deserialization.
   */
  public static List<SymTypeExpression> deserializeListMember(String memberName, JsonObject json, IBasicSymbolsScope gs) {
    List<SymTypeExpression> result = new ArrayList<>();

    if (json.hasMember(memberName)) {
      for (JsonElement e : json.getArrayMember(memberName)) {
        if (e.isJsonObject()) {
          JsonObject o = e.getAsJsonObject();

          switch (JsonDeSers.getKind(o)) {
            case SymTypePrimitiveDeSer.SERIALIZED_KIND: {
              if (o.hasStringMember("primitiveName")) {
                String constName = o.getStringMember("primitiveName");
                result.add(SymTypeExpressionFactory.createPrimitive(gs.resolveType(constName).get()));
              }
            } break;

            case SymTypeOfObjectDeSer.SERIALIZED_KIND: {
              if (o.hasStringMember("objName")) {
                String objName = o.getStringMember("objName");
                // TODO why not just resolve for actual type instead of setting a surrogate in the global scope?
                result.add(SymTypeExpressionFactory.createTypeObject(objName, gs));
              }
            } break;

            case SymTypeOfGenericsDeSer.SERIALIZED_KIND: {
              if (o.hasStringMember("typeConstructorFullName") &&
                  o.hasArrayMember("arguments")) {
                String typeConstructorFullName = o.getStringMember("typeConstructorFullName");
                List<SymTypeExpression> arguments = deserializeListMember("arguments", o, gs);

                result.add(SymTypeExpressionFactory
                    .createGenerics(typeConstructorFullName, gs, arguments));
              }
            } break;
          }
        }
      }
    }
    return result;
  }
}
