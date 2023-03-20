/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks top-level state-usages to be exhibited and be belonging to a part definition.
 * Reason being, that nested state usages are used in sysml to denote both nested state machines and states:
 *
 * Example:
 * <code>
 * exhibited state Name {
 *   state S;
 *   state T;
 *   transition first S then T;
 *   state Nested {
 *     state C;
 *   }
 * }
 * </code>
 */
public class WarnNonExhibited implements SysMLStatesASTStateUsageCoCo {

  private int depth = 0;

  /** Warns about non-exhibited state usages or state usages that are not direct descendants of part definitions */
  @Override
  public void check(ASTStateUsage node) {
    if(depth == 0) {
      if(!node.isExhibited()) {
        Log.warn("Non-exhibited state usages will be ignored for behavior specification",
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd());
      }
      else if(!parentIsPartDef(node)) {
        Log.warn("Only exhibited state usages within part definition will be considered for behavior specification",
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd());
      }
    }
  }

  private boolean parentIsPartDef(ASTNode node) {
    return node.getEnclosingScope() != null && node.getEnclosingScope().isPresentAstNode()
        && node.getEnclosingScope().getAstNode() instanceof ASTPartDef;
  }

  @Override
  public void visit(ASTStateUsage node) {
    SysMLStatesASTStateUsageCoCo.super.visit(node);
    depth++;
  }

  @Override
  public void endVisit(ASTStateUsage node) {
    SysMLStatesASTStateUsageCoCo.super.endVisit(node);
    depth--;
  }

}
