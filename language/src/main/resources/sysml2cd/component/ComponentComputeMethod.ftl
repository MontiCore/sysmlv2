<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("part")}

${cd4c.method("public void compute()")}

<#if part.hasAutomaton()>
    <#assign automaton = part.getAutomaton()>
    //run automaton behaviour of part
    ${automaton.getName()}.compute();
</#if>
// run compute on subComponent
    java.util.List${r"<montiarc.rte.timesync.IComponent>"} notYetComputed = new java.util.ArrayList<>(getAllSubcomponents());
    while(notYetComputed.size() > 0) {
      java.util.Set${r"<montiarc.rte.timesync.IComponent>"} computedThisIteration = new java.util.HashSet<>();
      for(montiarc.rte.timesync.IComponent subcomponent : notYetComputed) {
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
