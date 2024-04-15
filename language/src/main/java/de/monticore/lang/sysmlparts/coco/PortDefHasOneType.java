/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts.coco;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

/**
 * SysML allows for variables in PortDefs to have multiple (including 0) types - MontiBelle only supports exactly one
 * type
 */
public class PortDefHasOneType implements SysMLPartsASTPortDefCoCo {

  @Override
  public void check(ASTPortDef ast) {
    // Attributes with a direction are channels
    for (var attr: ast.getSysMLElementList().stream()
        .filter(e -> e instanceof ASTAttributeUsage)
        .map(e -> (ASTAttributeUsage)e)
        .filter(e -> (e.isPresentSysMLFeatureDirection()))
        .collect(Collectors.toList()))
    {
      if (attr.getSpecializationList().stream().noneMatch(s -> s instanceof ASTSysMLTyping)) {
        Log.error("0xFF006 Channels do need a type", attr.get_SourcePositionStart(), attr.get_SourcePositionEnd());
      }
    }
  }

}
