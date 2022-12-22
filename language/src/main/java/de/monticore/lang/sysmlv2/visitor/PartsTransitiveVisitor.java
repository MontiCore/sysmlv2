package de.monticore.lang.sysmlv2.visitor;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartsTransitiveVisitor implements SysMLPartsVisitor2 {

  @Override
  public void visit(ASTPartDef node) {
    if(node.getTransitiveSupertypes().size() == 0) {
      node.setTransitiveSupertypes(getSuperTypesOfNode(getSpecializationList(node), node));
    }
  }

  List<ASTPartDef> getSuperTypesOfNode(List<ASTMCType> superTypes, ASTPartDef node) {
    List<ASTPartDef> superTypeList = new ArrayList<>();
    for (ASTMCType superType : superTypes) {
      var partDefSymbol = node.getEnclosingScope().resolvePartDef(
          superType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())));
      if(partDefSymbol.isPresent()) {
        var partDef = partDefSymbol.get().getAstNode();
        superTypeList.add(partDef);
        if(partDef.getTransitiveSupertypes().size() == 0) {

          superTypeList.addAll(getSuperTypesOfNode(getSpecializationList(partDef), partDef));
        }
        else {
          superTypeList.addAll(partDef.getTransitiveSupertypes());
        }
      }
    }

    return superTypeList;
  }

  List<ASTMCType> getSpecializationList(ASTPartDef node) {
    return node.streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization)
        .flatMap(s -> s.streamSuperTypes()).collect(Collectors.toList());

  }

  @Override
  public void visit(ASTPartUsage node) {
    if(node.getTransitiveSupertypes().size() == 0) {
      node.setTransitiveSupertypes(getSuperTypesOfNode(getSpecializationList(node), node));
    }
  }

  List<ASTPartDef> getSuperTypesOfNode(List<ASTMCType> superTypes, ASTPartUsage node) {
    List<ASTPartDef> superTypeList = new ArrayList<>();
    for (ASTMCType superType : superTypes) {
      var partDefSymbol = node.getEnclosingScope().resolvePartDef(
          superType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())));
      if(partDefSymbol.isPresent()) {
        var partDef = partDefSymbol.get().getAstNode();
        superTypeList.add(partDef);
        if(partDef.getTransitiveSupertypes().size() == 0) {

          superTypeList.addAll(getSuperTypesOfNode(getSpecializationList(partDef), partDef));
        }
        else {
          superTypeList.addAll(partDef.getTransitiveSupertypes());
        }
      }
    }

    return superTypeList;
  }

  List<ASTMCType> getSpecializationList(ASTPartUsage node) {
    return node.streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization)
        .flatMap(s -> s.streamSuperTypes()).collect(Collectors.toList());

  }
}
