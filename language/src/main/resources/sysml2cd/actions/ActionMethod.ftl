<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("action","parameterList", "attributeList")}
${cd4c.method("public void ${action.getName()}(${autHelper.getParametersOfActionAsString(parameterList) })")}
      //local variables
<#assign openBracketsCount = 0>
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
    <#else >
        <#list actionsHelper.getDecisionPaths(firstControlNode,secondControlNode) as path>
          //path:
            <@printPath path action/>
        </#list>
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
          if (${autHelper.printExpression(succession.getGuard())}){
            <#assign openBracketsCount = openBracketsCount + 1>
        </#if>
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
    </#list>
    <#if openBracketsCount gt 0>
        <#list 0..openBracketsCount-1 as i>
          }
        </#list>
        <#assign openBracketsCount = 0>
    </#if>
</#macro>


<#macro printAction action>

</#macro>

