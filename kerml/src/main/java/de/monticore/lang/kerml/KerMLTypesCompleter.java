package de.monticore.lang.kerml;

import de.monticore.lang.kermlelements._ast.ASTFeature;
import de.monticore.lang.kermlelements._visitor.KerMLElementsVisitor2;
import de.monticore.types3.TypeCheck3;

import java.util.List;

/**
 * Setzt SymTypes am Symbol indem es den TypeCheck bittet Type des MCType des
 * ASTs zu berechnen.
 */
public class KerMLTypesCompleter implements KerMLElementsVisitor2 {

  @Override
  public void visit(ASTFeature node) {
    var symType = TypeCheck3.symTypeFromAST(node.getType());
    node.getSymbol().addAllTypes(List.of(symType));
  }
}
