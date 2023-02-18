<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("action","parameterList", "attributeList")}


${cd4c.method("public void ${action.getName()}(${autHelper.getParametersOfActionAsString(action, parameterList) })")}
<#list attributeList as attribute>
    <#if compHelper.isObjectAttribute(attribute)>
      ${compHelper.getAttributeType(attribute)} ${attribute.getName()} = new ${compHelper.getAttributeType(attribute)}();
      this.${attribute.getName()}.setUp();
    <#else>
      ${compHelper.getAttributeType(attribute)} ${attribute.getName()};
    </#if>
</#list>


<#list actionsHelper.getSuccessions(action) as succession>
  <#if succession.isPresentGuard()>
      if (${autHelper.printExpression(succession.getGuard())}){
          ${succession.getTgt()}();
      }
      <#else>
      ${succession.getTgt()}();
  </#if>
</#list>
