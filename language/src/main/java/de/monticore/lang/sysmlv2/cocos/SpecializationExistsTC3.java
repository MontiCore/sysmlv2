/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTSpecializationCoCo;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2.types.SysMLSynthesizer;
import de.monticore.types3.TypeCheck3;

public class SpecializationExistsTC3 implements SysMLBasisASTSpecializationCoCo {

  @Override
  public void check(ASTSpecialization node) {
    if (node.getEnclosingScope() instanceof ISysMLv2Scope) {
      // We synthesize the SymType from ASTs
      for(var typeAst : node.getSuperTypesList()) {
        // This will throw an 0xA0324 if the type does not exist.
        TypeCheck3.symTypeFromAST(typeAst);
      }
    }
  }

}
