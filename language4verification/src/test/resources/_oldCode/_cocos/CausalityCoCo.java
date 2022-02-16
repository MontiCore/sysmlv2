package schrott._cocos;

import de.monticore.lang.sysml.advanced.sysmlconstraints._ast.ASTConstraintBody;
import de.monticore.lang.sysml.basics.interfaces.sysmlpackagebasis._ast.ASTPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.interfaces.sysmlpackagebasis._cocos.SysMLPackageBasisASTPackagedDefinitionMemberCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTNestedUsageMember;
import de.monticore.lang.sysml.common.sysmlcommonbasis._ast.ASTDefinitionBody;
import de.monticore.lang.sysml.common.sysmldefinitions._ast.ASTDefinitionBodyStd;
import schrott._ast.ASTBlock;
import schrott._ast.ASTConstraintDefinition;
import schrott._ast.ASTPortDefinitionStd;
import schrott._ast.ASTRequirementDefinition;
import schrott._ast.ASTSysMLCausality;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CoCo stellt sicher, dass nur in BlockDefinitions Timing Keywords (implementieren ASTUsageMember) benutzt
 * werden können und, dass nur ein Timing in einer BlockDefinition spezifiziert werden kann
 */
public class CausalityCoCo implements SysMLPackageBasisASTPackagedDefinitionMemberCoCo {

  @Override
  public void check(ASTPackagedDefinitionMember node) {

    // Nur eine Timing Spezifikation in Blöcken
    if(node instanceof ASTBlock) {
      ASTDefinitionBody uselessDefBody = ((ASTBlock) node).getDefinitionBody();
      if(uselessDefBody instanceof ASTDefinitionBodyStd){
        ASTDefinitionBodyStd defBody = (ASTDefinitionBodyStd) uselessDefBody;
        if(numberOfTimingKeywords(defBody.getNestedUsageMemberList()) > 1){
          Log.error("Timing can only be specified once in a single BlockDefinition.", node.get_SourcePositionStart());
        }
      }
    }
    //keine Timing Spezifikation in allen anderen Definitionen (in state defs können von der Grammatik aus keine benutzt werden)
    else if(node instanceof ASTPortDefinitionStd){
      ASTDefinitionBody uselessDefBody = ((ASTPortDefinitionStd) node).getDefinitionBody();
      if(uselessDefBody instanceof ASTDefinitionBodyStd){
        ASTDefinitionBodyStd defBody = (ASTDefinitionBodyStd) uselessDefBody;
        if(numberOfTimingKeywords(defBody.getNestedUsageMemberList()) > 0){
          Log.error("Timing keywords are not allowed in PortDefinitions.", node.get_SourcePositionStart());
        }
      }
    }
    else if(node instanceof ASTConstraintDefinition){
      ASTConstraintBody constraintBody = ((ASTConstraintDefinition) node).getConstraintBody();
      if(numberOfTimingKeywords(constraintBody.getConstraintMembers().getNestedUsageMemberList()) > 0){
        Log.error("Timing keywords are not allowed in ConstraintDefinitions.", node.get_SourcePositionStart());
      }
    }
    else if(node instanceof ASTRequirementDefinition){
      if(numberOfTimingKeywords(((ASTRequirementDefinition) node).getRequirementBody()
          .getRequirementMembers().getNestedUsageMemberList()) > 0) {
        Log.error("Timing keywords are not allowed in RequirementDefinitions.", node.get_SourcePositionStart());
      }
    }
  }

  /**
   * Helper Funktion die nach ASTSysMLCausality in einer UsageMemberList sucht
   * @param usageMemberList
   * @return Anzahl von ASTSysMLCausality
   */
  private int numberOfTimingKeywords(List<ASTNestedUsageMember> usageMemberList) {
    return usageMemberList.stream().filter(x -> x instanceof ASTSysMLCausality).collect(Collectors.toList()).size();
  }
}
