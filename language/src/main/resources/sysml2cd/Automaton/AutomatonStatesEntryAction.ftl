<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "parent")}
${cd4c.method("protected void entry${autHelper.resolveStateName(state)?cap_first}()")}


  //entry action
    <#assign entryActions = autHelper.getEntryActionsOfElement(state)/>
    <#assign actionsParameters = []/>
    <#list entryActions as entryAction>
        <#assign subaction = actionsHelper.getActionFromEntryAction(entryAction)/>
        <#assign actionsParameters = actionsParameters + actionsHelper.getParameters(subaction)/>
        <#list actionsHelper.getParameters(subaction) as parameter>
            <#if compHelper.isObjectAttribute(parameter)>
                ${compHelper.getAttributeType(parameter)} ${subaction.getName()}_${parameter.getName()} = new ${compHelper.getAttributeType(parameter)}();
              this.${parameter.getName()}.setUp();
            <#else>
                ${compHelper.mapToWrapped(parameter)} ${subaction.getName()}_${parameter.getName()} = ${compHelper.mapToWrapped(parameter)}.valueOf(<#if parameter.isPresentExpression()>${actionsHelper.printExpression(parameter.getExpression())}<#else >0</#if>);
            </#if>
        </#list>
    </#list>
    //binds
    <#assign bindList = actionsHelper.getBindList(state)>
    <#list bindList as bind>
        <#if actionsHelper.isInParameters(actionsParameters,bind.getSource(),bind.getTarget())>
        ${actionsHelper.mapBindEnd(bind.getSource())} = ${actionsHelper.mapBindEnd(bind.getTarget())};
        </#if>
    </#list>
    <#list entryActions as entryAction>
        <@handleAction actionsHelper.getActionFromEntryAction(entryAction)/>
    </#list>


<#macro handleAction action>
    <#assign actionUsage = actionsHelper.getActionUsage(action,state)/>
    <#if actionsHelper.isSendAction(actionUsage)>
      this.parentPart.get${actionUsage.getTarget()?cap_first}().setValue(${autHelper.printExpression(actionUsage.getPayload(), parent)});
    </#if>
    <#if actionsHelper.isAssignmentAction(actionUsage)>
        ${autHelper.renameAction(actionUsage, parent)} = ${autHelper.printExpression(actionUsage.getValueExpression(), parent)};
    </#if>
    <#if !actionsHelper.isSendAction(actionUsage) && !actionsHelper.isAssignmentAction(actionUsage)>
      this.getParentPart().${actionUsage.getName()}(<#list  actionsHelper.getParametersWithActionPrefix(actionUsage) as param>${param}<#sep>, </#sep></#list>);
    </#if>
</#macro>

