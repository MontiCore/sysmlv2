package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTAnonymousReference;
import de.monticore.lang.sysmlbasis._ast.ASTAnonymousUsage;
import de.monticore.lang.sysmlbasis._ast.ASTModifier;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;

/**
 * Sets the isIn / isOut properties of symbols based on Modifiers parsed to the AST.
 */
public class DirectionCompleter implements SysMLBasisVisitor2, SysMLPartsVisitor2 {

  /**
   * Returns whether this is an input. Defaults to {@code true} if no direction was explicitly set. The keyword "inout"
   * yields true.
   */
  protected boolean isIn(ASTModifier modifier) {
    return modifier.isIn() || !modifier.isOut() && !modifier.isReturn();
  }

  /**
   * Returns whether this is an input. Defaults to {@code false} if no direction was explicitly set. The keyword "inout"
   * yields true. The keyword "return" is treated as output.
   */
  protected boolean isOut(ASTModifier modifier) {
    return modifier.isOut() || modifier.isInout() || modifier.isReturn();
  }

  @Override
  public void visit(ASTAnonymousUsage node) {
    if(node.isPresentSymbol()) {
      node.getSymbol().setIn(isIn(node.getModifier()));
      node.getSymbol().setOut(isOut(node.getModifier()));
    }
  }

  @Override
  public void visit(ASTAnonymousReference node) {
    if(node.isPresentSymbol()) {
      node.getSymbol().setIn(isIn(node.getModifier()));
      node.getSymbol().setOut(isOut(node.getModifier()));
    }
  }

  @Override
  public void visit(ASTAttributeUsage node) {
    if(node.isPresentSymbol()) {
      node.getSymbol().setIn(isIn(node.getModifier()));
      node.getSymbol().setOut(isOut(node.getModifier()));
    }
  }

}
