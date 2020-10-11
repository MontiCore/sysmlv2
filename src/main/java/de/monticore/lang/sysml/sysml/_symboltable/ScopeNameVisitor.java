package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;
import de.monticore.lang.sysml.sysml._visitor.SysMLParentAwareVisitor;
import de.monticore.lang.sysml.sysml._visitor.SysMLVisitor;
import de.monticore.symboltable.IScope;
import groovyjarjarantlr.collections.AST;

import java.util.Stack;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ScopeNameVisitor implements SysMLVisitor {
 Stack<SysMLTypeSymbol> stack = new Stack<>();


  public void startTraversal(ASTUnit ast) {
    this.handle((ASTPackageUnit) ast);//TODO all possible casts
  }
  public void startScopeTraversal(SysMLArtifactScope artifactScope){
    this.handle(artifactScope);
  }

  @Override
  public void visit(SysMLScope scope) { //TODO
    System.out.println("Visiting scope");
    if(!this.stack.empty()){
      System.out.println("Adding name to current scope " + stack.peek().getName());
      scope.setName(this.stack.peek().getName());
    }
  }

  @Override
  public void visit(SysMLTypeSymbol node) {
    if (!node.getName().equals("NotNamed1232454123534j4jn43")) {
      //TODO remove this if the visitor is correctly set up.
      System.out.println("Stacking name " + node.getName());
      this.stack.push(node);
    }
  }

  @Override
  public void endVisit(SysMLTypeSymbol node) {
    if (!node.getName().equals("NotNamed1232454123534j4jn43")) {
      //TODO remove this if the visitor is correctly set up.
      System.out.println("Pop name " + node.getName());
      this.stack.pop();
    }

  }


}
