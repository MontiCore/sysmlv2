package de.monticore.lang.sysmlv2.generator;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.lang.sysmlbasis._ast.*;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PortUtils {

  GeneratorUtils generatorUtils = new GeneratorUtils();

  AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();

  List<ASTPortUsage> inputPortList;

  List<ASTPortUsage> outputPortList;

  void createComponentMethods(ASTSysMLElement astSysMLElement, CD4C cd4C, ASTCDClass partDefClass,
                              List<ASTPartUsage> partUsageList) {
    setPortLists(astSysMLElement);
    cd4C.addMethod(partDefClass, "sysml2cd.component.ComponentIsSyncedMethod", inputPortList);
    cd4C.addMethod(partDefClass, "sysml2cd.component.ComponentTickMethod", outputPortList, partUsageList);

    //TODO void setUp(); -> atomic oder composed

    //TODO void init(); -> automaton oder init

    //TODO void compute(); -> compute oder composed oder atomic

  }

  public List<ASTCDAttribute> createPorts(ASTSysMLElement astSysMLElement) {
    setPortLists(astSysMLElement);
    //split into distinct lists

    List<String> generatedAttributeList = new ArrayList<>();
    List<ASTCDAttribute> attributeList = this.inputPortList.stream().map(
        t -> createPort(t, generatedAttributeList, "InPort")).collect(
        Collectors.toList());

    attributeList.addAll(this.outputPortList.stream().filter(this::isPortDelay).map(
        t -> createPort(t, generatedAttributeList, "DelayPort")).collect(
        Collectors.toList()));
    attributeList.addAll(this.outputPortList.stream().filter(t -> !isPortDelay(t)).map(
        t -> createPort(t, generatedAttributeList, "OutPort")).collect(
        Collectors.toList()));
    return attributeList;
  }

  private List<ASTPortUsage> createPortUsageList(ASTSysMLElement element) {
    List<ASTSysMLElement> elementList = new ArrayList<>();
    if(element instanceof ASTPartDef)
      elementList = ((ASTPartDef) element).getSysMLElementList();
    if(element instanceof ASTPartUsage)
      elementList = ((ASTPartUsage) element).getSysMLElementList();
    if(element instanceof ASTAttributeDef)
      elementList = ((ASTAttributeDef) element).getSysMLElementList();
    List<ASTPortUsage> attributeUsageList;
    attributeUsageList = elementList.stream().filter(
        t -> t instanceof ASTPortUsage).map(t -> (ASTPortUsage) t).collect(Collectors.toList());
    return attributeUsageList;
  }

  ASTCDAttribute createPort(ASTSysMLElement element, List<String> stringList, String portType) {

    if(element instanceof ASTPortUsage) {
      String typeWithGenerics = portType + "<" + getValueTypeOfPort((ASTPortUsage) element) + ">";
      String portName = ((ASTPortUsage) element).getName();
      if(!stringList.contains(portName)) {
        stringList.add(portName);
        ASTMCQualifiedType qualifiedType = generatorUtils.qualifiedType(
            Arrays.asList("de", "monticore", "lang", "sysmlv2", "generator", "timesync",
                typeWithGenerics));
        return CD4CodeMill.cDAttributeBuilder().setName(portName).setModifier(
            CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
      }
    }
    return null;
  }

  public void setPortLists(ASTSysMLElement astSysMLElement) {
    List<ASTPortUsage> portUsageList = getPortUsage(astSysMLElement);
    //divide into the different directions
    List<ASTPortUsage> inPortList = portUsageList.stream().filter(
        t -> t.getSysMLFeatureDirection().getIntValue() == 2).collect(
        Collectors.toList());
    List<ASTPortUsage> outPortList = portUsageList.stream().filter(
        t -> t.getSysMLFeatureDirection().getIntValue() == 4).collect(
        Collectors.toList());
    List<ASTPortUsage> inOutPortList = portUsageList.stream().filter(
        t -> t.getSysMLFeatureDirection().getIntValue() == 3).collect(
        Collectors.toList());
    //transform inoutport list to a list of input ports AND a list of output ports
    List<ASTPortUsage> input = new ArrayList<>();
    for (ASTPortUsage p : inOutPortList)
      input.add(p.deepClone());
    input.forEach(t -> t.setName(t.getName() + "_in"));
    inPortList.addAll(input);
    List<ASTPortUsage> output = new ArrayList<>();
    for (ASTPortUsage p : inOutPortList)
      output.add(p.deepClone());
    output.forEach(t -> t.setName(t.getName() + "_out"));
    outPortList.addAll(output);

    this.inputPortList = inPortList;
    this.outputPortList = outPortList;
  }

  public List<ASTPortUsage> getPortUsage(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = getDirectSupertypes(node);
    List<List<ASTPortUsage>> parentAttribute = new ArrayList<>();
    List<ASTPortUsage> attributeUsages = getPortUsageOfNode(node);

    parentAttribute = parentList.stream().map(this::getPortUsage).collect(Collectors.toList());
    attributeUsages.addAll(removeDuplicateAttributes(parentAttribute));
    return attributeUsages;
  }

  List<ASTPortUsage> removeDuplicateAttributes(List<List<ASTPortUsage>> attributeLists) {
    var stringList = attributeLists.stream().flatMap(Collection::stream).map(ASTPortUsage::getName).collect(
        Collectors.toList());

    Set<String> stringSet = new HashSet<>(stringList);

    List<List<ASTPortUsage>> returnList = new ArrayList<>(attributeLists);

    return returnList.stream().flatMap(Collection::stream).filter(
        t -> stringSet.contains((t.getName()))).collect(
        Collectors.toList());
  }

  List<ASTSysMLElement> getDirectSupertypes(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = new ArrayList<>();
    //Get direct supertypes
    if(node instanceof ASTPartDef) {
      parentList = ((ASTPartDef) node).streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartDef) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      parentList = ((ASTPartUsage) node).streamSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartUsage(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
      parentList.addAll(((ASTPartUsage) node).streamSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList()));
    }
    return parentList;
  }

  List<ASTPortUsage> getPortUsageOfNode(ASTSysMLElement node) {
    List<ASTPortUsage> portUsageList = new ArrayList<>();
    if(node instanceof ASTPartDef) {
      portUsageList = ((ASTPartDef) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPortUsage).map(f -> (ASTPortUsage) f).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      portUsageList = ((ASTPartUsage) node).getSysMLElementList().stream().filter(
          t -> t instanceof ASTPortUsage).map(f -> (ASTPortUsage) f).collect(
          Collectors.toList());
    }
    return portUsageList;
  }

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  String getValueTypeOfPort(ASTPortUsage portUsage) {
    var attributeUsageList = attributeResolveUtils.getAttributesOfElement(portUsage).stream().filter(
        t -> t.getName().equals("value")).flatMap(t -> t.streamSpecializations()).flatMap(
        t -> t.streamSuperTypes()).collect(
        Collectors.toList());
    return printName(attributeUsageList.get(0));
  }

  boolean isPortDelay(ASTPortUsage portUsage) {

    var expression = attributeResolveUtils.getAttributesOfElement(portUsage).stream().filter(
        t -> t.getName().equals("delayed")).filter(t -> t.isPresentExpression()).map(t -> t.getExpression()).collect(
        Collectors.toList());
    //TODO resolve
    return false;
  }

}
