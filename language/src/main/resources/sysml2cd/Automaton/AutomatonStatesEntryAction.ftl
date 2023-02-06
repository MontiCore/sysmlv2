<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("state", "automaton")}
${cd4c.method("protected void entry${state.getName()?cap_first}()")}

<#if autHelper.hasEntryAction(state)>
  // inputs
  //TODO inputs

  // outputs
  //TODO print outputs
  // entry action
      <#assign entryActions = state.getEntryActionList()/>

  <#list entryActions>
      <#items as entryAction>
    ${entryAction.getAction()}
    </#items>
</#list>
  // result
    //TODO print results
</#if>
