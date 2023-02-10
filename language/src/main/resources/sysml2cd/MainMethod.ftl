<#-- (c) https://github.com/MontiCore/monticore -->
<#-- Generates the deployment class for a component. -->
${tc.signature("listMainComponent")}
${cd4c.method("public static void main(String[] args)")}

    de.monticore.lang.sysmlv2.generator.DeployUtils deployUtils = new de.monticore.lang.sysmlv2.generator.DeployUtils();

    if(!deployUtils.parseArgs(args)) {
      return;
    }

    de.monticore.lang.sysmlv2.generator.log.Log.initFileLog(deployUtils.getLogPath());
    de.monticore.lang.sysmlv2.generator.log.Log.setTraceEnabled(true);

    <#list listMainComponent as subcomponent>
        ${compHelper.getPartType(subcomponent)} ${subcomponent.getName()} = new ${compHelper.getPartType(subcomponent)}();
        ${compHelper.getPartType(subcomponent)} ${subcomponent.getName()}.setUp();
    </#list>
    final List${"<IComponent>"} componentList =  java.util.Arrays.asList(new montiarc.rte.timesync.IComponent[] {
    <#list listMainComponent as subcomponent>
        ${subcomponent.getName()}<#sep>, </#sep>
    </#list>
    });

    long time;

    for(int cycles = 0; cycles < deployUtils.getMaxCyclesCount(); cycles++) {
        de.monticore.lang.sysmlv2.generator.log.Log.trace("::: Time t = " + cycles + " :::");
        time = System.currentTimeMillis();
        for(IComponent element : componentList){
            element.compute();
            element.tick();
        }
        while((System.currentTimeMillis() - time) < deployUtils.getCycleTime()) {
            Thread.yield();
        }
    }
