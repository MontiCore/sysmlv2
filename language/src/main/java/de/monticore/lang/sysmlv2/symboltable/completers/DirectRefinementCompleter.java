package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLRefinement;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.types.SysMLSynthesizer;
import de.monticore.types.check.TypeCheckResult;

import java.util.stream.Collectors;

public class DirectRefinementCompleter implements SysMLPartsVisitor2 {

  @Override
  public void visit(ASTPartDef node) {
    var refinementTypes = node.getSpecializationList().stream()
        .filter(r -> r instanceof ASTSysMLRefinement)
        .flatMap(r -> r.getSuperTypesList().stream()).collect(Collectors.toList());

    var syn = new SysMLSynthesizer();
    var refinementSymTypeExpressions = refinementTypes.stream()
        .map(syn::synthesizeType)
        .filter(TypeCheckResult::isPresentResult)
        .map(TypeCheckResult::getResult)
        .collect(Collectors.toList());

    node.getSymbol().setDirectRefinementsList(refinementSymTypeExpressions);
  }
}
