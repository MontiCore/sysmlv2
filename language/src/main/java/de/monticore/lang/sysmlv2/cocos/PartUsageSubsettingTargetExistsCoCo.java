package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts.symboltable.adapters.PartUsage2VariableSymbolAdapter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Collectors;

/**
 * Checks that every subsetting target used in a PartUsage
 * resolves to another PartUsage.
 */
public class PartUsageSubsettingTargetExistsCoCo implements SysMLPartsASTPartUsageCoCo {

  protected String printTarget(ASTMCType type) {
    return type.printType();
  }

  @Override
  public void check(ASTPartUsage node) {
    var invalidTargets = node.streamSpecializations()
        .filter(s -> s instanceof ASTSysMLSubsetting)
        .map(ASTSysMLSubsetting.class ::cast)
        .flatMap(ASTSysMLSubsetting::streamSuperTypes)
        .filter(t ->
            node.getEnclosingScope().resolveVariable(printTarget(t))
            .filter(PartUsage2VariableSymbolAdapter.class::isInstance)
            .isEmpty()
        )
        .collect(Collectors.toList());

    for (var target : invalidTargets) {
      Log.error(
          "0x10AA9 The subsetting target \"" + printTarget(target)
              + "\" does not resolve to a part usage.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }
}
