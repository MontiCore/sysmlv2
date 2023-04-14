package de.monticore.lang.sysmlv2.generator.utils.resolve;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResolveUtils {

  static List<ASTSysMLElement> getDirectSupertypes(ASTSysMLElement node) {
    List<ASTSysMLElement> parentList = new ArrayList<>();
    //Get direct supertypes
    if(node instanceof ASTPartDef) {
      parentList = ((ASTPartDef) node).streamDefSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartDef) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPartUsage) {
      parentList = ((ASTPartUsage) node).streamUsageSpecializations().filter(
          t -> t instanceof ASTSysMLSubsetting).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartUsage(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
      parentList.addAll(((ASTPartUsage) node).streamUsageSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPartUsage) node).getEnclosingScope().resolvePartDef(printName(t))).filter(Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList()));
    }
    if(node instanceof ASTAttributeDef) {
      parentList = ((ASTAttributeDef) node).streamDefSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTAttributeDef) node).getEnclosingScope().resolveAttributeDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPortUsage) {
      parentList = ((ASTPortUsage) node).streamUsageSpecializations().filter(
          t -> t instanceof ASTSysMLTyping).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPortUsage) node).getEnclosingScope().resolvePortDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTPortDef) {
      parentList = ((ASTPortDef) node).streamDefSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTPortDef) node).getEnclosingScope().resolvePortDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTStateDef) {
      parentList = ((ASTStateDef) node).streamDefSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTStateDef) node).getEnclosingScope().resolveStateDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
    }
    if(node instanceof ASTStateUsage) {
      parentList = ((ASTStateUsage) node).streamUsageSpecializations().filter(
          t -> t instanceof ASTSysMLSpecialization).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTStateUsage) node).getEnclosingScope().resolveStateUsage(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList());
      parentList.addAll(((ASTStateUsage) node).streamUsageSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
          f -> f.getSuperTypesList().stream()).map(
          t -> ((ASTStateUsage) node).getEnclosingScope().resolveStateDef(printName(t))).filter(
          Optional::isPresent).map(
          t -> t.get().getAstNode()).collect(
          Collectors.toList()));
    }
    return parentList;
  }

  static String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

}
