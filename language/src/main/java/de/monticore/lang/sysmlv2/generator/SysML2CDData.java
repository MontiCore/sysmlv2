/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;


public class SysML2CDData {

  protected final ASTCDCompilationUnit compilationUnit;
  protected final ASTCDClass automataClass;

  public SysML2CDData(ASTCDCompilationUnit compilationUnit, ASTCDClass automataClass) {
    this.compilationUnit = compilationUnit;
    this.automataClass = automataClass;
  }

  public ASTCDCompilationUnit getCompilationUnit() {
    return compilationUnit;
  }

  public ASTCDClass getAutomataClass() {
    return automataClass;
  }

}
