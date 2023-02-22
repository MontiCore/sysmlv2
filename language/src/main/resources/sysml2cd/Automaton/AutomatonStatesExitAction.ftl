<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton")}
${cd4c.method("protected void exit${autHelper.resolveStateName(state)?cap_first}()")}

<#if autHelper.hasExitAction(state)>
  // inputs
  //TODO inputs

  // outputs
  //TODO print outputs
  // entry action
      <#assign exitActions = state.getExitActionList()/>

  <#list exitActions>
      <#items as exitAction>
    ${exitAction.getAction()}
    </#items>
</#list>
  // result
    //TODO print results
</#if>
