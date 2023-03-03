package de.monticore.lang.sysmlv2.generator.utils;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._ast.ASTCDInterfaceUsage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InterfaceUtils {
  static public ASTCDInterfaceUsage createInterfaceUsage(List<ASTSysMLElement> sysMLList) {
    //Step 1 get a list of all specializations
    ASTCDInterfaceUsage interfaceUsage = CD4CodeMill.cDInterfaceUsageBuilder().build();
    for (ASTSysMLElement sysMLElement : sysMLList) {
      String name = null;

      if(sysMLElement instanceof ASTPartDef) {
        name = ((ASTPartDef) sysMLElement).getName();
      }
      if(sysMLElement instanceof ASTAttributeDef) {
        name = ((ASTAttributeDef) sysMLElement).getName();
      }
      //Step 2 add the created interface to the InterfaceUsage
      ASTMCQualifiedType mcQualifiedType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          CD4CodeMill.mCQualifiedNameBuilder().
              addParts(name + "Interface").build()).build();
      interfaceUsage.addInterface(mcQualifiedType);
    }
    return interfaceUsage;
  }

  static public ASTCDExtendUsage createExtendUsage(List<ASTMCType> supertypeList, boolean extendsInterface) {
    String suffix = "";
    if(extendsInterface)
      suffix = "Interface";
    ASTCDExtendUsage extendUsage = CD4CodeMill.cDExtendUsageBuilder().build();

    //Step 2 add each specialization to the Extend Usage
    for (ASTMCType element : supertypeList) {
      String elementName = element.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));

      ASTMCQualifiedType mcQualifiedType = CD4CodeMill.mCQualifiedTypeBuilder().setMCQualifiedName(
          CD4CodeMill.mCQualifiedNameBuilder().
              addParts(elementName + suffix).build()).build();
      extendUsage.addSuperclass(mcQualifiedType);
    }
    return extendUsage;
  }

  static public ASTCDInterface createInterface(ASTSysMLElement sysMLElement) {
    String name = null;
    List<ASTSpecialization> specializationList = new ArrayList<>();
    if(sysMLElement instanceof ASTPartDef) {
      name = ((ASTPartDef) sysMLElement).getName();
      specializationList = ((ASTPartDef) sysMLElement).getSpecializationList();
    }
    if(sysMLElement instanceof ASTAttributeDef) {
      name = ((ASTAttributeDef) sysMLElement).getName();
      specializationList = ((ASTAttributeDef) sysMLElement).getSpecializationList();
    }
    List<ASTMCType> supertypeList = specializationList.stream().filter(
        t -> t instanceof ASTSysMLSpecialization).flatMap(ASTSpecialization::streamSuperTypes).collect(
        Collectors.toList());
    ASTCDExtendUsage extendUsage = createExtendUsage(supertypeList, true);
    ASTCDInterface partInterface = CD4CodeMill.cDInterfaceBuilder().setName(name + "Interface").setModifier(
        CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
    ActionsUtils actionsUtils = new ActionsUtils();
    actionsUtils.createActionsForInterface(sysMLElement, partInterface);
    if(!extendUsage.isEmptySuperclass()) {
      partInterface.setCDExtendUsage(extendUsage);
    }
    return partInterface;

  }

}
