<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("automaton", "enumName")}
${cd4c.method("public ${automaton.getName()}()")}
  // set currentState to initial state

  this.currentState = ${enumName}.first;

