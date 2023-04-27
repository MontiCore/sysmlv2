package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._ast.ASTActionUsage;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ImportUtils {

  static public List<ASTSysMLImportStatement> getImportStatements(
      de.monticore.lang.sysmlv2._ast.ASTSysMLModel rootNode) {
    return rootNode.streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(Collectors.toList());
  }

  static List<ASTSysMLImportStatement> getImportStatements(ASTSysMLElement element) {
    if(element instanceof ASTSysMLImportStatement)
      return List.of((ASTSysMLImportStatement) element);
    if(element instanceof ASTPartUsage)
      return ((ASTPartUsage) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    if(element instanceof ASTPartDef)
      return ((ASTPartDef) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    if(element instanceof ASTSysMLPackage)
      return ((ASTSysMLPackage) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    if(element instanceof ASTStateUsage)
      return ((ASTStateUsage) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    if(element instanceof ASTStateDef)
      return ((ASTStateDef) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    if(element instanceof ASTActionUsage)
      return ((ASTActionUsage) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    if(element instanceof ASTActionDef)
      return ((ASTActionDef) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    if(element instanceof ASTPortUsage)
      return ((ASTPortUsage) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    if(element instanceof ASTPortDef)
      return ((ASTPortDef) element).streamSysMLElements().flatMap(t -> getImportStatements(t).stream()).collect(
          Collectors.toList());
    return new ArrayList<>();
  }
}
