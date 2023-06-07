/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PartBehaviorCoCo implements SysMLPartsASTPartDefCoCo {
  @Override
  public void check(ASTPartDef node) {
    int constraintAvailable = node.getSysMLElements(ASTConstraintUsage.class).size() > 0 ? 1 : 0;
    int automatonAvailable = node.getSysMLElements(ASTStateUsage.class).size() > 0 ? 1 : 0;
    int decompositionAvailable = node.getSysMLElements(ASTPartUsage.class).size() > 0 ? 1 : 0;

    if((constraintAvailable + automatonAvailable + decompositionAvailable) == 0){
      Log.warn("0xA70003 Part " + node.getName() + " has no explicit behavior!");
    }
    if((constraintAvailable + automatonAvailable + decompositionAvailable) > 1){
      Log.warn("0xA70004 Part " + node.getName() + "should contain exactly one "
          + "behavior specification (constraint, automaton or composition)!");
    }

  }
}
