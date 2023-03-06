<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "parent")}
${cd4c.method("protected void transitionTo${autHelper.resolveStateName(state)?cap_first}()")}
//do actions
    <#assign doActions = autHelper.getDoActionsOfElement(state)/>
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
    <#assign bindList = actionsHelper.getBindList(state)>
    <#list bindList as bind>
        <#if actionsHelper.isInParameters(actionsParameters,bind.getSource(),bind.getTarget())>
        ${actionsHelper.mapBindEnd(bind.getSource())} = ${actionsHelper.mapBindEnd(bind.getTarget())};
        </#if>
    </#list>
    <#list doActions as doAction>
        <@handleAction actionsHelper.getActionFromDoAction(doAction)/>
    </#list>
  <#assign substateList = autHelper.getSubStates(state)>
//calculate this iteration
  de.monticore.lang.sysmlv2.generator.log.Log.comment("Computing sub automaton  ${state.getName()}");
  // log state @ pre
  de.monticore.lang.sysmlv2.generator.log.Log.trace("State@pre = "+ ${autHelper.resolveCurrentStateName(state)});
  // transition from the current state
  switch (${autHelper.resolveCurrentStateName(state)}) {
    <#list substateList as substate>
      case ${substate.getName()}:
      <#if substate.getIsAutomaton()>
      transitionTo${autHelper.resolveTransitionName(state,substate.getName())?cap_first}();
          <#else >
      transitionFrom${autHelper.resolveTransitionName(state,substate.getName())?cap_first}();
      </#if>
      break;
    </#list>
  }

  // log state @ post
  de.monticore.lang.sysmlv2.generator.log.Log.trace(
  "State@post = "+ ${autHelper.resolveCurrentStateName(state)});

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
