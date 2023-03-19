/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTSpecializationCoCo;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2.types.SysMLSynthesizer;
import de.se_rwth.commons.logging.Log;

public class SpecializationExists implements SysMLBasisASTSpecializationCoCo {

  @Override
  public void check(ASTSpecialization node) {
    if (node.getEnclosingScope() instanceof ISysMLv2Scope) {
      var scope = (ISysMLv2Scope)node.getEnclosingScope();
      // Synthesizers are used to "build" the SymType from ASTs
      var synthesizer = new SysMLSynthesizer();
      for(var typeAst : node.getSuperTypesList()) {
        var type = synthesizer.synthesizeType(typeAst);
        // Unfortunately, MontiCore's "ObscureType" does not yet provide any information about
        // what went wrong.
        if(!type.isPresentResult() || type.getResult().isObscureType()) {
          Log.error("Could not check the existence of this type reference",
              typeAst.get_SourcePositionStart(),
              typeAst.get_SourcePositionEnd());
        }
      }
    }
  }

}
