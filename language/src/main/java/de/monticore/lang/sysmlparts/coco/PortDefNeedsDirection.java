/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlparts.coco;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

/**
 * MontiBelle is based on FOCUS which in turn is based on bundles made of channels. For ports to be mapped to channels
 * and bundles, one needs to be able to determine whether the port is part of the in- or output bundle.
 */
public class PortDefNeedsDirection implements SysMLPartsASTPortDefCoCo {

  @Override
  public void check(ASTPortDef ast) {
    // Attributes with a direction are channels
    for (var attr: ast.getSysMLElementList().stream()
        .filter(e -> e instanceof ASTAttributeUsage)
        .map(e -> (ASTAttributeUsage)e)
        .filter(e -> !(e.isPresentSysMLFeatureDirection())).collect(Collectors.toList()))
    {
      if(!attr.isPresentSysMLFeatureDirection()) {
        Log.warn("Attributes of port definitions without directions are ignored",
            attr.get_SourcePositionStart(),
            attr.get_SourcePositionEnd());
      }
      else if(attr.getSysMLFeatureDirection() == ASTSysMLFeatureDirection.INOUT) {
        // MontiBelle could support this in the future
        Log.warn("Attributes of port definitions with direction inout are ignored",
            attr.get_SourcePositionStart(),
            attr.get_SourcePositionEnd());
      }
    }
  }

}
