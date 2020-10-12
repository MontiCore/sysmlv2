package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.lang.sysml.advanced.sysmldefinitions._ast.ASTDefinitionBodyStd;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLType;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTDefinitionBody;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;
import groovyjarjarantlr.collections.AST;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ScopeNameVisitor implements SysMLInheritanceVisitor {
  public void startTraversal(ASTUnit ast) {
    this.handle((ASTPackageUnit) ast);//TODO all possible casts
  }

  @Override
  public void visit(ASTSysMLType node) {
    if (!node.getName().equals("NotNamed1232454123534j4jn43")) {
      String name = node.getName();
      if(node instanceof ASTBlock){
        ASTBlock block = (ASTBlock) node;
        if(block.getDefinitionBody() instanceof ASTDefinitionBodyStd){
          ASTDefinitionBodyStd body = (ASTDefinitionBodyStd) block.getDefinitionBody();
          body.getSpannedScope().setName(name);
        }
      }else if(node instanceof ASTPackage){
        ASTPackage astPackage = (ASTPackage) node;
        astPackage.getPackageBody().getSpannedScope().setName(name);
      }
      //TODO for all Types which could have a scope

    }


  }


}
