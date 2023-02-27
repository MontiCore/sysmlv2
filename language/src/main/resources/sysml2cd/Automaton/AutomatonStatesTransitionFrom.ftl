<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "inputPorts", "outputPorts")}
${cd4c.method("protected void transitionFrom${autHelper.resolveStateName(state)?cap_first}()")}
    <#if !state.getIsAutomaton()>
        <#assign doActions = state.getDoActionList()/>
        <#list doActions as doAction>
            <@handleAction actionsHelper.getActionFromDoAction(doAction)/>
        </#list>
    </#if>

  // input
    <#list inputPorts as port>
      ${compHelper.getValueTypeOfPort(port)} ${port.getName()}_value = this.parentPart.get${port.getName()?cap_first}().getValue();
    </#list>
  <#assign transitions = autHelper.getAllTransitionsWithGuardFrom(automaton, state)/>

  <#list transitions>
    <#items as transition>
        <@printTransition transition state automaton state state/>
    <#sep> else </#sep>
    </#items>
    else {
  </#list>
  <#if autHelper.hasTransitionWithoutGuardFrom(automaton, state)>
      <#assign transition = autHelper.getFirstTransitionWithoutGuardFrom(automaton, state)>
      <@printTransition transition state automaton state state/>
  <#elseif autHelper.hasSuperState(automaton) && autHelper.isFinalState(state)>
    // transition from super state
    transitionFrom${autHelper.resolveStateName(automaton)?cap_first}();
  </#if>
  <#if transitions?size != 0>}</#if>
    <#list outputPorts as port>
      this.getParentPart().get${port.getName()?cap_first}().sync();
    </#list>

<#macro printTransition transition state automaton output result>

    <#if transition.isPresentGuard()>
      if(${autHelper.printExpression(transition.getGuard())}) {
    </#if>
  // output
    //TODO output
  // reaction
      //TODO add do actions
    <#if transition.isPresentDoAction()>
      <@handleAction actionsHelper.getActionFromDoAction(transition.getDoAction())/>
    </#if>
  // result
    //TODO set outputs
  // entry state(s)

  this.${autHelper.resolveCurrentStateName(automaton)} =  ${autHelper.resolveEnumName(automaton)}.${transition.getTgt()};
  <#if autHelper.isAutomaton(transition.getTgt(),state)>
    this.${autHelper.resolveCurrentStateName(autHelper.resolveStateUsage(transition.getTgt(),state))} =  ${autHelper.resolveEnumName(autHelper.resolveStateUsage(transition.getTgt(),state))}.start;
  </#if>
  this.entry${(autHelper.resolveTransitionName(automaton,transition.getTgt())?cap_first)}();
    <#if transition.isPresentGuard()>
      }
    </#if>
</#macro>


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
