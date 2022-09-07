package schrott._cocos;

import afu.org.checkerframework.checker.oigj.qual.O;
import de.monticore.lang.sysml.ad._ast.ASTParameterListStd;
import de.monticore.lang.sysml.ad._ast.ASTParameterMemberStd;
import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTConstraintDefDeclaration;
import de.monticore.lang.sysml.advanced.sysmlconstraints._cocos.SysMLConstraintsASTConstraintDefDeclarationCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlpackagebasis._ast.ASTPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.interfaces.sysmlpackagebasis._cocos.SysMLPackageBasisASTPackagedDefinitionMemberCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTFeatureTyping;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTParameterList;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTParameterMember;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTParameterStd;
import de.monticore.lang.sysml.common.sysmlusages._ast.ASTParameterTypePart;
import de.monticore.lang.sysml.requirementdiagram._ast.ASTRequirementDefinition;
import de.monticore.lang.sysml.sysml4verification.SysML4VerificationMill;
import schrott._ast.ASTConstraintDefinition;
import schrott._ast.ASTFeatureTypingVerification;
import schrott._ast.ASTStateDefinition;
import schrott._ast.ASTSuperclassingList;
import schrott._symboltable.ConstraintDefinitionSymbol;
import schrott._symboltable.RequirementDefinitionSymbol;
import schrott._symboltable.StateDefinitionSymbol;
import de.monticore.lang.sysml.sysml4verification._visitor.SysML4VerificationTraverser;
import de.monticore.lang.sysml.sysml4verification._visitor.SysML4VerificationVisitor2;
import schrott.types.check.DeriveSymTypeOfSysMLExpressionDelegator;
import schrott.types.check.SynthesizeTypeSysML4VerificationDelegator;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * This CoCo ensures that parameters used in a refinement def have the same type as the ones of the refined def.
 */
public class RefinementParameter implements SysMLPackageBasisASTPackagedDefinitionMemberCoCo {

  private enum DefinitionKind {
    STATE_DEF,
    CONSTRAINT_DEF,
    REQUIREMENT_DEF,
    NONE
  }

  private DefinitionKind defKind = DefinitionKind.NONE;

  @Override
  public void check(ASTPackagedDefinitionMember node){
    SysML4VerificationTraverser traverser = SysML4VerificationMill.traverser();
    List<ASTFeatureTyping> parameterTypes;

    if(node instanceof ASTConstraintDefinition) {
      defKind = DefinitionKind.CONSTRAINT_DEF;
      ASTConstraintDefinition constraintDefinition = ((ASTConstraintDefinition) node);
      // Get all parameter types that are being used
      parameterTypes = getParameterTypesFromParameterList(constraintDefinition.getConstraintDefDeclaration().getParameterList());
      traverser.add4SysML4Verification(new SuperClassingListVisitor(parameterTypes));
      constraintDefinition.getConstraintDefDeclaration().getSuperclassingList().accept(traverser);
    } else if (node instanceof ASTStateDefinition) {
      defKind = DefinitionKind.STATE_DEF;
      ASTStateDefinition stateDefinition = ((ASTStateDefinition) node);
      // Get all parameter types that are being used
      parameterTypes = getParameterTypesFromParameterList(stateDefinition.getStateDefDeclaration().getParameterList());
      traverser.add4SysML4Verification(new SuperClassingListVisitor(parameterTypes));
      stateDefinition.getStateDefDeclaration().getSuperclassingList().accept(traverser);
    } else if (node instanceof ASTRequirementDefinition) {
      defKind = DefinitionKind.REQUIREMENT_DEF;
      ASTRequirementDefinition requirementDefinition = ((ASTRequirementDefinition) node);
      parameterTypes = getParameterTypesFromParameterMemberList(requirementDefinition.getRequirementDefDeclaration()
        .getRequirementDefParameterList().getParameterMemberList());
      traverser.add4SysML4Verification(new SuperClassingListVisitor(parameterTypes));
      requirementDefinition.getRequirementDefDeclaration().getSuperclassingList().accept(traverser);
    }
  }

  /**
   * Unwraps parameter list for ConstraintDefinition and StateDefinition
   * @param params list of parameters in a ASTParameterList
   * @return list of the parameter types used
   */
  private List<ASTFeatureTyping> getParameterTypesFromParameterList(ASTParameterList params) {

    // This >if< is probably always true and we just need it because the SysMLOfficial grammar is bad
    if(params instanceof ASTParameterListStd) {
      return getParameterTypesFromParameterMemberList(((ASTParameterListStd) params).getParameterMemberList());
    }
    return new ArrayList<>();
  }

