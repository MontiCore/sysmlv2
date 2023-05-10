package de.monticore.lang.sysmlv2.generator.helper;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlconnections._ast.ASTFlow;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._symboltable.SysMLPartsScope;
import de.monticore.lang.sysmlv2.generator.utils.PackageUtils;
import de.monticore.lang.sysmlv2.generator.utils.PartUtils;
import de.monticore.lang.sysmlv2.generator.utils.resolve.PortResolveUtils;
import de.monticore.lang.sysmlv2.types.CommonExpressionsJavaPrinter;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentHelper {
  PartUtils partUtils = new PartUtils();

  static int out_direction = 4;

  static int in_direction = 2;

  PackageUtils packageUtils = new PackageUtils();

  SysMLBasisTypesFullPrettyPrinter printer = new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter());

  public String getPartType(ASTPartUsage subcomponent) {

    return printer.prettyprint(PartUtils.getPartUsageType(subcomponent));
  }

  public String getAttributeType(ASTAttributeUsage astAttributeUsage) {
    return printer.prettyprint(PackageUtils.attributeType(astAttributeUsage));
  }

  public boolean isObjectAttribute(ASTAttributeUsage astAttributeUsage) {
    return !PackageUtils.getScalarValueMapping().containsValue(
        printer.prettyprint(PackageUtils.attributeType(astAttributeUsage)));
  }

  public String mapToWrapped(ASTAttributeUsage astAttributeUsage) {
    return packageUtils.mapToWrapper(getAttributeType(astAttributeUsage));
  }

  public boolean isPortDelayed(ASTPortUsage portUsage) {
    return partUtils.isPortDelayed(portUsage);
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
    return partUtils.getValueTypeOfPort(portUsage);
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

  public String cdPackageAsQualifiedName(ASTSysMLElement element, String baseName) {
    return PackageUtils.cdPackageAsQualifiedName(element, baseName);
  }

  public boolean isOutPort(String nameOfPort, ASTSysMLElement astSysMLElement) {
    var stringParts = Arrays.asList(nameOfPort.split("\\."));
    String baseName = stringParts.get(stringParts.size() - 1);
    ISysMLPartsScope scope = new SysMLPartsScope();
    if(astSysMLElement instanceof ASTPartDef)
      scope = ((ASTPartDef) astSysMLElement).getSpannedScope();
    if(astSysMLElement instanceof ASTPartUsage)
      scope = ((ASTPartUsage) astSysMLElement).getSpannedScope();
    var optionalPortUsageSymbol = PortResolveUtils.resolvePort(nameOfPort, baseName,
        scope);
    if(optionalPortUsageSymbol.isPresent()) {
      ASTPortUsage portUsage = optionalPortUsageSymbol.get().getAstNode();
      return portUsage.getValueAttribute().getSysMLFeatureDirection().getIntValue() == out_direction;
    }
    else
      return false;
  }

  public boolean isInPort(String nameOfPort, ASTSysMLElement astSysMLElement) {
    var stringParts = Arrays.asList(nameOfPort.split("\\."));
    String baseName = stringParts.get(stringParts.size() - 1);
    ISysMLPartsScope scope = new SysMLPartsScope();
    if(astSysMLElement instanceof ASTPartDef)
      scope = ((ASTPartDef) astSysMLElement).getSpannedScope();
    if(astSysMLElement instanceof ASTPartUsage)
      scope = ((ASTPartUsage) astSysMLElement).getSpannedScope();
    var optionalPortUsageSymbol = PortResolveUtils.resolvePort(nameOfPort, baseName,
        scope);
    if(optionalPortUsageSymbol.isPresent()) {
      ASTPortUsage portUsage = optionalPortUsageSymbol.get().getAstNode();
      return portUsage.getValueAttribute().getSysMLFeatureDirection().getIntValue() == in_direction;
    }
    else
      return false;
  }
}
