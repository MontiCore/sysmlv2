package de.monticore.lang.sysmlv2.generator.utils;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PartResolveUtils;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

public class PartUtils {

  public static List<ASTCDAttribute> createPartsAsAttributes(ASTSysMLElement astPartUsage) {
    List<ASTPartUsage> attributeUsageList = PartResolveUtils.getSubPartsOfElement(astPartUsage);
    //create astcdattributes for the current element
    return attributeUsageList.stream().map(
        PartUtils::createAttribute).collect(
        Collectors.toList());
  }

  static ASTCDAttribute createAttribute(ASTSysMLElement element) {
    if(element instanceof ASTPartUsage) {
      String attributeName = ((ASTPartUsage) element).getName();

      ASTMCQualifiedType qualifiedType = getPartUsageType((ASTPartUsage) element);
      return CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();

    }
    return null;
  }

  public static ASTMCQualifiedType getPartUsageType(ASTPartUsage element) {
    var sysMLTypingList = element.getUsageSpecializationList().stream().filter(
        t -> t instanceof ASTSysMLTyping).map(u -> ((ASTSysMLTyping) u)).collect(Collectors.toList());
    if(isAdHocClassDefinition(element))
      return GeneratorUtils.qualifiedType(element.getName());
    if(!sysMLTypingList.isEmpty()) {
      if(sysMLTypingList.get(0).getSuperTypesList().size() == 1) {
        String typString = sysMLTypingList.get(0).getSuperTypes(0).printType(
            new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
        List<String> partsList = Splitters.DOT.splitToList(typString);
        String typeName = partsList.get(partsList.size() - 1);
        return GeneratorUtils.qualifiedType(typeName);
      }
    }
    else {
      var sysmlSubset = element.getUsageSpecializationList().stream().filter(
          t -> t instanceof ASTSysMLSubsetting).map(u -> ((ASTSysMLSubsetting) u)).collect(Collectors.toList());
      if(!sysmlSubset.isEmpty()) {
        if(sysmlSubset.get(0).getSuperTypesList().size() == 1) {
          String typString = sysmlSubset.get(0).getSuperTypes(0).printType(
              new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
          List<String> partsList = Splitters.DOT.splitToList(typString);
          String typeName = partsList.get(partsList.size() - 1);
          return GeneratorUtils.qualifiedType(typeName);
        }
      }
    }

    Log.error(
        "The type of partUsage " + element.getName()
            + " could not be resolved.");
    return GeneratorUtils.qualifiedType("");
  }

  public static ASTMCType getNameOfSubsetPart(ASTMCType spec, ASTPartUsage astPartUsage) {
    ASTPartUsage specPartUsage = astPartUsage.getEnclosingScope().resolvePartUsage(printName(spec)).get().getAstNode();
    var subsetList = specPartUsage.streamUsageSpecializations().filter(
        t -> t instanceof ASTSysMLSubsetting).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var typingList = specPartUsage.streamUsageSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    if(isAdHocClassDefinition(specPartUsage)) {
      return spec;
    }
    if(!subsetList.isEmpty()) {
      return getNameOfSubsetPart(subsetList.get(0), specPartUsage);
    }
    if(typingList.size() == 1) {
      return typingList.get(0);
    }
    return null;
  }

  public static boolean isAdHocClassDefinition(ASTPartUsage astPartUsage) {

    var subsetList = astPartUsage.streamUsageSpecializations().filter(
        t -> t instanceof ASTSysMLSubsetting).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var typingList = astPartUsage.streamUsageSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var redefinitionList = astPartUsage.streamUsageSpecializations().filter(e -> e instanceof ASTSysMLRedefinition).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    return (!subsetList.isEmpty() && !typingList.isEmpty() && redefinitionList.isEmpty()) | (
        typingList.size() > 1
            | (!astPartUsage.getSysMLElementList().isEmpty()));
  }

  static String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }
}
