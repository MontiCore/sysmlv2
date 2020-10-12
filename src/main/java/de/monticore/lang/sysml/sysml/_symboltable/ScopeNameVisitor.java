package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.lang.sysml.ad._ast.ASTActivity;
import de.monticore.lang.sysml.ad._ast.ASTActivityBodyStd;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTAssertConstraintUsage;
import de.monticore.lang.sysml.advanced.sysmldefinitions._ast.ASTDefinitionBodyStd;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLType;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmlassociations._ast.ASTAssociationBlock;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTActivityBody;
import de.monticore.lang.sysml.basics.sysmlcommonbasis._ast.ASTDefinitionBody;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageUnit;
import de.monticore.lang.sysml.basics.sysmlports._ast.ASTInterfaceDefinition;
import de.monticore.lang.sysml.basics.sysmlports._ast.ASTPortDefinitionStd;
import de.monticore.lang.sysml.basics.sysmlvaluetypes._ast.ASTValueTypeStd;
import de.monticore.lang.sysml.bdd._ast.ASTBlock;
import de.monticore.lang.sysml.parametricdiagram._ast.ASTIndividualDefinition;
import de.monticore.lang.sysml.requirementdiagram._ast.ASTRequirementDefinition;
import de.monticore.lang.sysml.requirementdiagram._ast.ASTSatisfyRequirementUsage;
import de.monticore.lang.sysml.smd._ast.ASTStateDefinition;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ScopeNameVisitor implements SysMLInheritanceVisitor {
  public void startTraversal(ASTUnit ast) {
    ast.accept(this);
  }

  @Override
  public void visit(ASTSysMLType node) {
    if (!node.getName().equals("NotNamed1232454123534j4jn43")) {
      String name = node.getName();
      if(node instanceof ASTBlock){
        ASTBlock block = (ASTBlock) node;
        addNameToDefinitionBody(block.getDefinitionBody(), name);
      }else if(node instanceof ASTPackage){
        ASTPackage astPackage = (ASTPackage) node;
        astPackage.getPackageBody().getSpannedScope().setName(name);
      } else if(node instanceof ASTActivity){
        ASTActivityBody body = ((ASTActivity) node).getActivityBody();
        if(body instanceof ASTActivityBodyStd){
          ASTActivityBodyStd bodyStd = (ASTActivityBodyStd) body;
          bodyStd.getSpannedScope().setName(name);
        }
      } else if (node instanceof ASTAssociationBlock){
        ASTAssociationBlock associationBlock = (ASTAssociationBlock) node;
        associationBlock.getAssociationBlockBody().getSpannedScope().setName(name);
      } else if(node instanceof ASTStateDefinition){
        ((ASTStateDefinition) node).getStateBody().getSpannedScope().setName(name);
      } else if (node instanceof ASTPortDefinitionStd){
        addNameToDefinitionBody(((ASTPortDefinitionStd)node).getDefinitionBody(), name);
      } else if (node instanceof ASTIndividualDefinition){
        addNameToDefinitionBody(((ASTIndividualDefinition)node).getDefinitionBody(), name);
      }else if (node instanceof ASTRequirementDefinition){
        ((ASTRequirementDefinition) node).getRequirementBody().getSpannedScope().setName(name);
      }else if (node instanceof ASTSatisfyRequirementUsage){
        ((ASTRequirementDefinition) node).getRequirementBody().getSpannedScope().setName(name);
      }else if (node instanceof ASTAssertConstraintUsage){
        ((ASTAssertConstraintUsage) node).getConstraintBody().getSpannedScope().setName(name);
      }else if (node instanceof ASTValueTypeStd){
        addNameToDefinitionBody(((ASTValueTypeStd) node).getDefinitionBody(),name);
      }else if (node instanceof ASTInterfaceDefinition){
        ((ASTInterfaceDefinition) node).getInterfaceBody().getSpannedScope().setName(name);
      }

    }
  }

  private void addNameToDefinitionBody(ASTDefinitionBody defBody, String name){
    if(defBody instanceof ASTDefinitionBodyStd){
      ASTDefinitionBodyStd body = (ASTDefinitionBodyStd) defBody;
      body.getSpannedScope().setName(name);
    }
  }


}
