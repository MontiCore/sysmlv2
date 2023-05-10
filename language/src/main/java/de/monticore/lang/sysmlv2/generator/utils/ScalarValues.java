package de.monticore.lang.sysmlv2.generator.utils;

import com.google.common.collect.ImmutableMap;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Splitters;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ScalarValues {

  public ScalarValues() {
    //mapping of ScalarValues defined in the Kernel Modeling language
    ScalarValues.scalarValueMapping.put("Boolean", "boolean");
    ScalarValues.scalarValueMapping.put("Integer", "int");
    ScalarValues.scalarValueMapping.put("Natural", "int");
    ScalarValues.scalarValueMapping.put("Number", "int");
    ScalarValues.scalarValueMapping.put("NumericalValue", "int");
    ScalarValues.scalarValueMapping.put("Positive", "int");
    ScalarValues.scalarValueMapping.put("Rational", "double");
    ScalarValues.scalarValueMapping.put("Real", "float");
    ScalarValues.scalarValueMapping.put("ScalarValue", "float");
    ScalarValues.scalarValueMapping.put("String", "String");
    ScalarValues.primitiveWrapperMap.put("boolean", "Boolean");
    ScalarValues.primitiveWrapperMap.put("char", "Character");
    ScalarValues.primitiveWrapperMap.put("byte", "Byte");
    ScalarValues.primitiveWrapperMap.put("short", "Short");
    ScalarValues.primitiveWrapperMap.put("int", "Integer");
    ScalarValues.primitiveWrapperMap.put("long", "Long");
    ScalarValues.primitiveWrapperMap.put("float", "Float");
    ScalarValues.primitiveWrapperMap.put("double", "Double");
    //TODO maybe support Collections
  }
  static final HashMap<String, String> scalarValueMapping = new HashMap<>();

  static final HashMap<String, String> primitiveWrapperMap = new HashMap<>();

  public static ImmutableMap<String, String> getScalarValueMapping() {
    return ImmutableMap.copyOf(scalarValueMapping);
  }

  static public ASTMCQualifiedType attributeType(ASTAttributeUsage element) {
    var sysMLTypingList = element.getUsageSpecializationList().stream().filter(
        t -> t instanceof ASTSysMLTyping).map(u -> ((ASTSysMLTyping) u)).collect(Collectors.toList());
    String typString = sysMLTypingList.get(0).getSuperTypes(0).printType(
        new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
    List<String> partsList = Splitters.DOT.splitToList(typString);
    String typeName = partsList.get(partsList.size() - 1);
    if(scalarValueMapping.containsKey(typeName))
      partsList = List.of(scalarValueMapping.get(typeName));
    return PackageUtils.qualifiedType(partsList);
  }

  public static String mapToWrapper(String primitive) {
    return ScalarValues.primitiveWrapperMap.get(primitive);
  }
  public static String getValueTypeOfPort(ASTPortUsage portUsage) {
    return mapToWrapper(printName(ScalarValues.attributeType(portUsage.getValueAttribute())));
  }
  private static String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }
}
