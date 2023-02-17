<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("action")}

    <#if autHelper.isSendAction(action)>
        <#assign sendAction = autHelper.castToSend(transition.getDoAction())>
        ${tc.includeArgs("sysml2cd.actions.SendAction.ftl",sendAction)
        }
    <#else>
    </#if>
