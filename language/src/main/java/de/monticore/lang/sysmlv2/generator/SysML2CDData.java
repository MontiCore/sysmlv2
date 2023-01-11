/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.generator;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;

import java.util.Collection;

public class SysML2CDData {

  protected final ASTCDCompilationUnit compilationUnit;
  protected final ASTCDClass automataClass;
  protected final Collection<ASTCDClass> stateClasses;

  public SysML2CDData(ASTCDCompilationUnit compilationUnit, ASTCDClass automataClass, Collection<ASTCDClass> stateClasses) {
    this.compilationUnit = compilationUnit;
    this.automataClass = automataClass;
    this.stateClasses = stateClasses;
  }

  public ASTCDCompilationUnit getCompilationUnit() {
    return compilationUnit;
  }

  public ASTCDClass getAutomataClass() {
    return automataClass;
  }

  public Collection<ASTCDClass> getStateClasses() {
    return stateClasses;
  }
}
