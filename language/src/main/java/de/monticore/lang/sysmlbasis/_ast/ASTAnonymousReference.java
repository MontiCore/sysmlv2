package de.monticore.lang.sysmlbasis._ast;

public class ASTAnonymousReference extends ASTAnonymousReferenceTOP {

  @Override
  public String getName() {
    return getSrc().getQName();
  }

}
