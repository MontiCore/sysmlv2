package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;

/**
 * Completes SysMLIdentifier information in the symbol table. This is because
 * SysML does not follow convention of single, unique names and instead choses
 * to have multiple name for a single symbol. Without this, one could not
 * resolve a symbol by its SysMLIdentifier.
 *
 * Example: The SysMLIdentifier for `part <P1> part1;` ist "P1".
 */
public class IdentifierCompletion implements SysMLPartsVisitor2 {

  /**
   * Completes identifier
   */
  @Override
  public void visit(ASTPartUsage node) {
    if(node.isPresentSymbol()) {
      if(node.isPresentSysMLIdentifier()) {
        node.getSymbol().setSysMLIdentifier(node.getSysMLIdentifier().getName());
      }
      else {
        node.getSymbol().setSysMLIdentifierAbsent();
      }
    }
  }

}
