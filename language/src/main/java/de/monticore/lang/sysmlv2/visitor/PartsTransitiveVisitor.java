package de.monticore.lang.sysmlv2.visitor;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PartsTransitiveVisitor implements SysMLPartsVisitor2 {

  @Override
  public void visit(ASTPartDef node) {
    if(node.getTransitiveDefSupertypes().size() == 0) {
      node.setTransitiveDefSupertypes(
          getPartDefSuperTypesOfNode(getSpecializationList(node), node.getEnclosingScope()));
    }
    setAutomaton(node);
  }

  List<ASTPartDef> getPartDefSuperTypesOfNode(List<ASTMCType> superTypes, ISysMLPartsScope partsScope) {
    List<ASTPartDef> superTypeList = new ArrayList<>();
    for (ASTMCType superType : superTypes) {
      var partDefSymbol = partsScope.resolvePartDef(
          superType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())));
      if(partDefSymbol.isPresent()) {
        var partDef = partDefSymbol.get().getAstNode();
        superTypeList.add(partDef);
        if(partDef.getTransitiveDefSupertypes().size() == 0) {

          superTypeList.addAll(getPartDefSuperTypesOfNode(getSpecializationList(partDef), partDef.getEnclosingScope()));
        }
        else {
          superTypeList.addAll(partDef.getTransitiveDefSupertypes());
        }
      }
    }

    return superTypeList;
  }

  List<ASTMCType> getSpecializationList(ASTPartDef node) {
    return node.streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization)
        .flatMap(ASTSpecialization::streamSuperTypes).collect(Collectors.toList());

  }

  List<ASTMCType> getSpecializationList(ASTAttributeDef node) {
    return node.streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization)
        .flatMap(ASTSpecialization::streamSuperTypes).collect(Collectors.toList());

  }

  @Override
  public void visit(ASTPartUsage node) {
    if(node.getTransitiveUsageSupertypes().isEmpty()) {
      node.setTransitiveUsageSupertypes(getPartUsageSuperTypesOfNode(getPartUsageList(node), node.getEnclosingScope()));
      node.getTransitiveUsageSupertypes().forEach(this::visit);
    }
    if(node.getTransitiveDefSupertypes().isEmpty()) {
      List<ASTPartDef> superDefList = new ArrayList<>();
      superDefList.addAll(getPartDefSuperTypesOfNode(getPartDefList(node), node.getEnclosingScope()));
      List<ASTPartDef> transitiveDefList = node.getTransitiveUsageSupertypes().stream().flatMap(
          t -> t.getTransitiveDefSupertypes().stream()).collect(Collectors.toList());
      superDefList.addAll(transitiveDefList);
      node.setTransitiveDefSupertypes(superDefList);
    }
    setAutomaton(node);
  }

  List<ASTPartUsage> getPartUsageSuperTypesOfNode(List<ASTMCType> superTypes, ISysMLPartsScope partsScope) {
    List<ASTPartUsage> superTypeList = new ArrayList<>();
    for (ASTMCType superType : superTypes) {
      var partUsageSymbol = partsScope.resolvePartUsage(
          superType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())));
      if(partUsageSymbol.isPresent()) {
        var partUsage = partUsageSymbol.get().getAstNode();
        superTypeList.add(partUsage);
        if(partUsage.getTransitiveUsageSupertypes().size() == 0) {

          superTypeList.addAll(getPartUsageSuperTypesOfNode(getPartDefList(partUsage), partUsage.getEnclosingScope()));
        }
        else {
          superTypeList.addAll(partUsage.getTransitiveUsageSupertypes());
        }
      }
    }

    return superTypeList;
  }

  public void visit(ASTAttributeDef node) {
    if(node.getTransitiveDefSupertypes().size() == 0) {
      node.setTransitiveDefSupertypes(
          getAttributeDefSuperTypesOfNode(getSpecializationList(node), node.getEnclosingScope()));
    }
  }

  List<ASTAttributeDef> getAttributeDefSuperTypesOfNode(List<ASTMCType> superTypes, ISysMLPartsScope partsScope) {
    List<ASTAttributeDef> superTypeList = new ArrayList<>();
    for (ASTMCType superType : superTypes) {
      var attributeDefSymbol = partsScope.resolveAttributeDef(
          superType.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())));
      if(attributeDefSymbol.isPresent()) {
        var attributeDef = attributeDefSymbol.get().getAstNode();
        superTypeList.add(attributeDef);
        if(attributeDef.getTransitiveDefSupertypes().size() == 0) {

          superTypeList.addAll(
              getAttributeDefSuperTypesOfNode(getSpecializationList(attributeDef), attributeDef.getEnclosingScope()));
        }
        else {
          superTypeList.addAll(attributeDef.getTransitiveDefSupertypes());
        }
      }
    }

    return superTypeList;
  }

  List<ASTMCType> getPartDefList(ASTPartUsage node) {
    return node.streamSpecializations().filter(t -> t instanceof ASTSysMLTyping)
        .flatMap(ASTSpecialization::streamSuperTypes).collect(Collectors.toList());

  }

  List<ASTMCType> getPartUsageList(ASTPartUsage node) {
    return node.streamSpecializations().filter(
            t -> t instanceof ASTSysMLSpecialization | t instanceof ASTSysMLRedefinition)
        .flatMap(ASTSpecialization::streamSuperTypes).collect(Collectors.toList());

  }

  void setAutomaton(ASTPartUsage element) {
    List<ASTStateUsage> stateUsageList =  element.streamSysMLElements().filter(
        t -> t instanceof ASTStateUsage).map(t -> (ASTStateUsage) t).filter(t -> t.isExhibited()).collect(
        Collectors.toList());

    if(!stateUsageList.isEmpty())
      element.setAutomaton(stateUsageList.get(0));
  }
  void setAutomaton(ASTPartDef element) {
    List<ASTStateUsage> stateUsageList = element.streamSysMLElements().filter(
        t -> t instanceof ASTStateUsage).map(t -> (ASTStateUsage) t).filter(t -> t.isExhibited()).collect(
        Collectors.toList());

    if(!stateUsageList.isEmpty())
      element.setAutomaton(stateUsageList.get(0));
  }
}
