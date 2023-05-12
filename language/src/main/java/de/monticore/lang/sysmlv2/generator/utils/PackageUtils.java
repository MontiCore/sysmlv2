package de.monticore.lang.sysmlv2.generator.utils;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2ArtifactScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Splitters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PackageUtils {

  public static void addMethods(ASTCDType astcdType, List<ASTCDAttribute> attributeList, boolean addGetter,
                         boolean addSetter) {
    CD4C cd4C = CD4C.getInstance();
    for (ASTCDAttribute element : attributeList) {
      cd4C.addMethods(astcdType, element, addGetter, addSetter);
    }

  }

  static public ASTMCQualifiedType qualifiedType(String qname) {
    return qualifiedType(Splitters.DOT.splitToList(qname));
  }

  static protected ASTMCQualifiedType qualifiedType(List<String> partsList) {
    return CD4CodeMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(partsList).build()).build();
  }

  static public ASTCDPackage initCdPackage(ASTSysMLElement element, ASTCDDefinition astcdDefinition, String baseName) {
    List<String> basePackageName = List.of(baseName);
    List<String> partList = initCdPackage(element, basePackageName);
    ASTMCQualifiedName qualifiedName = CD4CodeMill.mCQualifiedNameBuilder()
        .setPartsList(partList)
        .build();
    ASTCDPackage cdPackage = CD4CodeMill.cDPackageBuilder()
        .setMCQualifiedName(qualifiedName)
        .build();
    var ListCDPackages = astcdDefinition.getCDElementList().stream().filter(t -> t instanceof ASTCDPackage).flatMap(
        t -> ((ASTCDPackage) t).streamCDElements()).filter(t -> t instanceof ASTCDPackage).collect(Collectors.toList());
    if(ListCDPackages.stream().noneMatch(
        t -> ((ASTCDPackage) t).getMCQualifiedName().getQName().equals(qualifiedName.getQName()))) {
      astcdDefinition.addCDElement(cdPackage);
    }
    else {
      cdPackage = (ASTCDPackage) ListCDPackages.stream().filter(
          t -> ((ASTCDPackage) t).getMCQualifiedName().getQName().equals(qualifiedName.getQName())).findFirst().get();
    }

    return cdPackage;
  }

  static List<String> initCdPackage(ASTSysMLElement element, List<String> partList) {
    List<String> packagePartList = new ArrayList<>(partList);
    var astNode = element.getEnclosingScope().getAstNode();
    if(astNode instanceof ASTSysMLElement) {
      ASTSysMLElement astSysMLElement = (ASTSysMLElement) astNode;
      if(astSysMLElement instanceof ASTSysMLPackage) {
        var packageName = ((ASTSysMLPackage) astSysMLElement).getName().toLowerCase();

        packagePartList.add(packageName);

        packagePartList = initCdPackage(astSysMLElement, packagePartList);
      }
      else if(!(astSysMLElement instanceof SysMLv2ArtifactScope)) {
        packagePartList = initCdPackage(astSysMLElement, partList);
      }
      else {
        return packagePartList;
      }
    }
    return packagePartList;
  }

  public static String cdPackageAsQualifiedName(ASTSysMLElement element, String baseName){
    List<String> basePackageName = List.of(baseName);
    return String.join(".",initCdPackage(element, basePackageName));
  }
}
