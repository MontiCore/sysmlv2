/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTDefSpecialization;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTDefSpecializationCoCo;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

public class DefSpecializationExists implements SysMLBasisASTDefSpecializationCoCo{

  @Override
  public void check(ASTDefSpecialization node) {
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
