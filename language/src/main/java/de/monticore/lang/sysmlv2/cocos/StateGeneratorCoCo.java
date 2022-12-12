package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTDecideAction;
import de.monticore.lang.sysmlactions._ast.ASTForkAction;
import de.monticore.lang.sysmlactions._ast.ASTJoinAction;
import de.monticore.lang.sysmlactions._ast.ASTLoopAction;
import de.monticore.lang.sysmlactions._ast.ASTMergeAction;
import de.monticore.lang.sysmlactions._ast.ASTSysMLFirst;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class StateGeneratorCoCo implements SysMLStatesASTStateUsageCoCo {

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that all super types (specializations) exist. They might be state definitions or usages.
   */
  @Override
  public void check(ASTStateUsage node) {

    if(node.isParalled()) {
      int parallelStates = 0;
        for (ASTSysMLElement x : node.getSysMLElementList()) {
          if(x instanceof ASTStateUsage) {
            parallelStates++;
          }
        }
        if(parallelStates < 2) {
          Log.error("StateUsage " + node.getName() + " has " + parallelStates + " \"StateUsages\" , but needs at least 2.");
        }
      }
    }
  }

