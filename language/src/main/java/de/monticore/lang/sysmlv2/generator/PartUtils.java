package de.monticore.lang.sysmlv2.generator;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.*;
import java.util.stream.Collectors;

public class PartUtils {

  private List<ASTPartUsage> createPartUsageList(ASTSysMLElement element) {
    List<ASTSysMLElement> elementList = new ArrayList<>();
    if(element instanceof ASTPartDef)
      elementList = ((ASTPartDef) element).getSysMLElementList();
    if(element instanceof ASTPartUsage)
      elementList = ((ASTPartUsage) element).getSysMLElementList();
    if(element instanceof ASTAttributeDef)
      elementList = ((ASTAttributeDef) element).getSysMLElementList();
    List<ASTPartUsage> attributeUsageList;
    attributeUsageList = elementList.stream().filter(
        t -> t instanceof ASTPartUsage).map(t -> (ASTPartUsage) t).collect(Collectors.toList());
    return attributeUsageList;
  }

  public List<ASTPartUsage> setPortLists(ASTSysMLElement astSysMLElement) {
    List<ASTPartUsage> portUsageList = createPartUsageList(astSysMLElement);  //create Port usage list
    List<ASTPartUsage> supertypePortUsageList = new ArrayList<>();
    //create astcdattributes for transitive attributes
    if(astSysMLElement instanceof ASTPartDef) {
      supertypePortUsageList = ((ASTPartDef) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createPartUsageList(t).stream()).collect(Collectors.toList());
    }
    if(astSysMLElement instanceof ASTPartUsage) {
      supertypePortUsageList = ((ASTPartUsage) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createPartUsageList(t).stream()).collect(Collectors.toList());
    }
    portUsageList.addAll(supertypePortUsageList);
    //divide into the different directions
    return portUsageList;
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

  boolean isAdHocClassDefinition(ASTPartUsage astPartUsage){

    var specializationList = astPartUsage.streamSpecializations().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var typingList = astPartUsage.streamSpecializations().filter(c -> c instanceof ASTSysMLTyping).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());

    var redefinitionList = astPartUsage.streamSpecializations().filter(e -> e instanceof ASTSysMLRedefinition).flatMap(
        f -> f.getSuperTypesList().stream()).collect(Collectors.toList());
    return (!specializationList.isEmpty() && !typingList.isEmpty() && redefinitionList.isEmpty()) | (typingList.size() > 1
        | (!astPartUsage.getSysMLElementList().isEmpty()));
  }

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }
}
