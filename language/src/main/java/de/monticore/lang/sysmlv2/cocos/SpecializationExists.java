/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTSpecializationCoCo;
import de.monticore.lang.sysmlstates._ast.ASTSysMLTransition;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTSysMLTransitionCoCo;
import de.monticore.lang.sysmlstates._symboltable.ISysMLStatesScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

public class SpecializationExists implements SysMLBasisASTSpecializationCoCo {

  @Override
  public void check(ASTSpecialization node) {
    if (node.getEnclosingScope() instanceof ISysMLv2Scope) {
      var scope = (ISysMLv2Scope)node.getEnclosingScope();
      for(var type : node.getSuperTypesList()) {
        var typeAsString = type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
        if(scope.resolveType(typeAsString).isEmpty()) {
          Log.error("Could not resolve this type", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      }
    }
  }

}
