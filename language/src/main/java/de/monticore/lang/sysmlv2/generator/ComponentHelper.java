package de.monticore.lang.sysmlv2.generator;

import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.ArrayList;
import java.util.List;
public class ComponentHelper {
  PartUtils partUtils = new PartUtils();

  public List<String> getTargets(ASTPortUsage port) {
    return new ArrayList<>();
  }

  public String getPartType(ASTPartUsage subcomponent) {
    var printer = new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter());
    return printer.prettyprint(partUtils.partType(subcomponent));
  }

}
