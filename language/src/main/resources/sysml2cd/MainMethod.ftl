<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the deployment class for a component. -->
${tc.signature("listMainComponent", "listFlows", "baseName")}
${cd4c.method("public static void main(String[] args)")}

    de.monticore.lang.sysmlv2.generator.DeployUtils deployUtils = new de.monticore.lang.sysmlv2.generator.DeployUtils();

    if(!deployUtils.parseArgs(args)) {
      return;
    }

    de.monticore.lang.sysmlv2.generator.log.Log.initFileLog(deployUtils.getLogPath());
    de.monticore.lang.sysmlv2.generator.log.Log.setTraceEnabled(true);

    <#list listMainComponent as subcomponent>
        ${compHelper.cdPackageAsQualifiedName(subcomponent,baseName)}.${compHelper.getPartType(subcomponent)} ${subcomponent.getName()} = new ${compHelper.cdPackageAsQualifiedName(subcomponent,baseName)}.${compHelper.getPartType(subcomponent)}();
        ${subcomponent.getName()}.setUp();
    </#list>
    <#list listFlows as connection>
      ${connection.getSource()}.connect(${connection.getTarget()});
    </#list>

    final List${"<de.monticore.lang.sysmlv2.generator.timesync.IComponent>"} componentList =  java.util.Arrays.asList(new de.monticore.lang.sysmlv2.generator.timesync.IComponent[] {
    <#list listMainComponent as subcomponent>
        ${subcomponent.getName()}<#sep>, </#sep>
    </#list>
    });

    long time;

    for(int cycles = 0; cycles < deployUtils.getMaxCyclesCount(); cycles++) {
        de.monticore.lang.sysmlv2.generator.log.Log.trace("::: Time t = " + cycles + " :::");
        time = System.currentTimeMillis();
        for(de.monticore.lang.sysmlv2.generator.timesync.IComponent element : componentList){
            element.compute();
            element.tick();
        }
        while((System.currentTimeMillis() - time) < deployUtils.getCycleTime()) {
            Thread.yield();
        }
    }
