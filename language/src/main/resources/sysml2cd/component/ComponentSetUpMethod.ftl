<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("subPartList", "portList")}
${cd4c.method("public void setUp()")}

<#list subPartList as subcomponent>
  this.${subcomponent.getName()} = new ${compHelper.getPartType(subcomponent)}();
  this.${subcomponent.getName()}.setUp();
</#list>

