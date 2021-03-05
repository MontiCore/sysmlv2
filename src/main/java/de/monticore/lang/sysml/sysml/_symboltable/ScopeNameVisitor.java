package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTAssertConstraintUsage;
import de.monticore.lang.sysml.basics.sysmlnamesbasis._ast.ASTSysMLType;
import de.monticore.lang.sysml.basics.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.common.sysmlassociations._ast.ASTAssociationBlock;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTActivityBody;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTDefinitionBody;
import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTDefinitionBodyStd;
import de.monticore.lang.sysml.common.sysmlports._ast.ASTInterfaceDefinition;
import de.monticore.lang.sysml.common.sysmlports._ast.ASTPortDefinitionStd;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTUsageStd;
import de.monticore.lang.sysml.common.sysmlvaluetypes._ast.ASTValueTypeStd;
import de.monticore.lang.sysml.sysml._visitor.SysMLInheritanceVisitor;
import de.monticore.lang.sysml.sysmlad._ast.ASTActivity;
import de.monticore.lang.sysml.sysmlad._ast.ASTActivityBodyStd;
import de.monticore.lang.sysml.sysmlbdd._ast.ASTBlock;
import de.monticore.lang.sysml.sysmlpd._ast.ASTIndividualDefinition;
import de.monticore.lang.sysml.sysmlrd._ast.ASTRequirementDefinition;
import de.monticore.lang.sysml.sysmlrd._ast.ASTSatisfyRequirementUsage;
import de.monticore.lang.sysml.sysmlstm._ast.ASTStateDefinition;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class ScopeNameVisitor implements SysMLInheritanceVisitor {
  public void startTraversal(ASTUnit ast) {
    ast.accept(this);
  }

  @Override
  public void visit(ASTUsageStd node) {
    if(node.getUsageDeclaration().isPresentSysMLNameAndTypePart()){
      addNameToDefinitionBody(node.getUsageCompletion().getDefinitionBody(),
          node.getUsageDeclaration().getSysMLNameAndTypePart().getName());
    }
  }

  @Override
  public void visit(ASTSysMLType node) {
    if (!node.getName().equals("")) {
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
