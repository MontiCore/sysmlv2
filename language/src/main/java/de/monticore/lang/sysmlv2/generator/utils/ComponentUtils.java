package de.monticore.lang.sysmlv2.generator.utils;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.lang.sysmlbasis._ast.*;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.generator.utils.resolve.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PortResolveUtils;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.lang.sysmlv2.types.SysMLExpressionsDeriver;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentUtils {

  GeneratorUtils generatorUtils = new GeneratorUtils();

  PortResolveUtils portResolveUtils = new PortResolveUtils();

  AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();

  public List<ASTPortUsage> inputPortList;

  public List<ASTPortUsage> outputPortList;

  public void createComponentMethods(ASTSysMLElement astSysMLElement, CD4C cd4C, ASTCDClass partClass,
                                     List<ASTPartUsage> subComponents, List<ASTAttributeUsage> attributeUsageList) {
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
    List<ASTPortUsage> portUsageList = portResolveUtils.getPortsOfElement(astSysMLElement);
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
      element.setEnclosingScope(p.getEnclosingScope());
      input.add(element);
    }
    input.forEach(t -> t.setName(t.getName() + "_in"));
    inPortList.addAll(input);
    List<ASTPortUsage> output = new ArrayList<>();
    for (ASTPortUsage p : inOutPortList) {
      var element = p.deepClone();
      element.setEnclosingScope(p.getEnclosingScope());
      output.add(element);
    }
    output.forEach(t -> t.setName(t.getName() + "_out"));
    outPortList.addAll(output);

    this.inputPortList = inPortList;
    this.outputPortList = outPortList;
  }

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  public String getValueTypeOfPort(ASTPortUsage portUsage) {
    var attributeUsage = portUsage.getValueAttribute().getSpecializationList().stream().filter(
        t -> t instanceof ASTSysMLTyping).flatMap(ASTSpecialization::streamSuperTypes).findFirst();
    return printName(attributeUsage.get());
  }

  public boolean isPortDelayed(ASTPortUsage portUsage) {

    var expression = portUsage.getDelayedAttribute().getExpression();
    SysMLExpressionsDeriver sysMLExpressionsDeriver = new SysMLExpressionsDeriver();
    //var type =sysMLExpressionsDeriver.deriveType(expression);
    //TODO remove, its just a proof of concept
    if(expression instanceof ASTLiteralExpression) {
      ASTLiteral literal = ((ASTLiteralExpression) expression).getLiteral();

      if(literal instanceof ASTBooleanLiteral) {
        if(((ASTBooleanLiteral) literal).getValue())
          return true;
      }
    }
    return false;
  }

}
