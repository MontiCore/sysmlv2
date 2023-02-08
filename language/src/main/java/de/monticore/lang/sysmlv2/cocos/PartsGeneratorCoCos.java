package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlv2.generator.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PartsGeneratorCoCos implements SysMLPartsASTPartUsageCoCo, SysMLPartsASTPartDefCoCo {
  AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();
  /**
   * Check that at least one part def is extended.
   */
  @Override public void check(ASTPartUsage node) {
    var specialications = node.streamSpecializations().filter(
        t -> t instanceof ASTSysMLSpecialization).map(ASTSpecialization::getSuperTypesList).collect(
        Collectors.toList());
    var redefinitons = node.streamSpecializations().filter(
        t -> t instanceof ASTSysMLRedefinition).map(ASTSpecialization::getSuperTypesList).collect(
        Collectors.toList());
    var typing = node.streamSpecializations().filter(
        t -> t instanceof ASTSysMLTyping).map(ASTSpecialization::getSuperTypesList).collect(
        Collectors.toList());
    var relevantElements = (int) node.getSysMLElementList().stream().filter(
        t -> t instanceof ASTActionDef | t instanceof ASTActionUsage | t instanceof ASTAttributeUsage
            | t instanceof ASTPartUsage |t instanceof ASTPortUsage).count(); //partUsage with at least one of the types is seen as a adhoc class definition
    if(specialications.isEmpty() && relevantElements == 0 && redefinitons.isEmpty() && typing.isEmpty()) {
      Log.error("The Part Usage " + node.getName()
          + " needs a type (at least one part def), redefine a part usage or specialize another part usage");
    }
    attributeResolveUtils.getAttributesOfElement(node);
    var redeinitionSpec = node.streamSpecializations().filter(
        t -> t instanceof ASTSysMLRedefinition).collect(Collectors.toList());
    if(!redeinitionSpec.isEmpty())
      checkRefinition(redeinitionSpec, node);
  }

  @Override public void check(ASTPartDef node) {
    long numberIllegalSpecs = node.streamSpecializations().filter(t -> t instanceof ASTSysMLTyping | t instanceof ASTSysMLRedefinition).count();
    if(numberIllegalSpecs!= 0)       Log.error("The Part Def " + node.getName()
        + " uses redefinitions or typings, this is not allowed.");

    attributeResolveUtils.getAttributesOfElement(node);
  }

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }




  //

  void checkRefinition(List<ASTSpecialization> redefinitionList, ASTPartUsage node) {

    if(redefinitionList.get(0).getSuperTypesList().size() != 1) {
      Log.error("Part Usage " + node.getName() + " has "
          + (redefinitionList.get(0).getSuperTypesList().size()
          + " redefinitions, but may only redefine 1."));
    }
    String parentPartName = printName((redefinitionList.get(0).getSuperTypes(0)));

    if(!node.getName().equals(parentPartName)) {
      Log.error("Part Usage " + node.getName()
          + " has to redefine a Part Usage with the same name, but redefines "
          + parentPartName + ".");
    }
    checkPartDefined(node, printName(redefinitionList.get(0).getSuperTypesList().get(0)));

  }

  void checkPartDefined(ASTPartUsage partUsage, String redefinedPartName) {
    int partFoundInParents = 0; // we count how often the redefinedPartName is defined within the transitiveSupertypes of the parent node
    var parentNode = partUsage.getEnclosingScope().getAstNode();

    if(parentNode instanceof ASTPartDef) { //TODO ASTPartUsage

      for (ASTPartDef transSuperType : ((ASTPartDef) parentNode).getTransitiveDefSupertypes()) {
        var partUsageSymbol = transSuperType.getSpannedScope().resolvePartUsage(redefinedPartName);
        partFoundInParents = checkPartUsageSymbol(partFoundInParents, partUsageSymbol);
      }
    }
    if(parentNode instanceof ASTPartUsage) {
      for (ASTPartDef transSuperType : ((ASTPartUsage) parentNode).getTransitiveDefSupertypes()) {
        var partUsageSymbol = transSuperType.getSpannedScope().resolvePartUsage(redefinedPartName);
        partFoundInParents = checkPartUsageSymbol(partFoundInParents, partUsageSymbol);
      }
      for (ASTPartUsage transSuperType : ((ASTPartUsage) parentNode).getTransitiveUsageSupertypes()) {
        var partUsageSymbol = transSuperType.getSpannedScope().resolvePartUsage(redefinedPartName);
        partFoundInParents = checkPartUsageSymbol(partFoundInParents, partUsageSymbol);
      }
      if(partFoundInParents != 1)
        Log.error("Part usage " + partUsage.getName() + " was found " + partFoundInParents
            + " in the transitive super types of its parent, but only 1 is allowed.");
    }
  }

  int checkPartUsageSymbol(int partFoundInParents, Optional<PartUsageSymbol> partUsageSymbol) {
    if(partUsageSymbol.isPresent()) {
      ASTPartUsage refinedAttr = partUsageSymbol.get().getAstNode();
      if(refinedAttr.getSpecializationList().stream().noneMatch(
          t -> t instanceof ASTSysMLRedefinition)) //we only count attributeUsages where no redefinition is used
      {
        partFoundInParents++;
      }
      //TODO check TypeCompatibility(partUsage, refinedAttr);
    }
    return partFoundInParents;
  }



}
