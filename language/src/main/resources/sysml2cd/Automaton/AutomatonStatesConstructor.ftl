<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("automaton", "enumName", "parentType")}
${cd4c.constructor("public ${automaton.getName()}(${parentType} parentPart)")}
    // set currentState to initial state

    this.parentPart = parentPart;
    this.currentState = ${enumName}.first;

