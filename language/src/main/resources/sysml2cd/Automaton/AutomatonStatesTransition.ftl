<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton", "enumName")}
${cd4c.method("protected void transitionFrom${state.getName()?cap_first}()")}
  // input

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
  <#elseif autHelper.hasSuperState(automaton, state) && autHelper.isFinalState(automaton, state)>
    // transition from super state
    transitionFrom${autHelper.getSuperState(automaton, state).getName()}();
  </#if>
  <#if transitions?size != 0>}</#if>


<#macro printTransition transition state automaton output result>

    <#if autHelper.isPresentGuard(transition)>
      if(${autHelper.printExpression(transition.getGuard())}) {
    </#if>
  // exit state(s)
  this.exit(this.getCurrentState(), ${enumName}.${state.getName()});

  // output
    //TODO output
  // reaction
      //TODO add do actions
  // result
    //TODO set outputs
  // entry state(s)
      //TODO sub states in automaton
  this.currentState =  ${enumName}.${state.getName()};

  this.entry${transition.getTgt()?cap_first}();

</#macro>
