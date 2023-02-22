<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "inputPorts", "outputPorts")}
${cd4c.method("protected void transitionFrom${autHelper.resolveStateName(state)?cap_first}()")}

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
  // exit state(s)
  this.exit(this.${autHelper.resolveCurrentStateName(state)}, ${autHelper.resolveEnumName(automaton)}.${autHelper.resolveStateName(state)});

  // output
    //TODO output
  // reaction
      //TODO add do actions
    <#if transition.isPresentDoAction()>
        ${tc.includeArgs("sysml2cd.actions.ActionUsage.ftl",transition.getDoAction())
        }
    </#if>
  // result
    //TODO set outputs
  // entry state(s)
      //TODO sub states in automaton

  this.${autHelper.resolveCurrentStateName(automaton)} =  ${autHelper.resolveEnumName(automaton)}.${autHelper.resolveTransitionName(state,transition.getTgt())};
  <#if autHelper.isAutomaton(transition.getTgt(),state)>
    this.${autHelper.resolveCurrentStateName(autHelper.resolveStateUsage(transition.getTgt(),state))} =  ${autHelper.resolveEnumName(autHelper.resolveStateUsage(transition.getTgt(),state))}.start;
  </#if>

  this.entry${autHelper.resolveTransitionName(state,transition.getTgt()?cap_first)}();
    <#if transition.isPresentGuard()>
      }
    </#if>
</#macro>
