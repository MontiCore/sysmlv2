package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlnames._ast.ASTSimpleName;
import de.monticore.lang.sysml.sysml._visitor.SysMLVisitor;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;
import de.monticore.symboltable.IScope;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AddImportToScopeVisitor implements SysMLInheritanceVisitor {
  public void startTraversal(ASTUnit ast) {
    this.handle((ASTPackageUnit) ast);//TODO all possible casts
  }

  @Override
  public void visit(ASTImportUnitStd importUnit){
    //resolving importUnit
    IScope searchingScope = importUnit.getEnclosingScope().getEnclosingScope();
    IScope scope = importUnit.getEnclosingScope();
    // First scope is package, but we want to have the package above.
    // TODO importUnit.getQualifiedName().ge
    List<String> names = new ArrayList<>();
    /*if(importUnit.getQualifiedName() instanceof ASTSimpleName){
      names.add((ASTSimpleName)importUnit.getQualifiedName()).getSysMLName();
    } else if(importUnit.getQualifiedName() instanceof ASTSimpleName)


      */
    if(importUnit.isStar()){
      //Import all TODO
    }

  }
}
