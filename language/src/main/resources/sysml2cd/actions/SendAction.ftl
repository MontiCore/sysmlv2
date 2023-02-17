<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("sendAction")}

    this.parentPart.get${sendAction.getTarget()?cap_first}().setValue(${autHelper.printExpression(sendAction.getPayload())});

