package de.monticore.lang.sysmlv2.generator.helper;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlconnections._ast.ASTFlow;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.generator.utils.ComponentUtils;
import de.monticore.lang.sysmlv2.generator.utils.GeneratorUtils;
import de.monticore.lang.sysmlv2.generator.utils.PartUtils;
import de.monticore.lang.sysmlv2.types.CommonExpressionsJavaPrinter;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentHelper {
  ComponentUtils componentUtils = new ComponentUtils();

  GeneratorUtils generatorUtils = new GeneratorUtils();

  SysMLBasisTypesFullPrettyPrinter printer = new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter());

  public List<String> getTargets(ASTPortUsage port) {
    return new ArrayList<>();
  }

  public String getPartType(ASTPartUsage subcomponent) {

    return printer.prettyprint(PartUtils.partType(subcomponent));
  }

  public String getAttributeType(ASTAttributeUsage astAttributeUsage) {
    return printer.prettyprint(GeneratorUtils.attributeType(astAttributeUsage));
  }

  public boolean isObjectAttribute(ASTAttributeUsage astAttributeUsage) {
    return !generatorUtils.getScalarValueMapping().containsValue(
        printer.prettyprint(GeneratorUtils.attributeType(astAttributeUsage)));
  }

  public String mapToWrapped(ASTAttributeUsage astAttributeUsage) {
    return generatorUtils.mapToWrapper(getAttributeType(astAttributeUsage));
  }

  public boolean isPortDelayed(ASTPortUsage portUsage) {
    return componentUtils.isPortDelayed(portUsage);
  }

  public String getDefaultValue(ASTPortUsage portUsage) {

    if(portUsage.getValueAttribute().isPresentExpression()) {
      CommonExpressionsJavaPrinter prettyPrinter = new CommonExpressionsJavaPrinter(new IndentPrinter());
      return prettyPrinter.prettyprint(portUsage.getValueAttribute().getExpression());
    }
    else
      return "";
  }

  public String getDefaultValue(ASTAttributeUsage attributeUsage) {

    if(attributeUsage.isPresentExpression()) {
      CommonExpressionsJavaPrinter prettyPrinter = new CommonExpressionsJavaPrinter(new IndentPrinter());
      return prettyPrinter.prettyprint(attributeUsage.getExpression());
    }
    else
      return "";
  }

  public String getValueTypeOfPort(ASTPortUsage portUsage) {
    return componentUtils.getValueTypeOfPort(portUsage);
  }

  public List<ASTFlow> getFlowOfPart(ASTSysMLElement element) {
    if(element instanceof ASTPartUsage) {
      return ((ASTPartUsage) element).streamSysMLElements().filter(t -> t instanceof ASTFlow).map(
          t -> (ASTFlow) t).collect(
          Collectors.toList());
    }
    if(element instanceof ASTPartDef) {
      return ((ASTPartDef) element).streamSysMLElements().filter(t -> t instanceof ASTFlow).map(
          t -> (ASTFlow) t).collect(
          Collectors.toList());
    }

    return new ArrayList<>();
  }
}
