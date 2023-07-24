package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.se_rwth.commons.SourcePositionBuilder;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RefinementChainCheck implements SysMLPartsASTPartDefCoCo {

  @Override
  public void check(ASTPartDef node) {
    if (!node.isPresentSymbol()){
      return;
    }

    var partDefNameEnd = new SourcePositionBuilder().setFileName(node.get_SourcePositionStart().getFileName().get())
        .setLine(node.get_SourcePositionStart().getLine())
        .setColumn(node.get_SourcePositionStart().getColumn() + node.getName().length() + "part def".length() + 2)
        .build();

    if (node.getRefinements().isEmpty() && node.getSymbol().getRequirementType() == ASTSysMLReqType.LLR){
      Log.warn("0x90010 Low level requirement without refinement.", node.get_SourcePositionStart(), partDefNameEnd);
    } else if (node.getSymbol().getRequirementType() == ASTSysMLReqType.LLR){
      // If there are ConnectionUsages in a Lrr,
      // there should be a refinement with similar ConnectionUsages that uses HLR parts only.
      if (node.getSysMLElementList().stream().anyMatch(e -> e instanceof ASTConnectionUsage)){
        if (filterSimilarCompositions(node.getSymbol(), node.getSymbol().getTransitiveRefinements()).size() == 0){
          Log.warn("0x90011 Llr with ConnectionUsages should refine at least one HLR with similiar ConnectionUsages.",
              node.get_SourcePositionStart(),
              partDefNameEnd);
        }
      }
    }

    if (node.getSymbol().getRequirementType() == ASTSysMLReqType.HLR ||
        node.getSymbol().getRequirementType() == ASTSysMLReqType.MIXED){
      var refiners = node.getSymbol().getTransitiveRefiners();
      if (refiners.isEmpty()){
        Log.warn("0x90020 High level or mixed requirements should be refined.",
            node.get_SourcePositionStart(), partDefNameEnd);
      } else {
        // If there are ConnectionUsages in a Hlr,
        // there should be a refiner with similar ConnectionUsages that uses Llr parts only.
        if (node.getSysMLElementList().stream().anyMatch(e -> e instanceof ASTConnectionUsage)){
          if (filterSimilarCompositions(node.getSymbol(), node.getSymbol().getTransitiveRefiners()).size() == 0){
            Log.warn("0x90021 A composition should be (further) refined by a similarly composed composition.",
                node.get_SourcePositionStart(), partDefNameEnd);
          }
        }
      }
      if (node.getSysMLElementList().stream().anyMatch(e -> e instanceof ASTConnectionUsage)) {
        if (getAllWithoutConnectionUsages(node.getRefinements()).size() == 0){
          // High Level Architekturen / Dekompositionen sollte auf ein "Basis Requirement" zurückzuführen sein,
          // welches das Verhalten nur anhand von beispielsweise Constraints beschreibt.
          Log.warn("0x90022 High level decompositions should refine at least one basic requirement",
              node.get_SourcePositionStart(), partDefNameEnd);
        }
      }
    }
  }

  /**
   * Attempts to filter the {@code symbols} for ones that are similarly composed as {@code reference}
   */
  private List<PartDefSymbol> filterSimilarCompositions(PartDefSymbol reference, List<PartDefSymbol> symbols) {
    var result = new ArrayList<PartDefSymbol>();
    var connectionUsages = reference.getAstNode().getSysMLElementList().stream()
        .filter(e -> e instanceof ASTConnectionUsage)
        .map(e -> (ASTConnectionUsage) e)
        .collect(Collectors.toList());

    for (var refinement : symbols){
      var refConnectionUsages = refinement.getAstNode().getSysMLElementList().stream()
          .filter(e -> e instanceof ASTConnectionUsage)
          .collect(Collectors.toList());

      if (connectionUsages.size() == refConnectionUsages.size()){
        result.add(refinement);
      }
    }
    return result;
  }

  private List<PartDefSymbol> getAllWithoutConnectionUsages(List<PartDefSymbol> symbols) {
    return symbols.stream()
        .filter(e -> e.getAstNode().getSysMLElements(ASTConnectionUsage.class).isEmpty())
        .collect(Collectors.toList());
  }
}
