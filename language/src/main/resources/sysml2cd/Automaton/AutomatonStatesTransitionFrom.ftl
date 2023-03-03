<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "inputPorts", "outputPorts", "parent")}
${cd4c.method("protected void transitionFrom${autHelper.resolveStateName(state)?cap_first}()")}
    <#if !state.getIsAutomaton()>
        <#assign doActions = state.getDoActionList()/>
        <#list doActions as doAction>
            <#assign subaction = actionsHelper.getActionFromDoAction(doAction)/>
            <#list actionsHelper.getParameters(subaction) as parameter>
                <#if compHelper.isObjectAttribute(parameter)>
                    ${compHelper.getAttributeType(parameter)} ${subaction.getName()}_${parameter.getName()} = new ${compHelper.getAttributeType(parameter)}();
                  this.${parameter.getName()}.setUp();
                <#else>
                    ${compHelper.mapToWrapped(parameter)} ${subaction.getName()}_${parameter.getName()} = ${compHelper.mapToWrapped(parameter)}.valueOf(<#if parameter.isPresentExpression()>${actionsHelper.printExpression(parameter.getExpression())}<#else >0</#if>);
                </#if>
            </#list>
        </#list>

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
      if(${autHelper.printExpression(transition.getGuard(), parent)}) {
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
  this.exit${(autHelper.resolveTransitionName(automaton,transition.getSrc())?cap_first)}();
  this.entry${(autHelper.resolveTransitionName(automaton,transition.getTgt())?cap_first)}();
    <#if transition.isPresentGuard()>
      }
    </#if>
</#macro>


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
