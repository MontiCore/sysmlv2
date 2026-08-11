package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTAnonymousReference;
import de.monticore.lang.sysmlbasis._ast.ASTAnonymousUsage;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.umlmodifier._ast.ASTModifier;

/**
 * Sets the isIn / isOut properties of symbols based on Modifiers parsed to the AST.
 */
public class DirectionCompleter implements SysMLBasisVisitor2, SysMLPartsVisitor2 {

  /**
   * Returns whether this is an input. Defaults to {@code true} if no direction was explicitly set. The keyword "inout"
   * yields true.
   */
  protected boolean isIn(ASTModifier modifier) {
    if (modifier instanceof de.monticore.lang.sysmlv2._ast.ASTModifier) {
      var sysMLModifier = (de.monticore.lang.sysmlv2._ast.ASTModifier) modifier;
      return sysMLModifier.isIn() || !sysMLModifier.isOut() && !sysMLModifier.isReturn();
    }
    return true;
  }

  /**
   * Returns whether this is an input. Defaults to {@code false} if no direction was explicitly set. The keyword "inout"
   * yields true. The keyword "return" is treated as output.
   */
  protected boolean isOut(ASTModifier modifier) {
    if (modifier instanceof de.monticore.lang.sysmlv2._ast.ASTModifier) {
      var sysMLModifier = (de.monticore.lang.sysmlv2._ast.ASTModifier) modifier;
      return sysMLModifier.isOut() || sysMLModifier.isInout() || sysMLModifier.isReturn();
    }
    return false;
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
