/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts.symboltable.adapters.PartDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

/**
 * CoCo1: Jeder in "part name:Typ" verwendete Typ muss eine existierende Part-Definition sein.
 */
public class PartTypeDefinitionExistsCoCo implements SysMLPartsASTPartUsageCoCo {

  protected String printPartType(ASTMCType type) {
    return type.printType();
  }

  @Override
  public void check(ASTPartUsage node) {
    var superTypes = node.streamSpecializations()
        .filter(s -> s instanceof ASTSysMLTyping)
        .flatMap(ASTSpecialization::streamSuperTypes)
        .map(t ->
            ((ISysMLv2Scope)t.getEnclosingScope()).resolveType(printPartType(t))
        )
        // nicht-vorhandene in  SpecializationExistsTC3 gecheckt
        .filter(t -> t.isPresent())
        .map(t -> t.get())
        .collect(Collectors.toList());

    for (var type : superTypes) {
      if(!(type instanceof PartDef2TypeSymbolAdapter)) {
        Log.error(
            "0x10AA1 Types of part usages must be part definitions",
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd()
        );
      }
    }
  }
}
