/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTSpecializationCoCo;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTSysMLTypingCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class OneCardinality implements SysMLBasisASTSysMLTypingCoCo {
  @Override
  public void check (ASTSysMLTyping node) {
    if (node.isPresentCardinality()) {
      var card = node.getCardinality();

      // [*]
      if(card.isMany()) {
        Log.warn("0x10030 Cardiniality will be ignored in verification");
      }
      // [n..*]
      else if(card.isNoUpperLimit()) {
        Log.warn("0x10031 Cardiniality will be ignored in verification");
      }
      // [n..m]
      else if(card.isPresentLowerBoundLit() && card.isPresentUpperBoundLit()) {
        if(card.getUpperBound() < card.getLowerBound()) {
          Log.error("0x10032 Upper bound is below lower bound");
        }
        else if(card.getUpperBound() != card.getLowerBound()) {
          Log.warn("0x10033 Cardiniality will be ignored in verification");
        }
      }
    }
  }
}
