package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

public class PartUtils {
PartResolveUtils partResolveUtils = new PartResolveUtils();
GeneratorUtils generatorUtils = new GeneratorUtils();
  List<ASTCDAttribute> createPartsAsAttributes(ASTSysMLElement astPartUsage){
    List<ASTPartUsage> attributeUsageList = partResolveUtils.getSubPartsOfElement(astPartUsage);
    //create astcdattributes for the current element
    return attributeUsageList.stream().map(
        t -> createAttribute(t)).collect(
        Collectors.toList());
  }

  ASTCDAttribute createAttribute(ASTSysMLElement element) {
    if(element instanceof ASTPartUsage) {
      String attributeName = ((ASTPartUsage) element).getName();

      ASTMCQualifiedType qualifiedType = partType((ASTPartUsage) element);
      return CD4CodeMill.cDAttributeBuilder().setName(attributeName).setModifier(
          CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();

    }
    return null;
  }

  ASTMCQualifiedType partType(ASTPartUsage element) {
    var sysMLTypingList = element.getSpecializationList().stream().filter(
        t -> t instanceof ASTSysMLTyping).map(u -> ((ASTSysMLTyping) u)).collect(Collectors.toList());
    if(isAdHocClassDefinition(element)) return generatorUtils.qualifiedType(element.getName());
    if(!sysMLTypingList.isEmpty()){
    if(sysMLTypingList.get(0).getSuperTypesList().size()==1){
      String typString = sysMLTypingList.get(0).getSuperTypes(0).printType(
          new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
      List<String> partsList = Splitters.DOT.splitToList(typString);
      String typeName = partsList.get(partsList.size() - 1);
      return generatorUtils.qualifiedType(typeName);
    }}else {
      var sysmlSpec = element.getSpecializationList().stream().filter(
          t -> t instanceof ASTSysMLSpecialization).map(u -> ((ASTSysMLSpecialization) u)).collect(Collectors.toList());
      if(!sysmlSpec.isEmpty()) {
        if(sysmlSpec.get(0).getSuperTypesList().size() == 1){
          String typString = sysmlSpec.get(0).getSuperTypes(0).printType(
              new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
          List<String> partsList = Splitters.DOT.splitToList(typString);
          String typeName = partsList.get(partsList.size() - 1);
          return generatorUtils.qualifiedType(typeName);
        }
      }
    }

    Log.error(
        "The type of partUsage " + element.getName()
            + " could not be resolved.");
  return generatorUtils.qualifiedType("");
  }
  ASTMCType getNameOfSpecialication(ASTMCType spec, ASTPartUsage astPartUsage) {
    ASTPartUsage specPartUsage = astPartUsage.getEnclosingScope().resolvePartUsage(printName(spec)).get().getAstNode();
    var specializationList = specPartUsage.streamSpecializations().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var typingList = specPartUsage.streamSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var redefinitionList = specPartUsage.streamSpecializations().filter(e -> e instanceof ASTSysMLRedefinition).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    if((!specializationList.isEmpty() && !typingList.isEmpty() && redefinitionList.isEmpty()) | (typingList.size() > 1
        | (!specPartUsage.getSysMLElementList().isEmpty()))) {
      return spec;
    }
    if(!specializationList.isEmpty()) {
      return specializationList.get(0);
    }
    if(typingList.size() == 1) {
      return typingList.get(0);
    }
    return null;
  }

  boolean isAdHocClassDefinition(ASTPartUsage astPartUsage) {

    var specializationList = astPartUsage.streamSpecializations().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var typingList = astPartUsage.streamSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var redefinitionList = astPartUsage.streamSpecializations().filter(e -> e instanceof ASTSysMLRedefinition).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    return (!specializationList.isEmpty() && !typingList.isEmpty() && redefinitionList.isEmpty()) | (
        typingList.size() > 1
            | (!astPartUsage.getSysMLElementList().isEmpty()));
  }

  String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }
}
