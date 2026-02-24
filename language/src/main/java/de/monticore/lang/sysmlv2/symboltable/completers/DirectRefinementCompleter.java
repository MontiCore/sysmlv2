package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLRefinement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.types.SysMLSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Adds specializations and refinements to the list of direct refinements.
 * Example:
 * "part def Specific specializes Abstract"
 * Results in the "Specific"-PartDefSymbol to contain "Abstract" in the list of
 * "directRefinements".
 */
public class DirectRefinementCompleter implements SysMLPartsVisitor2 {

  @Override
  public void visit(ASTPartDef node) {
    var syn = new SysMLSynthesizer();

    node.getSymbol().setDirectRefinementsList(
        new ArrayList<SymTypeExpression>()
    );

    var refinementTypes = node.getSpecializationList().stream()
        .filter(r -> r instanceof ASTSysMLRefinement)
        .flatMap(r -> r.getSuperTypesList().stream()).collect(Collectors.toList());

    for (var refinementType : refinementTypes) {
      // Check existence of refinement before actually synthesizing it to avoid
      // FATAL errors thrown by the synthesizer
     if (refinementType.getDefiningSymbol().isEmpty()) {
       continue;
     }
      var result = syn.synthesizeType(refinementType);
      if (result.isPresentResult()) {
        node.getSymbol().addDirectRefinements(result.getResult());
      }
    }

    var specializationTypes = node.getSpecializationList().stream()
        .filter(r -> r instanceof ASTSysMLSpecialization)
        .flatMap(r -> r.getSuperTypesList().stream()).collect(Collectors.toList());

    for (var specializationType : specializationTypes) {
      // Check existence of refinement before actually synthesizing it to avoid
      // FATAL errors thrown by the synthesizer
      if (specializationType.getDefiningSymbol().isEmpty()) {
        continue;
      }
      var result = syn.synthesizeType(specializationType);
      if (result.isPresentResult()) {
        node.getSymbol().addDirectRefinements(result.getResult());
      }
    }
  }
}
