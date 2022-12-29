/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.types;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLQualifiedName;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisHandler;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisTraverser;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.prettyprint.IndentPrinter;

import java.util.stream.Collectors;

/**
 * Printet den Teil der SysMLBasis, die in Types involviert ist.
 */
public class SysMLBasisTypesPrettyPrinter implements SysMLBasisVisitor2, SysMLBasisHandler {

  // ########################### BOILER PLATE ####################################
  protected SysMLBasisTraverser traverser;

  protected IndentPrinter printer;

  public SysMLBasisTypesPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public SysMLBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SysMLBasisTraverser traverser) {
    this.traverser = traverser;
  }
  // #############################################################################

  public void visit(ASTSysMLQualifiedName node) {
    printer.print(node.streamParts().collect(Collectors.joining(".")));
  }

}
