/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.lang.sysmlbasis._ast.ASTDefSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._ast.ASTUsageSpecialization;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTDefSpecializationCoCo;
import de.monticore.lang.sysmlbasis._cocos.SysMLBasisASTUsageSpecializationCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class OneCardinality implements SysMLBasisASTUsageSpecializationCoCo, SysMLBasisASTDefSpecializationCoCo {
  @Override
  public void check (ASTUsageSpecialization node) {
    if (node instanceof ASTSysMLTyping && ((ASTSysMLTyping) node).isPresentCardinality()) {
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
  public void check (ASTDefSpecialization node) {
    if (node instanceof ASTSysMLTyping && ((ASTSysMLTyping) node).isPresentCardinality()) {
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
