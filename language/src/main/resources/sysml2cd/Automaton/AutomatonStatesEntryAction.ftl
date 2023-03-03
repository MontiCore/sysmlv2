<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "parent")}
${cd4c.method("protected void entry${autHelper.resolveStateName(state)?cap_first}()")}

<#if autHelper.hasEntryAction(state)>
  // entry action
    <#assign entryActions = state.getEntryActionList()/>
    <#list entryActions as entryAction>
        <#assign subaction = actionsHelper.getActionFromEntryAction(entryAction)/>
        <#list actionsHelper.getParameters(subaction) as parameter>
            <#if compHelper.isObjectAttribute(parameter)>
                ${compHelper.getAttributeType(parameter)} ${subaction.getName()}_${parameter.getName()} = new ${compHelper.getAttributeType(parameter)}();
              this.${parameter.getName()}.setUp();
            <#else>
                ${compHelper.mapToWrapped(parameter)} ${subaction.getName()}_${parameter.getName()} = ${compHelper.mapToWrapped(parameter)}.valueOf(<#if parameter.isPresentExpression()>${actionsHelper.printExpression(parameter.getExpression())}<#else >0</#if>);
            </#if>
        </#list>
    </#list>


    <#list entryActions as entryAction>
        <@handleAction actionsHelper.getActionFromEntryAction(entryAction)/>
    </#list>
</#if>




<#macro handleAction action>
    <#assign actionUsage = actionsHelper.getActionUsage(action,state)/>
    <#if actionsHelper.isSendAction(actionUsage)>
      this.parentPart.get${actionUsage.getTarget()?cap_first}().setValue(${autHelper.printExpression(actionUsage.getPayload(), parent)});
    </#if>
    <#if actionsHelper.isAssignmentAction(actionUsage)>
        ${autHelper.renameAction(actionUsage, parent)} = ${autHelper.printExpression(actionUsage.getValueExpression(), parent)};
    </#if>
    <#if !actionsHelper.isSendAction(actionUsage) && !actionsHelper.isAssignmentAction(actionUsage)>
        ${actionUsage.getName()}(<#list  actionsHelper.getParametersWithActionPrefix(actionUsage) as param>${param}<#sep>, </#sep></#list>);
    </#if>
</#macro>

