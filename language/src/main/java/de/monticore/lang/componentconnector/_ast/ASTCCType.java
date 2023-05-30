package de.monticore.lang.componentconnector._ast;

import de.monticore.symboltable.ISymbol;

import java.util.Optional;

// TODO
public class ASTCCType extends ASTCCTypeTOP {
  @Override
  public Optional<? extends ISymbol> getDefiningSymbol() {
    return Optional.empty();
  }

  @Override
  public void setDefiningSymbol(ISymbol symbol) {

  }
}
