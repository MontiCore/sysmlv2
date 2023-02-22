<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateList")}

${cd4c.method("public void compute()")}

    de.monticore.lang.sysmlv2.generator.log.Log.comment("Computing component " + this.getClass().getName() + "");
    // log state @ pre
    de.monticore.lang.sysmlv2.generator.log.Log.trace("State@pre = "+ this.getCurrentState());
    // transition from the current state
    switch (currentState) {
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
    "State@post = "+ this.getCurrentState());
