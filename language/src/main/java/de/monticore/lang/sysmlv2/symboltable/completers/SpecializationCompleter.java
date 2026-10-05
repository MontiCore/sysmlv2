/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._symboltable.SysMLDefinitionSymbol;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlv2._ast.ASTSysMLSpecialization;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class SpecializationCompleter implements SysMLBasisVisitor2 {
  @Override
  public void visit(ASTSpecialization node) {
    // Setzt das defining Symbol aller MCTypes der jeweiligen Specialization
    node.streamSuperTypes().forEach(s -> {
      String typeName;
      if(s instanceof ASTMCGenericType) {
        typeName = ((ASTMCGenericType) s).printWithoutTypeArguments();
      }
      else {
        typeName = s.printType();
      }

      var typeSymbol = node.getEnclosingScope().resolveType(typeName);
      if(typeSymbol.isPresent()) {
        s.setDefiningSymbol(typeSymbol.get());

        if(node instanceof ASTSysMLSpecialization
            && node.getEnclosingScope().isPresentSpanningSymbol()
            && node.getEnclosingScope().getSpanningSymbol() instanceof SysMLDefinitionSymbol) {

          var definition = (SysMLDefinitionSymbol) node.getEnclosingScope().getSpanningSymbol();

          definition.addSpecializations(TypeCheck3.symTypeFromAST(s));
        }
      }
      else {
        Log.debug("Can't resolve defining symbol for " + typeName, getClass().getName());
      }
    });
  }
}
