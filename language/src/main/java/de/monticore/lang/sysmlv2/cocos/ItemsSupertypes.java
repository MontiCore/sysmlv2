/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmloccurrences._ast.ASTItemDef;
import de.monticore.lang.sysmloccurrences._ast.ASTItemUsage;
import de.monticore.lang.sysmloccurrences._cocos.SysMLOccurrencesASTItemDefCoCo;
import de.monticore.lang.sysmloccurrences._cocos.SysMLOccurrencesASTItemUsageCoCo;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

// TODO Muss mit SpecialiationExists zusammenspielen, also darf der nicht anschlagen, wenn garkein Type existiert,
// sondern nur, wenn zwar einer existiert, es aber keine ItemsDef/ItemsUsage ist
public class ItemsSupertypes implements SysMLOccurrencesASTItemDefCoCo, SysMLOccurrencesASTItemUsageCoCo {

  private String printName(ASTMCType type) {
    return type.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter()));
  }

  /**
   * Check that all super types (specializations) exist. They need to be items definitions.
   */
  @Override
  public void check(ASTItemDef node) {
    var nonExistent = node.streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> node.getEnclosingScope().resolveItemDef(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("0x10023 Could not find item definition \"" + printName(problem) + "\".");
    }
  }

  /**
   * Check that all super types (specializations) exist. They might be item definitions or usages.
   */
  @Override
  public void check(ASTItemUsage node) {
    var nonExistent = node.streamSpecializations()
        .flatMap(s -> s.streamSuperTypes())
        .filter(t -> node.getEnclosingScope().resolveItemDef(printName(t)).isEmpty()
            && node.getEnclosingScope().resolveItemUsage(printName(t)).isEmpty())
        .collect(Collectors.toList());

    for(var problem: nonExistent) {
      Log.error("0x10024 Could not find item definition or usage with the name \"" + printName(problem) + "\".");
    }
  }
}
