package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLRefinement;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.types.SysMLSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DirectRefinementCompleter implements SysMLPartsVisitor2 {

  @Override
  public void visit(ASTPartDef node) {
    var refinementTypes = node.getSpecializationList().stream()
        .filter(r -> r instanceof ASTSysMLRefinement)
        .flatMap(r -> r.getSuperTypesList().stream()).collect(Collectors.toList());

    var syn = new SysMLSynthesizer();

    var validRefinementExpressions = new ArrayList<SymTypeExpression>();
    for (var refinementType : refinementTypes) {
      // Check existince of refinement before actually synthesizing it to avoid FATAL errors thrown by the synthesizer
     if (refinementType.getDefiningSymbol().isEmpty()) {
       continue;
     }
      var result = syn.synthesizeType(refinementType);
      if (result.isPresentResult()) {
        validRefinementExpressions.add(result.getResult());
      }
    }
    node.getSymbol().setDirectRefinementsList(validRefinementExpressions);
  }
}
