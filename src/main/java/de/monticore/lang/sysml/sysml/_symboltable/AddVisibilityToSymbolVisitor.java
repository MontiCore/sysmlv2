package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.lang.sysml.basics.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.sysmlpackagebasis._ast.ASTPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.sysmlvisibilitybasis._ast.ASTPackageElementVisibilityIndicator;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmlimportsandpackages._ast.ASTPackageMember;
import de.monticore.lang.sysml.basics.sysmlvisibility._ast.ASTPackageElementVisibilityIndicatorStd;
import de.monticore.lang.sysml.common.sysmlassociations._ast.ASTAssociationBlock;
import de.monticore.lang.sysml.common.sysmlports._ast.ASTInterfaceDefinition;
import de.monticore.lang.sysml.common.sysmlports._ast.ASTPortDefinitionStd;
import de.monticore.lang.sysml.common.sysmlvaluetypes._ast.ASTValueTypeStd;
import de.monticore.lang.sysml.sysml._visitor.SysMLVisitor;
import de.monticore.lang.sysml.sysmlad._ast.ASTActivity;
import de.monticore.lang.sysml.sysmlbdd._ast.ASTBlock;
import de.monticore.lang.sysml.sysmlpd._ast.ASTIndividualDefinition;
import de.monticore.lang.sysml.sysmlrd._ast.ASTRequirementDefinition;
import de.monticore.lang.sysml.sysmlstm._ast.ASTStateDefinition;
import de.monticore.symboltable.modifiers.AccessModifier;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AddVisibilityToSymbolVisitor implements SysMLVisitor {

  public void startTraversal(ASTUnit ast) {
    ast.accept(this);
    }

  @Override
  public void visit (ASTPackageMember packageMember)  {
    if(packageMember.getPackageMemberPrefix().isPresentVisibility() && packageMember.isPresentPackagedDefinitionMember()) {
      ASTPackageElementVisibilityIndicator visibilityInterface = packageMember.getPackageMemberPrefix().getVisibility();
      if (visibilityInterface instanceof ASTPackageElementVisibilityIndicatorStd) {
        ASTPackageElementVisibilityIndicatorStd visibility = (ASTPackageElementVisibilityIndicatorStd) visibilityInterface;

        int vis = visibility.getVis().getIntValue();
        setVisibilityToPackagedDefinitionMember(packageMember.getPackagedDefinitionMember(), vis);
      }
    }
  }

  /*@Override
  public  void visit (ASTImportUnitStd importUnitStd){
      // ImportUnit does not create a symbol for itself. The Symbol adding is done while importing, which has
      // to take the visibility into account.
  }*/

  private void setVisibilityToPackagedDefinitionMember(ASTPackagedDefinitionMember member, int vis){
    AccessModifier accessModifier;
    if(vis == 2){  // => is private
      accessModifier = new SysMLAccessModifierPrivate();
    }else{
      accessModifier = new SysMLAccessModifierPublic();
    }

    //Shows how to set visibility.
    if(member instanceof ASTAliasPackagedDefinitionMember){
      ASTAliasPackagedDefinitionMember importOrAlias = (ASTAliasPackagedDefinitionMember) member;
      setVisibilityAccessModifier(importOrAlias.getSymbol(), accessModifier);
    }
    else if(member instanceof ASTBlock){
      setVisibilityAccessModifier((((ASTBlock) member).getSymbol()), accessModifier);
    }else if(member instanceof ASTPackage){
      setVisibilityAccessModifier((((ASTPackage) member).getSymbol()), accessModifier);
    } else if(member instanceof ASTActivity){
      setVisibilityAccessModifier((((ASTActivity) member).getSymbol()), accessModifier);
    } else if (member instanceof ASTAssociationBlock){
      setVisibilityAccessModifier((((ASTAssociationBlock) member).getSymbol()), accessModifier);
    } else if(member instanceof ASTStateDefinition){
      setVisibilityAccessModifier((((ASTStateDefinition) member).getSymbol()), accessModifier);
    } else if (member instanceof ASTPortDefinitionStd){
      setVisibilityAccessModifier((((ASTPortDefinitionStd) member).getSymbol()), accessModifier);
    } else if (member instanceof ASTIndividualDefinition){
      setVisibilityAccessModifier((((ASTIndividualDefinition) member).getSymbol()), accessModifier);
    }else if (member instanceof ASTRequirementDefinition){
      setVisibilityAccessModifier((((ASTRequirementDefinition) member).getSymbol()), accessModifier);
    }else if (member instanceof ASTValueTypeStd){
      setVisibilityAccessModifier((((ASTValueTypeStd) member).getSymbol()), accessModifier);
    }else if (member instanceof ASTInterfaceDefinition){
      setVisibilityAccessModifier((((ASTInterfaceDefinition) member).getSymbol()), accessModifier);
    }
  }
  private void setVisibilityAccessModifier(SysMLTypeSymbol symbol, AccessModifier accessModifier){
    symbol.setAccessModifier(accessModifier);
  }
}
