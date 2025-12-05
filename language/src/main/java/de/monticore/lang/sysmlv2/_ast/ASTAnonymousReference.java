package de.monticore.lang.sysmlv2._ast;

public class ASTAnonymousReference extends ASTAnonymousReferenceTOP {

  @Override
  public String getName() {
    return getSrc().getQName();
  }

}
