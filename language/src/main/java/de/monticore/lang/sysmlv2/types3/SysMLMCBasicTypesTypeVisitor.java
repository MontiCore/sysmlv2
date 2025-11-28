package de.monticore.lang.sysmlv2.types3;

import de.monticore.lang.sysmlexpressions._ast.ASTMCPrimitiveTypeWithNat;
import de.monticore.lang.sysmlexpressions._visitor.SysMLExpressionsVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;

public class SysMLMCBasicTypesTypeVisitor extends MCBasicTypesTypeVisitor implements
    SysMLExpressionsVisitor2 {

  @Override
  public void endVisit(ASTMCPrimitiveTypeWithNat node) {
    endVisit((ASTMCPrimitiveType) node);
  }
}
