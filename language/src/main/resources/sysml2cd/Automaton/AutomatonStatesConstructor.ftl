<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("automaton", "enumName")}
${cd4c.constructor("public ${automaton.getName()}()")}
  // set currentState to initial state

  this.currentState = ${enumName}.first;

