<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateList", "enumName")}
${cd4c.method("protected void exit(${enumName} from, ${enumName} to)")}

    switch (from) {
    <#list stateList as state>
      case ${state.getName()} :
        exit${state.getName()?cap_first}();
        break;
    </#list>
    }

