package de.monticore.lang.sysmlv2._ast;

public class ASTKerMLCardinality extends ASTKerMLCardinalityTOP {

  /*public int getLowerBound() {
    if(getLower() instanceof ASTLiteralExpression) {
      if(((ASTLiteralExpression) getLower()).getLiteral() instanceof ASTNatLiteral) {
        ASTNatLiteral v = (ASTNatLiteral) ((ASTLiteralExpression) getLower()).getLiteral();
        return v.getValue();
      }
    }
    Log.error("0xA0001 KerML cardinality cannot produce a value for lower bound");
    return -1;
  }

  public int getUpperBound() {
    if(isPresentUpper()) {
      if(getUpper() instanceof ASTLiteralExpression) {
        if(((ASTLiteralExpression) getUpper()).getLiteral() instanceof ASTNatLiteral) {
          ASTNatLiteral v = (ASTNatLiteral) ((ASTLiteralExpression) getUpper()).getLiteral();
          return v.getValue();
        }
      }
    }
    else {
      return getLowerBound();
    }
    Log.error("0xA0002 KerML cardinality cannot produce a value for upper bound");
    return -1;
  }*/

}
