<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("action","parameterList", "attributeList")}


${cd4c.method("public void ${action.getName()}(${autHelper.getParametersOfActionAsString(parameterList) })")}

<#assign openBracketsCount = 0>

<#list actionsHelper.getFromAllSubActions(action) as attribute>
    <#if compHelper.isObjectAttribute(attribute)>
        ${compHelper.getAttributeType(attribute)} ${attribute.getName()} = new ${compHelper.getAttributeType(attribute)}();
      this.${attribute.getName()}.setUp();
    <#else>
        ${compHelper.mapToWrapped(attribute)} ${attribute.getName()} = new ${compHelper.mapToWrapped(attribute)}();
    </#if>
</#list>
<#if actionsHelper.hasActionDecideMerge(action)>
    <#assign  firstControlNode = actionsHelper.getFirstControlNode(action)>
    <#assign  secondControlNode = actionsHelper.getSecondControlNode(action)>
        //start
    <@printPath actionsHelper.getPathFromStart(action)/>
        <#if actionsHelper.isMergeNode(firstControlNode)>
            <#else >
    <#list actionsHelper.getDecisionPaths(firstControlNode,secondControlNode) as path>
        //path:
     <@printPath path/>
    </#list>
        </#if>
  <#else >

    <#if actionsHelper.hasActionForkJoin(action)>
        <#assign  firstControlNode = actionsHelper.getFirstControlNode(action)>
        <#assign  secondControlNode = actionsHelper.getSecondControlNode(action)>
        //start
        <@printPath actionsHelper.getPathFromStart(action)/>
        <#list actionsHelper.getDecisionPaths(firstControlNode,secondControlNode) as path>
          //path:
            <@printPath path/>
        </#list>

        <@printPath actionsHelper.getEndPath(action)/>
        <#else>
        just print path
    <@printPath actionsHelper.getPathFromStart(action)/>
    </#if>
</#if>

<#macro printPath successionList>


    <#list successionList as succession>
        <#if succession.isPresentGuard()>
          if (${autHelper.printExpression(succession.getGuard())}){
            ${succession.getTgt()}();
            <#assign openBracketsCount = openBracketsCount + 1>
        <#else>
            <#if actionsHelper.isDoneOrControlNode(succession.getTgt(),succession)>
            <#else >
                <#assign resolvedTarget = actionsHelper.resolveAction(succession.getTgt(), succession)>
            ${succession.getTgt()}(<#list  (actionsHelper.getParameters(resolvedTarget)) as param>${param.getName()}<#sep>, </#sep></#list>);
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


<#macro printAttributes attributeList>

</#macro>
