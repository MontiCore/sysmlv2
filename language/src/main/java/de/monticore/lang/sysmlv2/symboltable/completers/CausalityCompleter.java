package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._ast.ASTSysMLCausality;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;

/**
 * Berechnet und vervollständigt die Kausalität der PortUsages
 * anhand der besitzenden (umliegenden) PartDefinition.
 */
public class CausalityCompleter implements SysMLPartsVisitor2 {
  @Override
  public void visit(ASTPortUsage node) {
    if (node.getEnclosingScope().isPresentSpanningSymbol()) {
      var enclosingSymbol = node.getEnclosingScope().getSpanningSymbol();
      if(enclosingSymbol instanceof PartDefSymbol && enclosingSymbol.isPresentAstNode()) {
        var ast = (ASTPartDef) enclosingSymbol.getAstNode();
        node.getSymbol().setStrong(
            ast.getSysMLElements(ASTSysMLCausality.class).stream().noneMatch(ASTSysMLCausality::isInstant)
        );
      }
    }
  }
}
