<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateList")}

${cd4c.method("public void compute()")}

montiarc.rte.log.Log.comment("Computing component " + this.getInstanceName() + "");
// log state @ pre
montiarc.rte.log.Log.trace("State@pre = "+ this.getCurrentStateName()());
// transition from the current state
switch (currentStateName}) {
<#list stateList as state>
  case ${state.getName()}:
  transitionFrom${state.getName()?cap_first}();
  break;
</#list>
}

// log state @ post
montiarc.rte.log.Log.trace(
"State@post = "+ this.getCurrentStateName()());
