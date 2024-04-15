/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlrequirements._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlstates._ast.ASTStateUsage;
import de.se_rwth.commons.logging.Log;

/**
 * Die SPES-Methodik sieht 3 verschiedene Arten der Verhaltenbeschreibung vor:
 * - Abstrakte (deklarative) Spezifikationen
 * - Zustandsmaschinen
 * - (De-)Komposition
 * Diese wurden so auch in SpesML umgesetzt. Wir (SpesML v2) sehen es so vor, dass
 * analog die PartDefs als Signatur-Definition einer Komponente dienen, während darin
 * eine der drei genannten Verhaltensspezifikationen liegen muss. Diese CoCo prüft,
 * dass genau eine der drei vorhanden ist.
 */
public class PartBehaviorCoCo implements SysMLPartsASTPartDefCoCo {

  @Override
  public void check(ASTPartDef node) {
    int constraintAvailable = checkForConstraintUsageInReqUsage(node) ? 1 : 0;
    int automatonAvailable = node.getSysMLElements(ASTStateUsage.class).size() > 0 ? 1 : 0;
    int decompositionAvailable = node.getSysMLElements(ASTPartUsage.class).size() > 0 ? 1 : 0;

    if((constraintAvailable + automatonAvailable + decompositionAvailable) == 0) {
      Log.warn("0xFF0003 Part " + node.getName() + " has no explicit behavior!");
    }
    if((constraintAvailable + automatonAvailable + decompositionAvailable) > 1) {
      Log.warn("0xFF0004 Part " + node.getName() + " must contain exactly one "
          + "behavior specification (constraint, automaton or composition) but has " +
          (constraintAvailable + automatonAvailable + decompositionAvailable));
    }
  }

  private boolean checkForConstraintUsageInReqUsage(ASTPartDef node) {
    for(ASTSysMLElement reqUsage : node.getSysMLElements(ASTRequirementUsage.class)){
      for(ASTSysMLElement element : ((ASTRequirementUsage) reqUsage).getSysMLElementList()){
        if(element instanceof ASTConstraintUsage){
          return true;
        }
      }
    }
    return false;
  }
}
