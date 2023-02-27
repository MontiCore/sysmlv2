<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton")}
${cd4c.method("protected void exit${autHelper.resolveStateName(state)?cap_first}()")}

<#if autHelper.hasExitAction(state)>
  // exit action
    <#assign exitActions = state.getExitActionList()/>
    <#list exitActions as exitAction>
        <@handleAction actionsHelper.getActionFromExitAction(exitAction)/>
    </#list>
</#if>


<#macro handleAction action>
    <#if actionsHelper.isSendAction(action)>
      this.parentPart.get${action.getTarget()?cap_first}().setValue(${autHelper.printExpression(action.getPayload())});
    </#if>
    <#if actionsHelper.isAssignmentAction(action)>
        ${action.getTarget()} = ${autHelper.printExpression(action.getValueExpression())};
    </#if>
    <#if !actionsHelper.isSendAction(action) && !actionsHelper.isAssignmentAction(action)>
        ${action.getName()}();
    </#if>
</#macro>

