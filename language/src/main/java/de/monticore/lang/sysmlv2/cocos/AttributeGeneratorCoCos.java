package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class AttributeGeneratorCoCos implements SysMLPartsASTAttributeUsageCoCo {

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that only one redefinition is used and that it is resolvable.
   */
  @Override
  public void check(ASTAttributeUsage node) {
    checkTyping(node);
    var redefinitionList = node.getSpecializationList().stream().filter(t -> t instanceof ASTSysMLRedefinition).collect(
        Collectors.toList());
    for (ASTSpecialization redefinition : redefinitionList) {
      if(((ASTSysMLRedefinition) redefinition).getSuperTypesList().size() != 1) {
        Log.error("Attribute Usage " + node.getName() + " has "
            + ((ASTSysMLRedefinition) redefinition).getSuperTypesList().size()
            + " redefinitions, but may only redefine 1.");
      }
      var redefinitionType = ((ASTSysMLRedefinition) redefinition).getSuperTypes(0); //only one redefinition is allowed

      var definition = node.getEnclosingScope().resolveAttributeDef(printName(redefinitionType));
      if(definition.isEmpty()) {

        var usage = node.getEnclosingScope().resolveAttributeUsage(printName(redefinitionType));

        if(usage.isPresent()) {
          var usageType = usage.getClass(); //attributes may only have 1 type
          var nodeType = ((ASTSysMLTyping) node.getSpecializationList().stream().filter(
              t -> t instanceof ASTSysMLTyping).collect(
              Collectors.toList()).get(0)); //attributes may only have 1 type

          //TODO compare nodeType and usageType for compatibility
        }
        else {
          Log.error("Attribute Usage redefinition" + node.getName() + " could not be found");
        }
      }

    }
  }

  void checkTyping(ASTAttributeUsage node) {
    int typeCount = node.getSpecializationList().stream().filter(t -> t instanceof ASTSysMLTyping).collect(
        Collectors.toList()).size();
    if(typeCount != 1) {
      Log.error("Attribute usage " + node.getName() + " has " + typeCount + " typings, but needs exactly 1.");

    }

  }
}
