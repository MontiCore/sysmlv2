<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("outPortList","subComponentList")}

${cd4c.method("public void tick()")}
// update subcomponents
<#list subComponentList as subcomponent>
    <#lt>this.${subcomponent.getName()}.tick();
</#list>
// update outgoing ports
<#list outPortList as port>
    <#lt> this.${port.getName()}.tick();
</#list>
