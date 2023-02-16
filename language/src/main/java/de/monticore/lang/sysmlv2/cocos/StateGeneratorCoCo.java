package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLRedefinition;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class StateGeneratorCoCo implements SysMLStatesASTStateUsageCoCo,SysMLStatesASTStateDefCoCo {

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that a usage only uses type, specialization or sub eleements
   */
  @Override
  public void check(ASTStateUsage node) {

    if(node.isParalled()) {
      Log.error("Parallel States are currently not supported, but " + node.getName() + " uses it.");
      int parallelStates = 0;
      for (ASTSysMLElement x : node.getSysMLElementList()) {
        if(x instanceof ASTStateUsage) {
          parallelStates++;
        }
      }
      if(parallelStates < 2) {
        Log.error(
            "StateUsage " + node.getName() + " has " + parallelStates + " \"StateUsages\" , but needs at least 2.");
      }
    }
    var specTypes = node.streamSpecializations().filter(t -> t instanceof ASTSysMLSpecialization).flatMap(
        ASTSpecialization::streamSuperTypes).collect(
        Collectors.toList());
    var typeTypes = node.streamSpecializations().filter(t -> t instanceof ASTSysMLTyping).flatMap(
        ASTSpecialization::streamSuperTypes).collect(
        Collectors.toList());
    if(node.streamSpecializations().anyMatch(t -> t instanceof ASTSysMLRedefinition))
      Log.error("State usage " + node.getName() + " uses redefinition, this is not allowed.");
    if(specTypes.size() > 1)
      Log.error("State usage " + node.getName() + " has more than one specialization, this is not allowed.");
    if(typeTypes.size() > 1)
      Log.error("State usage " + node.getName() + " has more than one type, this is not allowed.");
    if(specTypes.size() > 0 && typeTypes.size() > 0)
      Log.error("State usage " + node.getName() + " has a type and a specialization. It's not allowed to have both.");
    if(node.getSysMLElementList().size() > 0 && (typeTypes.size() > 0 || specTypes.size() > 0))
      Log.error("State usage " + node.getName()
          + " has sub-elements, but uses a type or a specialization. This is not allowed.");
  }

  /**
   * Check that a def does not use type, specialization or redefinition
   */
  @Override
  public void check(ASTStateDef node) {

    if(node.getSpecializationList().size()>0)
      Log.error("State def " + node.getName() + " has a specialization, redefinition or type none of these are allowed.");

  }

}

