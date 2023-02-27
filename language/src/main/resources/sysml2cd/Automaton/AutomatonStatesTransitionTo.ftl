<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state")}
${cd4c.method("protected void transitionTo${autHelper.resolveStateName(state)?cap_first}()")}
//do actions
    <#assign doActions = state.getDoActionList()/>
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
