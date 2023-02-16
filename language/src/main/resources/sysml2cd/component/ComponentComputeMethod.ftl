<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("part", "input", "output")}

${cd4c.method("public void compute()")}
      de.monticore.lang.sysmlv2.generator.log.Log.comment("Computing component " + this.getClass().getName() + "");
    // log input values
    <#list input as port>
      de.monticore.lang.sysmlv2.generator.log.Log.trace("Value of input port ${port.getName()} = "  + this.get${port.getName()?cap_first}().getValue());
    </#list>


<#if part.hasAutomaton()>
    <#assign automaton = part.getAutomaton()>
    //run automaton behaviour of part
    ${automaton.getName()}.compute();
</#if>
// run compute on subComponent
    java.util.List${r"<de.monticore.lang.sysmlv2.generator.timesync.IComponent>"} notYetComputed = new java.util.ArrayList<>(getAllSubcomponents());
    while(notYetComputed.size() > 0) {
      java.util.Set${r"<de.monticore.lang.sysmlv2.generator.timesync.IComponent>"} computedThisIteration = new java.util.HashSet<>();
      for(de.monticore.lang.sysmlv2.generator.timesync.IComponent subcomponent : notYetComputed) {
        if(subcomponent.isSynced()) {
          subcomponent.compute();
          computedThisIteration.add(subcomponent);
        }
      }
      if(computedThisIteration.isEmpty()) {
        throw new RuntimeException("Could not complete compute cycle due to not all ports being synced. Likely reasons: Forgot to call init() or cyclic connector loop.");
      } else {
        notYetComputed.removeAll(computedThisIteration);
      }
    }
    // log output values
    <#list output as port>
      de.monticore.lang.sysmlv2.generator.log.Log.trace("Value of output port ${port.getName()} = "+ this.get${port.getName()?cap_first}().getValue());
    </#list>
