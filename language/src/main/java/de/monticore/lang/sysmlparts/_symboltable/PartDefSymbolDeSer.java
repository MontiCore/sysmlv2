package de.monticore.lang.sysmlparts._symboltable;

import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.symboltable.serialization.json.JsonObject;

public class PartDefSymbolDeSer extends PartDefSymbolDeSerTOP {

  @Override
  protected void serializeRequirementType(ASTSysMLReqType requirementType, SysMLPartsSymbols2Json s2j) {
    // RequirementType Classification is not supposed to appear in serialized models.
  }

  @Override
  protected ASTSysMLReqType deserializeRequirementType(JsonObject symbolJson) {
    // RequirementType Classification is done by completer
    return null;
  }


}
