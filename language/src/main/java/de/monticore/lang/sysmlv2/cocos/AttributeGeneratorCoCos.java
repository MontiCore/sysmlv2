package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeUsageCoCo;
import de.monticore.lang.sysmlv2.generator.AttributeResolveUtils;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class AttributeGeneratorCoCos implements SysMLPartsASTAttributeUsageCoCo, SysMLPartsASTAttributeDefCoCo {
 AttributeResolveUtils attributeResolveUtils = new AttributeResolveUtils();
  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that only one redefinition is used and that it is resolvable.
   */
  @Override
  public void check(ASTAttributeUsage node) {
    checkTypingExists(node);
    var redefinitionList = node.getSpecializationList().stream().filter(t -> t instanceof ASTSysMLRedefinition).collect(
        Collectors.toList());
    if(!redefinitionList.isEmpty()) {
      for (ASTSpecialization redefinition : redefinitionList) {
        if(redefinition.getSuperTypesList().size() != 1) {
          Log.error("Attribute Usage " + node.getName() + " has "
              + redefinition.getSuperTypesList().size()
              + " redefinitions, but may only redefine 1.");
        }
        String parentAttributeName = printName(redefinition.getSuperTypes(0));
        if(!node.getName().equals(parentAttributeName)) {
          Log.error("Attribute Usage " + node.getName()
              + " has to redefine an Attribute Usage with the same name, but redefines "
              + parentAttributeName + ".");
        }
      }
    }

  }

public void check(ASTAttributeDef node){
    attributeResolveUtils.getAttributesOfElement(node);

}

  void checkTypingExists(ASTAttributeUsage node) {
    long typeCount = node.getSpecializationList().stream().filter(t -> t instanceof ASTSysMLTyping).count();
    if(typeCount != 1) {
      Log.error("Attribute usage " + node.getName() + " has " + typeCount + " typings, but needs exactly 1.");
    }
  }

}
