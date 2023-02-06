<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stateList")}
${cd4c.method("protected void exit(States from, States to)")}

switch (from) {
<#list stateList as state>
  case ${state.getName()} :
  exit${state.getName()?cap_first}();
  break;
</#list>
}
//TODO check:
if (from != to && from.getSuperState().isPresent()) {
exit(from.getSuperState().get(), to);
}

