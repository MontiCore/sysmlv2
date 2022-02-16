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

}
