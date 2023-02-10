package de.monticore.lang.sysmlv2.generator;

import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.List;

public class ComponentHelper {
  PartUtils partUtils = new PartUtils();
  ComponentUtils componentUtils = new ComponentUtils();
  GeneratorUtils generatorUtils = new GeneratorUtils();

  SysMLBasisTypesFullPrettyPrinter printer = new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter());

  public List<String> getTargets(ASTPortUsage port) {
    return new ArrayList<>();
  }

  public String getPartType(ASTPartUsage subcomponent) {

    return printer.prettyprint(partUtils.partType(subcomponent));
  }

  public String getAttributeType(ASTAttributeUsage astAttributeUsage) {
    return printer.prettyprint(generatorUtils.attributeType(astAttributeUsage));
  }

  public boolean isObjectAttribute(ASTAttributeUsage astAttributeUsage) {
    return !generatorUtils.getScalarValueMapping().containsValue(printer.prettyprint(generatorUtils.attributeType(astAttributeUsage)));
  }
  public boolean isPortDelayed(ASTPortUsage portUsage){
    return componentUtils.isPortDelayed(portUsage);
  }
  public String getValueTypeOfPort(ASTPortUsage portUsage){
    return componentUtils.getValueTypeOfPort(portUsage);
  }
}
