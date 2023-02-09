<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("subPartList", "portList", "attributeList")}
${cd4c.method("public void setUp()")}
<#if subPartList?has_content>
//initialize sub components
</#if>
<#list subPartList as subcomponent>
  this.${subcomponent.getName()} = new ${compHelper.getPartType(subcomponent)}();
  this.${subcomponent.getName()}.setUp();
</#list>

<#if attributeList?has_content>
  //initialize other attributes
</#if>
<#list attributeList as attribute>
  <#if compHelper.isObjectAttribute(attribute)>
  this.${attribute.getName()} = new ${compHelper.getAttributeType(attribute)}();
  this.${attribute.getName()}.setUp();
  <#else>
  </#if>
</#list>
