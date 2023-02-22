<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("automaton", "parentType")}
${cd4c.constructor("public ${automaton.getName()}(${parentType} parentPart)")}
    // set currentState to initial state

    this.parentPart = parentPart;
    this.${autHelper.resolveCurrentStateName(automaton)} = ${autHelper.resolveEnumName(automaton)}.start;

