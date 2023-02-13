<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("subComponentList")}
<#assign iComponent = "de.monticore.lang.sysmlv2.generator.timesync.IComponent" />
${cd4c.method("protected java.util.List<${iComponent}> getAllSubcomponents()")}

    return java.util.Arrays.asList(new ${iComponent}[] {
    <#list subComponentList as subcomponent>
        ${subcomponent.getName()}<#sep>, </#sep>
    </#list>
    });

