<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributeList")}
${cd4c.method("public void setUp()")}


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
