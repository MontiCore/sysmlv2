<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("action","parameterList", "attributeList", "isAbstract")}
<#if isAbstract>
  <#assign abstract = "abstract "/>
    <#else >
    <#assign abstract = ""/>
</#if>
${cd4c.method("${abstract}public void ${action.getName()}(${autHelper.getParametersOfActionAsString(parameterList) })")}
      //local variables
<#list attributeList as attribute>
    <#if compHelper.isObjectAttribute(attribute)>
        ${compHelper.getAttributeType(attribute)} ${attribute.getName()} = new ${compHelper.getAttributeType(attribute)}();
      this.${attribute.getName()}.setUp();
    <#else>
        ${compHelper.mapToWrapped(attribute)} ${attribute.getName()} = ${compHelper.mapToWrapped(attribute)}.valueOf(<#if attribute.isPresentExpression()>${autHelper.printExpression(attribute.getExpression())}<#else >0</#if>);
    </#if>
</#list>
      //Pointer to parameters of sub actions
<#list actionsHelper.getSubActions(action) as subaction>
    <#list actionsHelper.getParameters(subaction) as parameter>
        <#if compHelper.isObjectAttribute(parameter)>
            ${compHelper.getAttributeType(parameter)} ${subaction.getName()}_${parameter.getName()} = new ${compHelper.getAttributeType(parameter)}();
          this.${parameter.getName()}.setUp();
        <#else>
            ${compHelper.mapToWrapped(parameter)} ${subaction.getName()}_${parameter.getName()} = ${compHelper.mapToWrapped(parameter)}.valueOf(<#if parameter.isPresentExpression()>${autHelper.printExpression(parameter.getExpression())}<#else >0</#if>);
        </#if>
    </#list>
</#list>
      //control flow
<#if actionsHelper.hasActionDecideMerge(action)>
    <#assign  firstControlNode = actionsHelper.getFirstControlNode(action)>
    <#assign  secondControlNode = actionsHelper.getSecondControlNode(action)>
  //start
    <@printPath actionsHelper.getPathFromStart(action) action/>
    <#if actionsHelper.isMergeNode(firstControlNode)>
        do{
        <@printPath actionsHelper.getPathFromAction(actionsHelper.getDirectSuccessor(firstControlNode),actionsHelper.getSuccessions(action)) action/>
        }while(returnPath_${secondControlNode.getName()}(${actionsHelper.parameterListForDecisionMethod(action, false) }));
        <@printPath actionsHelper.getEndPath(action) action/>
    <#else >
        <#assign paths= actionsHelper.getDecisionPaths(firstControlNode,secondControlNode)>
        <@printDecision paths />
    </#if>
<#else >
    <#if actionsHelper.hasActionForkJoin(action)>
        <#assign  firstControlNode = actionsHelper.getFirstControlNode(action)>
        <#assign  secondControlNode = actionsHelper.getSecondControlNode(action)>
      //start
        <@printPath actionsHelper.getPathFromStart(action) action/>
        <#list actionsHelper.getDecisionPaths(firstControlNode,secondControlNode) as path>
          //path:
            <@printPath path action/>
        </#list>
        <@printPath actionsHelper.getEndPath(action) action/>
    <#else>
        <@printPath actionsHelper.getPathFromStart(action) action/>
    </#if>
</#if>






<#macro printPath successionList action>
    <#list successionList as succession>
        <#if succession.isPresentGuard()>
    if (!(${autHelper.printExpression(succession.getGuard())})){
        de.monticore.lang.sysmlv2.generator.log.Log.error("Could not evaluate the guard \"${autHelper.printExpression(succession.getGuard())}\" in action \"${action.getName()}\" to true, but the execution has to terminate.");
    }
        </#if>
      <@printAction succession/>
    </#list>
</#macro>


<#macro printAction succession>
    <#if actionsHelper.isDoneOrControlNode(succession.getTgt(),succession)>
    <#else >
        <#assign resolvedTarget = actionsHelper.resolveAction(succession.getTgt(), succession)>
        <#if actionsHelper.isSendAction(resolvedTarget)>
        this.get${resolvedTarget.getTarget()?cap_first}().setValue(${autHelper.printExpression(resolvedTarget.getPayload())});
        <#else >
            <#if actionsHelper.isAssignmentAction(resolvedTarget)>
        ${resolvedTarget.getTarget()} = ${autHelper.printExpression(resolvedTarget.getValueExpression())};
            <#else >
        ${succession.getTgt()}(<#list  actionsHelper.getParametersWithActionPrefix(resolvedTarget) as param>${param}<#sep>, </#sep></#list>);
            </#if>
        </#if>
    </#if>
</#macro>

<#macro printDecision paths>
  //path:
  if (${autHelper.printExpression(paths[0][0].getGuard())}){
<@printAction paths[0][0] />
<@printPath actionsHelper.dropFirstElement(paths[0]) action/>
  } else if(${autHelper.printExpression(paths[1][0].getGuard())}){
<@printAction paths[1][0] />
<@printPath actionsHelper.dropFirstElement(paths[1]) action/>
    }else{
        de.monticore.lang.sysmlv2.generator.log.Log.error("Could not evaluate the guard \"${autHelper.printExpression(paths[0][0].getGuard())}\" or guard \"${autHelper.printExpression(paths[1][0].getGuard())}\" at the \"decide\" in action \"${action.getName()}\" to true, but the execution has to terminate.");
  }
<@printPath actionsHelper.getEndPath(action) action/>
</#macro>
