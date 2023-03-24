<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("action","parameterList", "attributeList", "isAbstract")}
<#if isAbstract>
    <#assign abstract = "abstract "/>
<#else >
    <#assign abstract = ""/>
</#if>
<#assign  secondControlNode = actionsHelper.getSecondControlNode(action)>
${cd4c.method("public boolean returnPath_${secondControlNode.getName()}(${actionsHelper.parameterListForDecisionMethod(action, true) })")}
<#assign firstReturn = actionsHelper.getFirstReturnPathSuccessor(action)/>
      if (${actionsHelper.printExpression(firstReturn.getGuard())}){
        <@printAction firstReturn action/>
        <@printPath actionsHelper.getReturnPath(action) action/>
          return true;
      }
          return false;




<#macro printPath successionList action>
    <#list successionList as succession>
        <#if succession.isPresentGuard()>
          if (!(${autHelper.printExpression(succession.getGuard())})){
          throw new RuntimeException("Could not evaluate the guard \"${autHelper.printExpression(succession.getGuard())}\" in action \"${action.getName()}\" to true, but the execution has to terminate.");
          }
        </#if>
        <@printAction succession action/>
    </#list>
</#macro>

<#macro printAction succession action>
    <#if actionsHelper.isDoneOrControlNode(succession.getTgt(),succession)>
    <#else >
        <#assign resolvedTarget = actionsHelper.resolveAction(succession.getTgt(), succession)>
        <#if actionsHelper.isSendAction(resolvedTarget)>
          this.get${resolvedTarget.getTarget()?cap_first}().setValue(${actionsHelper.printExpression(resolvedTarget.getPayload())});
        <#else >
            <#if actionsHelper.isAssignmentAction(resolvedTarget)>
                ${actionsHelper.mapBindEnd(resolvedTarget.getTarget())} = ${actionsHelper.printExpression(resolvedTarget.getValueExpression())};
            <#else >
                ${succession.getTgt()}(<#list  actionsHelper.getParametersWithActionPrefix(resolvedTarget) as param>${param}<#sep>, </#sep></#list>);
            </#if>
        </#if>
    </#if>
</#macro>
