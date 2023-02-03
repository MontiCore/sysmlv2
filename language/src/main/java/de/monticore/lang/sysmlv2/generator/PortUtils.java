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
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PortUtils {

  GeneratorUtils generatorUtils = new GeneratorUtils();

  void createComponentMethods(ASTPartDef astPartDef, CD4C cd4C, ASTCDClass partDefClass) {
    var portList = astPartDef.getSysMLElementList().stream().filter(t -> t instanceof ASTPortUsage).map(
        t -> (ASTPortUsage) t).collect(
        Collectors.toList()); //TODO unterscheidung nach port direction
    var inPortList = portList.stream().filter(t -> t.getSysMLFeatureDirection().getIntValue() == 2).collect(
        Collectors.toList());
    var outPortList = portList.stream().filter(t -> t.getSysMLFeatureDirection().getIntValue() == 4).collect(
        Collectors.toList());
    var inOutPortList = portList.stream().filter(t -> t.getSysMLFeatureDirection().getIntValue() == 3).collect(
        Collectors.toList());
    var input = new ArrayList<>(inOutPortList);
    //manually add inout ports to the inPortList ! This causes changes to the port usage in the ast!
    input.forEach(t -> t.setName(t.getName() + "_in"));
    inPortList.addAll(input);
    cd4C.addMethod(partDefClass, "sysml2cd.component.ComponentIsSyncedMethod", inPortList);
    var output = new ArrayList<>(inOutPortList);
    output.forEach(t -> t.setName(t.getName().substring(0, t.getName().length() - ("_in").length()) + "_out"));
    outPortList.addAll(output);
    var partList = astPartDef.getSysMLElementList().stream().filter(t -> t instanceof ASTPartUsage).map(
        t -> (ASTPartUsage) t).collect(
        Collectors.toList());

    cd4C.addMethod(partDefClass, "sysml2cd.component.ComponentTickMethod", outPortList, partList);
    output.forEach(t -> t.setName(
        t.getName().substring(0, t.getName().length() - ("_out").length()))); //Reset name to resolve sideeffects
    //TODO void setUp(); -> atomic oder composed

    //TODO void init(); -> automaton oder init

    //TODO void compute(); -> compute oder composed oder atomic

  }

  List<ASTCDAttribute> createPorts(ASTSysMLElement astSysMLElement) {

    List<ASTPortUsage> portUsageList = createPortUsageList(astSysMLElement);
    List<ASTPortUsage> supertypePortUsageList = new ArrayList<>();
    //create astcdattributes for transitive attributes
    if(astSysMLElement instanceof ASTPartDef) {
      supertypePortUsageList = ((ASTPartDef) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createPortUsageList(t).stream()).collect(Collectors.toList());
    }
    if(astSysMLElement instanceof ASTPartUsage) {
      supertypePortUsageList = ((ASTPartUsage) astSysMLElement).streamTransitiveDefSupertypes().flatMap(
          t -> createPortUsageList(t).stream()).collect(Collectors.toList());
    }
    portUsageList.addAll(supertypePortUsageList);
    //split into distinct lists
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
    List<ASTPortUsage> input = new ArrayList<>(inOutPortList);
    //manually add inout ports to the inPortList ! This causes changes to the port usage in the ast!
    input.forEach(t -> t.setName(t.getName() + "_in"));
    inPortList.addAll(input);

    List<String> generatedAttributeList = new ArrayList<>();
    List<ASTCDAttribute> attributeList = inPortList.stream().map(
        t -> createPort(t, generatedAttributeList, "InPort")).collect(
        Collectors.toList());
    List<ASTPortUsage> output = new ArrayList<>(inOutPortList);
    //remove the suffix _in and add suffix _out, manually add inout ports to the outPortList ! This causes changes to the port usage in the ast!
    output.forEach(t -> t.setName(t.getName().substring(0, t.getName().length() - ("_in").length()) + "_out"));
    outPortList.addAll(output);
    attributeList.addAll(outPortList.stream().map(
        t -> createPort(t, generatedAttributeList, "OutPort")).collect(
        Collectors.toList()));
    output.forEach(t -> t.setName(
        t.getName().substring(0, t.getName().length() - ("_out").length()))); //Reset name to resolve sideeffects
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

  ASTCDAttribute createPort(ASTSysMLElement element, List<String> stringList, String direction) {
    String type = direction + "<Integer>";//TODO korrekten typ erkennen
    if(element instanceof ASTPortUsage) {
      String portName = ((ASTPortUsage) element).getName();
      if(!stringList.contains(portName)) {
        stringList.add(portName);
        ASTMCQualifiedType qualifiedType = generatorUtils.qualifiedType(
            Arrays.asList("de", "monticore", "lang", "sysmlv2", "generator", "timesync",
                type));
        return CD4CodeMill.cDAttributeBuilder().setName(portName).setModifier(
            CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(qualifiedType).build();
      }
    }
    return null;
  }

  /////////////////////

}
