<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateList", "automaton", "parentPart")}

${cd4c.method("public void compute()")}
    //Parameter
    <#assign doActions = autHelper.getDoActionsOfElement(automaton)/>
    <#assign actionsParameters = []/>
    <#list doActions as doAction>
        <#assign subaction = actionsHelper.getActionFromDoAction(doAction)/>
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
    <#assign bindList = actionsHelper.getBindList(automaton)>
    <#list bindList as bind>
        <#if actionsHelper.isInParameters(actionsParameters,bind.getSource(),bind.getTarget())>
    ${actionsHelper.mapBindEnd(bind.getSource())} = ${actionsHelper.mapBindEnd(bind.getTarget())};
        <#else>

    ${autHelper.printExpression(autHelper.mapQualifiedName(bind.getSource(), bind), parentPart)} = ${autHelper.printExpression(autHelper.mapQualifiedName(bind.getTarget(), bind),parentPart)};
        </#if>
    </#list>
    //Do Actions
    <#list doActions as doAction>
        <@handleAction actionsHelper.getActionFromDoAction(doAction)/>
    </#list>
    de.monticore.lang.sysmlv2.generator.log.Log.comment("Computing component " + this.getClass().getName() + "");
    // log state @ pre
    de.monticore.lang.sysmlv2.generator.log.Log.trace("State@pre = "+ ${autHelper.resolveCurrentStateName(automaton)});
    // transition from the current state
    switch (${autHelper.resolveCurrentStateName(automaton)}) {
    <#list stateList as state>
      case ${state.getName()}:
     <#if state.getIsAutomaton()>
      transitionTo${state.getName()?cap_first}();
     <#else >
      transitionFrom${state.getName()?cap_first}();
     </#if>
      break;
    </#list>
    }

    // log state @ post
    de.monticore.lang.sysmlv2.generator.log.Log.trace(
    "State@post = "+ ${autHelper.resolveCurrentStateName(automaton)});
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
