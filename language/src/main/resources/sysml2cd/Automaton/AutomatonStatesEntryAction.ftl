<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton")}
${cd4c.method("protected void entry${autHelper.resolveStateName(state)?cap_first}()")}

<#if autHelper.hasEntryAction(state)>
  // entry action
      <#assign entryActions = state.getEntryActionList()/>
  <#list entryActions as entryAction>
      <@handleAction actionsHelper.getActionFromEntryAction(entryAction)/>
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

