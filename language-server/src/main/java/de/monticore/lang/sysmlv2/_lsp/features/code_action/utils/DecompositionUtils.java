package de.monticore.lang.sysmlv2._lsp.features.code_action.utils;

import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._ast.ASTSysMLReqType;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO Pascal Devant sind die Util-Klassen
//  a) nach Language verschiebbar
//  b) durch MontiCore-Konstrukte (CoCos) ersetzbar
//  c) Reihenweise private-statics vermeidbar?
public abstract class DecompositionUtils {

  private static int complexityDifference(ASTPartDef comp1, ASTPartDef comp2){
    int score = 0;

    var comp1_ports = comp1.getSysMLElements(ASTPortUsage.class);
    var comp2_ports = comp2.getSysMLElements(ASTPortUsage.class);
    score += Math.abs(comp1_ports.size() - comp2_ports.size()) * 1.5;

    var comp1_attributes = comp1.getSysMLElements(ASTAttributeUsage.class);
    var comp2_attributes = comp2.getSysMLElements(ASTAttributeUsage.class);
    score += Math.abs(comp1_attributes.size() - comp2_attributes.size());

    var comp1_parts = comp1.getSysMLElements(ASTPartUsage.class);
    var comp2_parts = comp2.getSysMLElements(ASTPartUsage.class);
    score += Math.abs(comp1_parts.size() - comp2_parts.size());

    var comp1_connections = comp1.getSysMLElements(ASTConnectionUsage.class);
    var comp2_connections = comp2.getSysMLElements(ASTConnectionUsage.class);
    score += Math.abs(comp1_connections.size() - comp2_connections.size()) * 0.5;

    return score;
  }

  private static int complexityDifference(ASTPartDef reference, ASTPartDef comp1, ASTPartDef comp2){
    int score = 0;

    score += complexityDifference(reference, comp1);
    score += complexityDifference(reference, comp2);
    score += complexityDifference(comp1, comp2);

    return score;
  }

  public static Stream<Pair<ASTPartDef, ASTPartDef>> getDecompositionCandidates(ASTPartDef reference, ASTSysMLReqType type){
    var decompositions = new HashMap<Pair<ASTPartDef, ASTPartDef>, Map<Pair<ASTPartDef, ASTPartDef>, Map<ASTPortUsage, ASTPortUsage>>> ();
    var parts = PartDefSymbol.getAllPartDefs()
        .filter(p -> p.getRequirementType() == type)
        .map(PartDefSymbol::getAstNode)
        .filter(p -> !p.equals(reference))
        .collect(Collectors.toList());

    for (var comp1 : parts) {
      for (var comp2 : parts){
        // Try to calculate mapping.
        // If that fails with an IllegalStateException, (comp1 o comp2)  is not a valid decomposition of reference.
        try {
          var mapping = getDecompositionMapping(reference, comp1.deepClone(), comp2.deepClone());
          decompositions.put(new Pair<>(comp1.deepClone(), comp2.deepClone()), mapping);
        } catch (IllegalStateException ignored) { }
      }
    }

    return new ArrayList<>(decompositions.keySet()).stream()
        .sorted(Comparator.comparingInt(p -> complexityDifference(reference, p.a, p.b)));
  }


  public static Map<Pair<ASTPartDef, ASTPartDef>, Map<ASTPortUsage, ASTPortUsage>> getDecompositionMapping(ASTPartDef reference, ASTPartDef comp1, ASTPartDef comp2){
    var mappings = new HashMap<Pair<ASTPartDef, ASTPartDef>, Map<ASTPortUsage, ASTPortUsage>>();
    // We don't really care how to exactly connect the components for now.
    // But this will be the place to do it in future.
    return mappings;
  }
}
