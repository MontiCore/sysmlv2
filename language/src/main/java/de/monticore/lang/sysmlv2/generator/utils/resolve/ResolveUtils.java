package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResolveUtils {

  List<ASTSysMLElement> getDirectSupertypes(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = new ArrayList<>();
    //Get direct supertypes
    if(node instanceof ASTPartDef) {
      parentList = ((ASTPartDef) node).streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartDef) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      parentList = ((ASTPartUsage) node).streamSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartUsage(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
      parentList.addAll(((ASTPartUsage) node).streamSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList()));
    }
    if(node instanceof ASTAttributeDef) {
      parentList = ((ASTAttributeDef) node).streamSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTAttributeDef) node).getEnclosingScope().resolveAttributeDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPortUsage) {
      parentList = ((ASTPortUsage) node).streamSpecializations().filter(
          t -> t instanceof ASTSysMLTyping).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPortUsage) node).getEnclosingScope().resolvePortDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPortDef) {
      parentList = ((ASTPortDef) node).streamSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPortDef) node).getEnclosingScope().resolvePortDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    return parentList;
  }

  String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

}
