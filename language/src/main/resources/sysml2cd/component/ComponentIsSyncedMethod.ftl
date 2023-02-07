<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("inPortList")}

${cd4c.method("public boolean isSynced()")}
return
<#list inPortList as inPort>
  this.get${inPort.getName()?cap_first}().isSynced()<#sep> && </#sep>
<#else>
  true
</#list>;


