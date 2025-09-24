/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlactions._ast.ASTActionDef;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintDef;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementDef;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTConstraintDefCoCo;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTRequirementDefCoCo;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTSysMLPackageCoCo;
import de.monticore.lang.sysmlparts._ast.ASTAttributeDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

public class NameCompatible4Isabelle implements SysMLStatesASTStateDefCoCo,
    SysMLPartsASTPartDefCoCo, SysMLPartsASTPortDefCoCo, SysMLConstraintsASTConstraintDefCoCo,
    SysMLPartsASTAttributeDefCoCo, SysMLActionsASTActionDefCoCo,
    SysMLConstraintsASTRequirementDefCoCo,
    SysMLImportsAndPackagesASTSysMLPackageCoCo {
  //check for name
  private void LogsCompatible4Isabelle(String name, SourcePosition start, SourcePosition end) {
    if(!name.matches("^(?!0-9)[a-zA-Z0-9_]+$") && !name.isEmpty()) {
      Log.error("0xFF005 This name is not Isabelle compatible", start, end);
    }
  }

  //state
  @Override
  public void check (ASTStateDef node) {
    String name = node.getName();
    LogsCompatible4Isabelle(name, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  //part
  @Override
  public void check (ASTPartDef node) {
    String name = node.getName();
    LogsCompatible4Isabelle(name, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  //port
  @Override
  public void check (ASTPortDef node) {
    String name = node.getName();
    LogsCompatible4Isabelle(name, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  //constraint
  @Override
  public void check (ASTConstraintDef node) {
    String name = node.getName();
    LogsCompatible4Isabelle(name, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  //attribute
  @Override
  public void check (ASTAttributeDef node) {
    String name = node.getName();
    LogsCompatible4Isabelle(name, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  //action
  @Override
  public void check (ASTActionDef node) {
    String name = node.getName();
    LogsCompatible4Isabelle(name, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  //requirement
  @Override
  public void check (ASTRequirementDef node) {
    String name = node.getName();
    LogsCompatible4Isabelle(name, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }

  //package
  @Override
  public void check (ASTSysMLPackage node) {
    String name = node.getName();
    LogsCompatible4Isabelle(name, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
  }
}
