<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateList", "automaton")}

${cd4c.method("public void compute()")}
//Parameter
    <#assign doActions = autHelper.getDoActionsOfElement(automaton)/>
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
    //binds
    <#assign bindList = actionsHelper.getBindList(state)>
    <#list bindList as bind>
        ${actionsHelper.mapBindEnd(bind.getSource())} = ${actionsHelper.mapBindEnd(bind.getTarget())};
    </#list>
    //Do Actions
    <#list doActions as doAction>
        <@handleAction actionsHelper.getActionFromDoAction(doAction)/>
    </#list>
    de.monticore.lang.sysmlv2.generator.log.Log.comment("Computing component " + this.getClass().getName() + "");
    // log state @ pre
    de.monticore.lang.sysmlv2.generator.log.Log.trace("State@pre = "+ ${autHelper.resolveCurrentStateName(automaton)});
    // transition from the current state
    switch (${autHelper.resolveCurrentStateName(automaton)}) {
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
    "State@post = "+ ${autHelper.resolveCurrentStateName(automaton)});
