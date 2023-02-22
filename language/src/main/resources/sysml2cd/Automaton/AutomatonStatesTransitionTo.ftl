<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state")}
${cd4c.method("protected void transitionTo${autHelper.resolveStateName(state)?cap_first}()")}

  <#assign substateList = autHelper.getSubStates(state)>

  de.monticore.lang.sysmlv2.generator.log.Log.comment("Computing sub automaton  ${state.getName()}");
  // log state @ pre
  de.monticore.lang.sysmlv2.generator.log.Log.trace("State@pre = "+ currentState_${ autHelper.resolveStateName(state)});
  // transition from the current state
  switch (currentState_${ autHelper.resolveStateName(state)}) {
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
  "State@post = "+ this.getCurrentState());
