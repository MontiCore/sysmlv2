package de.monticore.lang.sysmlimportsandpackages._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTModifierBuilder;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SysMLImportsAndPackagesDeSer extends SysMLImportsAndPackagesDeSerTOP {
  @Override
  protected void serializeSysMLImports(
      List<ASTSysMLImportStatement> sysMLImports, SysMLImportsAndPackagesSymbols2Json s2j) {
    JsonPrinter jsonPrinter = s2j.getJsonPrinter();
    jsonPrinter.beginArray("imports");
    for (ASTSysMLImportStatement sysMLImport : sysMLImports) {
      var json = JsonElementFactory.createJsonObject();
      jsonPrinter.addToArray(json);
      json.putMember("isStar", JsonElementFactory.createJsonBoolean(sysMLImport.isStar()));
      json.putMember("isRecursive", JsonElementFactory.createJsonBoolean(sysMLImport.isRecursive()));
      var nameParts = JsonElementFactory.createJsonArray();
      json.putMember("nameParts", nameParts);
      for (var part : sysMLImport.getMCQualifiedName().getPartsList()) {
        nameParts.add(JsonElementFactory.createJsonString("\"" + part + "\""));
      }
    }
    jsonPrinter.endArray();
  }

  @Override
  protected List<ASTSysMLImportStatement> deserializeSysMLImports(JsonObject scopeJson) {
    return scopeJson
        .getArrayMemberOpt("imports")
        .stream()
        .flatMap(Collection::stream)
        .map(statement -> SysMLv2Mill.sysMLImportStatementBuilder()
            .setMCQualifiedName(SysMLv2Mill.sysMLQualifiedNameBuilder()
                .setPartsList(statement
                    .getAsJsonObject()
                    .getMember("nameParts")
                    .getAsJsonArray()
                    .getValues()
                    .stream()
                    .map(Objects::toString)
                    .toList())
                .build())
            .setStar(statement.getAsJsonObject().getMember("isStar").getAsJsonBoolean().getValue())
            .setRecursive(statement.getAsJsonObject().getMember("isRecursive").getAsJsonBoolean().getValue())
            .setModifier(new ASTModifierBuilder().setPublic(true).build())
            .build())
        .toList();
  }
}
