package de.monticore.lang.sysmlv2.generator.utils;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PartResolveUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PortResolveUtils;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

public class PartUtils {

  PackageUtils packageUtils = new PackageUtils();

  public List<ASTPortUsage> inputPortList;

  public List<ASTPortUsage> outputPortList;

  public void createComponentMethods(ASTSysMLElement astSysMLElement, CD4C cd4C, ASTCDClass partClass) {
    List<ASTPartUsage> subComponents = PartResolveUtils.getPartUsageOfNode(astSysMLElement);
    List<ASTAttributeUsage> attributeUsageList = AttributeResolveUtils.getAttributesOfElement(astSysMLElement);
    setPortLists(astSysMLElement);
    cd4C.addMethod(partClass, "sysml2cd.component.ComponentIsSyncedMethod", inputPortList);
    cd4C.addMethod(partClass, "sysml2cd.component.ComponentTickMethod", outputPortList, subComponents);

    cd4C.addMethod(partClass, "sysml2cd.component.ComponentSetUpMethod", subComponents, outputPortList, inputPortList,
        attributeUsageList, astSysMLElement);
    cd4C.addMethod(partClass, "sysml2cd.component.ComponentGetAllSubcomponentsMethod", subComponents);
    cd4C.addMethod(partClass, "sysml2cd.component.ComponentComputeMethod", astSysMLElement, inputPortList,
        outputPortList);
    //TODO void init(); -> automaton oder init

  }

  public ASTMCObjectType createComponent() {

    return CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
        CD4CodeMill.mCQualifiedNameBuilder().
            addParts("de.monticore.lang.sysmlv2.generator.timesync.IComponent").build()).build();
  }

  public List<ASTCDAttribute> createPorts(ASTSysMLElement astSysMLElement) {
    setPortLists(astSysMLElement);
    //split into distinct lists

    List<String> generatedAttributeList = new ArrayList<>();
    List<ASTCDAttribute> attributeList = this.inputPortList.stream().map(
        t -> createPort(t, generatedAttributeList, "InPort")).collect(
        Collectors.toList());

    attributeList.addAll(this.outputPortList.stream().filter(this::isPortDelayed).map(
        t -> createPort(t, generatedAttributeList, "DelayPort")).collect(
        Collectors.toList()));
    attributeList.addAll(this.outputPortList.stream().filter(t -> !isPortDelayed(t)).map(
        t -> createPort(t, generatedAttributeList, "OutPort")).collect(
        Collectors.toList()));
    return attributeList;
  }

  ASTCDAttribute createPort(ASTSysMLElement element, List<String> stringList, String portType) {

    if(element instanceof ASTPortUsage) {
      String typeWithGenerics = portType + "<" + getValueTypeOfPort((ASTPortUsage) element) + ">";
      String portName = ((ASTPortUsage) element).getName();
      if(!stringList.contains(portName)) {
        stringList.add(portName);
        ASTMCQualifiedType qualifiedType = packageUtils.qualifiedType(
            Arrays.asList("de", "monticore", "lang", "sysmlv2", "generator", "timesync",
                typeWithGenerics));
        return CD4CodeMill.cDAttributeBuilder().setName(portName).setModifier(
            CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
      }
    }
    return null;
  }

  public void setPortLists(ASTSysMLElement astSysMLElement) {
    List<ASTPortUsage> portUsageList = PortResolveUtils.getPortsOfElement(astSysMLElement);
    //divide into the different directions
    List<ASTPortUsage> inPortList = portUsageList.stream().filter(
        t -> t.getValueAttribute().getSysMLFeatureDirection().getIntValue() == 2).collect(
        Collectors.toList());
    List<ASTPortUsage> outPortList = portUsageList.stream().filter(
        t -> t.getValueAttribute().getSysMLFeatureDirection().getIntValue() == 4).collect(
        Collectors.toList());
    List<ASTPortUsage> inOutPortList = portUsageList.stream().filter(
        t -> t.getValueAttribute().getSysMLFeatureDirection().getIntValue() == 3).collect(
        Collectors.toList());
    //transform inoutport list to a list of input ports AND a list of output ports
    List<ASTPortUsage> input = new ArrayList<>();
    for (ASTPortUsage p : inOutPortList) {
      var element = p.deepClone();
      element.setValueAttribute(p.getValueAttribute().deepClone());
      element.setDelayedAttribute(p.getDelayedAttribute().deepClone());
      element.setEnclosingScope(p.getEnclosingScope());
      input.add(element);
    }
    input.forEach(t -> t.setName(t.getName() + "_in"));
    inPortList.addAll(input);
    List<ASTPortUsage> output = new ArrayList<>();
    for (ASTPortUsage p : inOutPortList) {
      var element = p.deepClone();
      element.setValueAttribute(p.getValueAttribute().deepClone());
      element.setDelayedAttribute(p.getDelayedAttribute().deepClone());
      element.setEnclosingScope(p.getEnclosingScope());
      output.add(element);
    }
    output.forEach(t -> t.setName(t.getName() + "_out"));
    outPortList.addAll(output);

    this.inputPortList = inPortList;
    this.outputPortList = outPortList;
  }

  public String getValueTypeOfPort(ASTPortUsage portUsage) {
    return ScalarValues.mapToWrapper(printName(AttributeUtils.attributeType(portUsage.getValueAttribute())));
  }

  public boolean isPortDelayed(ASTPortUsage portUsage) {
    if(portUsage.isPresentDelayed()) {
      var expression = portUsage.getDelayedAttribute().getExpression();
      if(expression instanceof ASTLiteralExpression) {
        ASTLiteral literal = ((ASTLiteralExpression) expression).getLiteral();

        if(literal instanceof ASTBooleanLiteral) {
          return ((ASTBooleanLiteral) literal).getValue();
        }
      }
    }
    return false;
  }
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
      return PackageUtils.qualifiedType(element.getName());
    if(!sysMLTypingList.isEmpty()) {
      if(sysMLTypingList.get(0).getSuperTypesList().size() == 1) {
        String typString = sysMLTypingList.get(0).getSuperTypes(0).printType(
            new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
        List<String> partsList = Splitters.DOT.splitToList(typString);
        String typeName = partsList.get(partsList.size() - 1);
        return PackageUtils.qualifiedType(typeName);
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
          return PackageUtils.qualifiedType(typeName);
        }
      }
    }

    Log.error(
        "The type of partUsage " + element.getName()
            + " could not be resolved.");
    return PackageUtils.qualifiedType("");
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
