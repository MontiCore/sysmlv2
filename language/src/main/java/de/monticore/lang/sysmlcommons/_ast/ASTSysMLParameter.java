package de.monticore.lang.sysmlcommons._ast;

public class ASTSysMLParameter extends ASTSysMLParameterTOP {
  /**
   * Method overridden to add cloning of the associated symbol as well.
   *
   * @return ASTSysMLParameter
   */
  @Override
  public ASTSysMLParameter deepClone() {
    ASTSysMLParameter clone = super.deepClone();
    if(this.isPresentSymbol()) {
      clone.setSymbol(this.getSymbol().deepClone());
    }
    return clone;
  }
}
