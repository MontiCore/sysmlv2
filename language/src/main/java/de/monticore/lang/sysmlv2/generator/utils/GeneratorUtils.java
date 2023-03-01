package de.monticore.lang.sysmlv2.generator.utils;

import com.google.common.collect.ImmutableMap;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Splitters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GeneratorUtils {
  private HashMap<String, String> scalarValueMapping = new HashMap<String, String>();

  private HashMap<String, String> primitiveWrapperMap = new HashMap<String, String>();

  protected final CD4C cd4C;

  public GeneratorUtils() {
    this.cd4C = CD4C.getInstance();
    //mapping of ScalarValues defined in the Kernel Modeling language
    scalarValueMapping.put("Boolean", "boolean");
    scalarValueMapping.put("Integer", "int");
    scalarValueMapping.put("Natural", "int");
    scalarValueMapping.put("Number", "int");
    scalarValueMapping.put("NumericalValue", "int");
    scalarValueMapping.put("Positive", "int");
    scalarValueMapping.put("Rational", "double");
    scalarValueMapping.put("Real", "float");
    scalarValueMapping.put("ScalarValue", "float");
    scalarValueMapping.put("String", "String");
    primitiveWrapperMap.put("boolean", "Boolean");
    primitiveWrapperMap.put("char", "Character");
    primitiveWrapperMap.put("byte", "Byte");
    primitiveWrapperMap.put("short", "Short");
    primitiveWrapperMap.put("int", "Integer");
    primitiveWrapperMap.put("long", "Long");
    primitiveWrapperMap.put("float", "Float");
    primitiveWrapperMap.put("double", "Double");
    //TODO maybe support Collections
  }

  public void addMethods(ASTCDType astcdType, List<ASTCDAttribute> attributeList, boolean addGetter,
                         boolean addSetter) {
    for (ASTCDAttribute element : attributeList) {
      cd4C.addMethods(astcdType, element, addGetter, addSetter);
    }

  }

  public ASTMCQualifiedType attributeType(ASTAttributeUsage element) {
    var sysMLTypingList = element.getSpecializationList().stream().filter(
        t -> t instanceof ASTSysMLTyping).map(u -> ((ASTSysMLTyping) u)).collect(Collectors.toList());
    String typString = sysMLTypingList.get(0).getSuperTypes(0).printType(
        new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
    List<String> partsList = Splitters.DOT.splitToList(typString);
    String typeName = partsList.get(partsList.size() - 1);
    if(scalarValueMapping.containsKey(typeName))
      partsList = List.of(scalarValueMapping.get(typeName));
    return qualifiedType(partsList);
  }

  public ASTMCQualifiedType qualifiedType(String qname) {
    return qualifiedType(Splitters.DOT.splitToList(qname));
  }

  protected ASTMCQualifiedType qualifiedType(List<String> partsList) {
    return CD4CodeMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(partsList).build()).build();
  }

  public ASTCDPackage initCdPackage(ASTSysMLElement element, ASTCDDefinition astcdDefinition, String baseName) {
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

    return cdPackage;
  }

  List<String> initCdPackage(ASTSysMLElement element, List<String> partList) {
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

  boolean isBasicType(String typeName) {
    return scalarValueMapping.containsKey(typeName);
  }

  public ImmutableMap<String, String> getScalarValueMapping() {
    return ImmutableMap.copyOf(scalarValueMapping);
  }

  public String mapToWrapper(String primitive) {
    return primitiveWrapperMap.get(primitive);
  }
}
