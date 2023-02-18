<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("action")}

    <#if actionsHelper.isSendAction(action)>
        <#assign sendAction = actionsHelper.castToSend(transition.getDoAction())>
        ${tc.includeArgs("sysml2cd.actions.SendAction.ftl",sendAction)
        }
    </#if>
