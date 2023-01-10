<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "converter", "hwPath", "generator")}

<!-- ====================================================================
     build classdiagram
-->
<#assign cdata=converter.doConvert(ast, glex)>

<!-- ====================================================================
     call TopDecorator
-->
<#--<#assign topDecorator = tc.instantiate("de.monticore.cd.codegen.TopDecorator", [hwPath])>
${topDecorator.decorate(cdata.getCompilationUnit())}-->

<!-- ====================================================================
     Generate Java-classes
-->
${generator.generate(cdata.getCompilationUnit())}
