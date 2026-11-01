package de.monticore.lang.kerml;

import de.monticore.lang.kermlelements._ast.ASTFeature;
import de.monticore.lang.kermlelements._ast.ASTKerMLTyping;
import de.monticore.lang.kermlelements._visitor.KerMLElementsVisitor2;
import de.monticore.types3.TypeCheck3;

/**
 * Setzt SymTypes am Symbol indem es den TypeCheck bittet Type des MCType des
 * ASTs zu berechnen.
 */
public class KerMLTypesCompleter implements KerMLElementsVisitor2 {

  @Override
  public void visit(ASTFeature node) {
    for (var specialization : node.getSpecializationList()) {
      if (specialization instanceof ASTKerMLTyping) {
        var typing = (ASTKerMLTyping) specialization;
        for (var type : typing.getSuperTypesList()) {
          node.getSymbol().addTypes(TypeCheck3.symTypeFromAST(type));
        }
      }
    }
  }
}
