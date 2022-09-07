/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml4verificationblockdiagrams._ast;

import de.monticore.lang.sysmlblockdiagrams._symboltable.PartDefSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.Splitters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ASTPartDef extends ASTPartDefTOP {

  private final boolean DEFAULT_CAUSALITY_INSTANT = true;
  private final boolean DEFAULT_CAUSALITY_DELAYED = !DEFAULT_CAUSALITY_INSTANT;
  public static final int DEFAULT_RELATIVE_TRUST_LEVEL = -1;

  /**
   * PartDefinitions that this PartDefinition is a refinement of
   */
  public List<PartDefSymbol> getRefinements() {
    List<PartDefSymbol> refinements = new ArrayList<>();
    if(this.isPresentSysMLRefinement()) {
      refinements = this.getSysMLRefinement().getRoughDefList().stream()
          .map(obj -> ((ASTMCQualifiedType)obj).getNameList().stream().collect(Collectors.joining(Splitters.DOT.toString()))) // TODO könnte auch andere Subklasse von ASTMCObjectType sein // Wir haben BasicGenerics genommen, PartDefs haben erstmal keine <Generics>
          .map(name -> this.enclosingScope.resolvePartDef(name))
          .filter(optSym -> optSym.isPresent()) // Deffensiv
          .map(optSym -> optSym.get())
          .collect(Collectors.toList());
    }
    return refinements;
  }

  /**
   * Erleichert das Abfragen von Timings von Blöcken
   * @return ob in der BlockDefinition (falls es eine gibt) das Timing als Instant spezifiziert wurde
   */
  public boolean isInstant() {
    if(this.sysMLCausalitys.size() > 0) {
      return this.sysMLCausalitys.get(0).isInstant();
    }
    else {
      return DEFAULT_CAUSALITY_INSTANT;
    }
  }

  /**
   * Erleichert das Abfragen von Timings von Blöcken
   * @return ob in der BlockDefinition (falls es eine gibt) das Timing als Delayed spezifiziert wurde
   */
  public boolean isDelayed() {
    return !isInstant();
  }

  /**
   * Erleichtert das Abfragen vom Trustlevel einer PartDefinition
   * @return den Wert der trustlevel Angabe, falls es eine gibt, sonst wird ein Defaultwert zurückgegeben; das Trustlevel
   *  wird immer relativ zur Oberkomponente angegeben
   */
  public int getRelativeTrustLevel() {

    if(this.trustLevels.size() > 0) {
      if(this.trustLevels.get(0).mINUS) {
        return (-1) * this.trustLevels.get(0).natLiteral.getValue();
      }
      return this.trustLevels.get(0).natLiteral.getValue();
    }
    else {
      return DEFAULT_RELATIVE_TRUST_LEVEL;
    }
  }

}
