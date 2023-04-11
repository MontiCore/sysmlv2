<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("subPartList", "outPortList","inPortList", "attributeList", "part")}
${cd4c.method("public void setUp()")}
    <#if subPartList?has_content>
      //initialize sub components
    </#if>
    <#list subPartList as subcomponent>
      this.${subcomponent.getName()} = new ${compHelper.getPartType(subcomponent)}();
      this.${subcomponent.getName()}.setUp();
    </#list>

    <#if attributeList?has_content>
      //initialize other attributes
    </#if>
    <#list attributeList as attribute>
        <#if compHelper.isObjectAttribute(attribute)>
          this.${attribute.getName()} = new ${compHelper.getAttributeType(attribute)}();
          this.${attribute.getName()}.setUp();
        <#else>
        </#if>
        <#if attribute.isPresentExpression()>
          this.${attribute.getName()} = ${compHelper.getDefaultValue(attribute)};
        </#if>
    </#list>


    <#if part.hasAutomaton()>
        <#assign automaton = part.getAutomaton()>
      this.${automaton.getName()} = new ${automaton.getName()}(this);
    </#if>


    <#list outPortList as port>
        <#if compHelper.isPortDelayed(port)>
          this.${port.getName()} = new de.monticore.lang.sysmlv2.generator.timesync.DelayPort<${compHelper.getValueTypeOfPort(port)}>();
        <#else>
          this.${port.getName()} = new de.monticore.lang.sysmlv2.generator.timesync.OutPort<${compHelper.getValueTypeOfPort(port)}>();
        </#if>
        <#if port.getValueAttribute().isPresentExpression()>
          this.${port.getName()}.setValue(${compHelper.getDefaultValue(port)});
        </#if>
    </#list>

    <#list inPortList as port>
          this.${port.getName()} = new de.monticore.lang.sysmlv2.generator.timesync.InPort<${compHelper.getValueTypeOfPort(port)}>();
          <#if port.getValueAttribute().isPresentExpression()>
            this.${port.getName()}.update(${compHelper.getDefaultValue(port)});
          </#if>
    </#list>
    <#list compHelper.getFlowOfPart(part) as connection>
      <#if compHelper.isOutPort(connection.getSource(), part) && compHelper.isOutPort(connection.getTarget(), part)>
      ${connection.getTarget()} = ${connection.getSource()};
      <#elseif compHelper.isInPort(connection.getSource(), part) && compHelper.isInPort(connection.getTarget(), part)>
      ${connection.getTarget()} = ${connection.getSource()};
      <#else >
      this.${connection.getSource()}.connect(${connection.getTarget()});
      </#if>
    </#list>
