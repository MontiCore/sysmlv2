/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlconnections._ast.ASTInterfaceDef;
import de.monticore.lang.sysmlconnections._ast.ASTInterfaceUsage;
import de.monticore.lang.sysmlconnections._cocos.SysMLConnectionsASTInterfaceDefCoCo;
import de.monticore.lang.sysmlconnections._cocos.SysMLConnectionsASTInterfaceUsageCoCo;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

// TODO Muss mit SpecialiationExists zusammenspielen, also darf der nicht anschlagen, wenn garkein Type existiert,
// sondern nur, wenn zwar einer existiert, es aber keine InterfaceDef/InterfaceUsage ist
public class InterfaceSupertypes implements SysMLConnectionsASTInterfaceDefCoCo, SysMLConnectionsASTInterfaceUsageCoCo {

  private String printName(ASTMCType type) {
    return type.printType();
  }

  /**
   * Check that all super types (specializations) exist. They need to be Interface definitions.
   */
  @Override
  public void check(ASTInterfaceDef node) {
    var nonExistent = node.streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> node.getEnclosingScope().resolveInterfaceDef(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("0x10021 Could not find Interface definition \"" + printName(problem) + "\".");
    }
  }

  /**
   * Check that all super types (specializations) exist. They might be Interface definitions or usages.
   */
  @Override
  public void check(ASTInterfaceUsage node) {
    var nonExistent = node.streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> node.getEnclosingScope().resolveInterfaceDef(printName(t)).isEmpty()
            && node.getEnclosingScope().resolveInterfaceUsage(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("0x10022 Could not find Interface definition or usage with the name \"" + printName(problem) + "\".");
    }
  }
}