  /**
   * @param members list of ASTParameterMembers
   * @return list of the parameter types used
   */
  private List<ASTFeatureTyping> getParameterTypesFromParameterMemberList(List<ASTParameterMember> members) {
    List<ASTFeatureTyping> types = new ArrayList<>();
    for(ASTParameterMember member : members) {
      // >if< probably always true again, see comment above
      if(member instanceof ASTParameterMemberStd) {
        // See comment above
        if(((ASTParameterMemberStd) member).getParameter() instanceof ASTParameterStd) {
          if(((ASTParameterStd) ((ASTParameterMemberStd) member).getParameter()).isPresentParameterTypePart()) {
            ASTParameterTypePart paramType = ((ASTParameterStd) ((ASTParameterMemberStd) member).getParameter()).getParameterTypePart();
            if(paramType.getFeatureTyping() instanceof ASTFeatureTypingVerification) {
              types.add(paramType.getFeatureTyping());
            }
          }
        }
      }
    }
    return types;
  }

  /**
   * Visitor handles resolving of all refined definitions and then checks for exact type equality.
   */
  private class SuperClassingListVisitor implements SysML4VerificationVisitor2 {

    // Types of the refinement definition
    List<ASTFeatureTyping> refinementDefParameters;

    private SuperClassingListVisitor(List<ASTFeatureTyping> parameterTypes){
      this.refinementDefParameters = parameterTypes;
    }

    /**
     * Checks if the types of a refinement component are exactly equal to the types of the general components.
     * @param node includes references to all refined components of a definition
     */
    @Override
    public void visit(ASTSuperclassingList node) {

      for (ASTQualifiedName refine : node.getRefinesList()) {
        Optional<SysMLTypeSymbol> sysMLTypeSymbolOpt = node.getEnclosingScope().resolveSysMLType(refine.getFullQualifiedName());
        if(!sysMLTypeSymbolOpt.isPresent()) {
          Log.error("Component which shall be refined must exist!", node.get_SourcePositionStart());
        }

        // Get the parameters of the component which is being refined, but only if it is of the same definition
        List<ASTFeatureTyping> refinedDefParameters = new ArrayList<>();
        if(sysMLTypeSymbolOpt.get() instanceof ConstraintDefinitionSymbol && defKind == DefinitionKind.CONSTRAINT_DEF) {
          refinedDefParameters = getParameterTypesFromParameterList(
              ((ConstraintDefinitionSymbol) sysMLTypeSymbolOpt.get()).getAstNode()
                  .getConstraintDefDeclaration().getParameterList());
        } else if(sysMLTypeSymbolOpt.get() instanceof StateDefinitionSymbol && defKind == DefinitionKind.STATE_DEF) {
          refinedDefParameters = getParameterTypesFromParameterList(
              ((StateDefinitionSymbol) sysMLTypeSymbolOpt.get()).getAstNode()
                  .getStateDefDeclaration().getParameterList());
        } else if (sysMLTypeSymbolOpt.get() instanceof RequirementDefinitionSymbol && defKind == DefinitionKind.REQUIREMENT_DEF){
          refinedDefParameters = getParameterTypesFromParameterMemberList(
              ((RequirementDefinitionSymbol)sysMLTypeSymbolOpt.get()).getAstNode()
                  .getRequirementDefDeclaration().getRequirementDefParameterList().getParameterMemberList());
        } else {
          Log.error("A definition can only refine the same definition kind!", node.get_SourcePositionStart());
        }

        // Different amount of parameters excludes type equality
        if(refinedDefParameters.size() != refinementDefParameters.size()) {
          Log.error("The refinement definition does not have the same number of parameters as the general definition.",
              node.get_SourcePositionStart());
        } else {
          Iterator<ASTFeatureTyping> refinementDefParametersIterator = refinementDefParameters.iterator();
          Iterator<ASTFeatureTyping> refinedDefParametersIterator = refinedDefParameters.iterator();
          while(refinementDefParametersIterator.hasNext()) {
            // Check if types are equal TODO assume for now that we have MCType
            TypeCheck typeCheck = new TypeCheck(new SynthesizeTypeSysML4VerificationDelegator(),
                new DeriveSymTypeOfSysMLExpressionDelegator());

            SymTypeExpression left = typeCheck.symTypeFromAST(
                ((ASTFeatureTypingVerification) refinementDefParametersIterator.next()).getMCType());
            SymTypeExpression right = typeCheck.symTypeFromAST(
                ((ASTFeatureTypingVerification) refinedDefParametersIterator.next()).getMCType());
            if(!TypeCheck.compatible(right, left)) {
              Log.error("The parameter types of the refinement definition do not match with the general definition.",
                  node.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
}
