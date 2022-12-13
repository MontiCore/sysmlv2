package de.monticore.lang.sysmlv2.cocos;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTSpecializationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class OneCardinality implements SysMLBasisASTSpecializationCoCo {
  @Override
  public void check (ASTSpecialization node) {
    if (((ASTSysMLTyping) node).isPresentCardinality()) {
      ASTCardinality Cardinality =
          Optional.of(((ASTSysMLTyping) node).getCardinality()).get();
      boolean multipleCardinalities =
          (Cardinality.getLowerBound() != Cardinality.getUpperBound())
              && (Cardinality.getLowerBound() < Cardinality.getUpperBound());

      if((Cardinality.getLowerBound() > Cardinality.getUpperBound()) && !Cardinality.isNoUpperLimit()) {
        Log.error("invalid Cardinalities", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }

      if(multipleCardinalities || Cardinality.isMany() || Cardinality.isNoUpperLimit()) {
        Log.warn("SysML-Transformer will ignore multiple Cardinalities",
            node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    }
  }
}