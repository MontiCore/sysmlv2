package de.monticore.lang.sysmlv2._lsp.features.code_action.utils;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;

import java.util.List;
import java.util.stream.Collectors;

public abstract class RefinementAnalysis {

  /**
   * Is used to find partDefsSymbols with the same port interface as the given partDefSymbol.
   * @param partDefSymbol The partDefSymbol to find similar partDefs for.
   * @param scope The scope to search in for partDefs.
   */
  public static List<PartDefSymbol> getRefinementOrRoughCandidates(PartDefSymbol partDefSymbol,
                                                                   ISysMLv2Scope scope,
                                                                   boolean matchConnectionUsages){
    var parts = PartDefSymbol.getAllPartDefs(scope)
        .filter(p -> p.matchesInterfaceOf(partDefSymbol))
        .filter(p -> p.getAstNode().getRefinements().stream()
            .noneMatch(r -> r.getAstNode().equals(partDefSymbol.getAstNode())))
        .filter(p -> !p.getName().equals(partDefSymbol.getName()));

    if (matchConnectionUsages){
      var connectionUsages = partDefSymbol.getAstNode().getSysMLElementList().stream()
          .filter(e -> e instanceof ASTConnectionUsage)
          .collect(Collectors.toList());

      parts = parts.filter(p -> {
        var refConnectionUsages = p.getAstNode().getSysMLElementList().stream()
            .filter(e -> e instanceof ASTConnectionUsage)
            .collect(Collectors.toList());
        return refConnectionUsages.size() == connectionUsages.size();
      });
    }

    return parts.collect(Collectors.toList());
  }

  public static List<PartDefSymbol> getBasicRefinementCandidates(PartDefSymbol partDefSymbol, ISysMLv2Scope scope){
    var result = getRefinementOrRoughCandidates(partDefSymbol, scope, false);
    result = result.stream()
        .filter(p -> p.getAstNode().getSysMLElements(ASTConnectionUsage.class).size() == 0)
        .collect(Collectors.toList());

    return result;
  }

  /**
   * Calculates a score for a possible refinement based on similarity between the refinement and the rough component.
   * @return A higher score means higher similarity.
   */
  public static int calculateRefinementScore(PartDefSymbol refinement, PartDefSymbol rough) {
    var score = 0;

    // Prefer refinements of higher level components
    if (rough.getRequirementType() == ASTSysMLReqType.LLR){
      if (refinement.getRequirementType() == ASTSysMLReqType.MIXED) {
        score += 5;
      } else if (refinement.getRequirementType() == ASTSysMLReqType.HLR){
        score += 10;
      }
    } else if (rough.getRequirementType() == ASTSysMLReqType.MIXED){
      if (refinement.getRequirementType() == ASTSysMLReqType.HLR){
        score += 10;
      }
    }

    // Prefer refinements with the same number of connections
    if (rough.getAstNode() != null){
      var connectionUsages = refinement.getAstNode().getSysMLElementList().stream()
          .filter(e -> e instanceof ASTConnectionUsage)
          .collect(Collectors.toList());

      var refConnectionUsages = rough.getAstNode().getSysMLElementList().stream()
          .filter(e -> e instanceof ASTConnectionUsage)
          .collect(Collectors.toList());

      if (connectionUsages.size() == refConnectionUsages.size()){
        score += 10;
      }
    }
    return score;
  }
}
