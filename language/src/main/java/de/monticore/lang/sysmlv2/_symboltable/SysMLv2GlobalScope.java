package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import org.apache.commons.io.FilenameUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class SysMLv2GlobalScope extends SysMLv2GlobalScopeTOP {
  /**
   * We initialize the Basic types for SysML here as defined in KernelLibrary/ScalarValues.kerml
   */
  @Override
  public void init() {
    super.init();
    add(SysMLv2Mill.typeSymbolBuilder()
        .setName("Boolean")
        .setEnclosingScope(this)
        .setFullName("Boolean")
        .build());

    add(SysMLv2Mill.typeSymbolBuilder()
        .setName("String")
        .setEnclosingScope(this)
        .setFullName("String")
        .build());

    add(SysMLv2Mill.typeSymbolBuilder()
        .setName("Complex")
        .setEnclosingScope(this)
        .setFullName("Complex")
        .build());

    add(SysMLv2Mill.typeSymbolBuilder()
        .setName("Real")
        .setEnclosingScope(this)
        .setFullName("Real")
        .build());

    add(SysMLv2Mill.typeSymbolBuilder()
        .setName("Rational")
        .setEnclosingScope(this)
        .setFullName("Rational")
        .build());

    add(SysMLv2Mill.typeSymbolBuilder()
        .setName("Integer")
        .setEnclosingScope(this)
        .setFullName("Integer")
        .build());

    add(SysMLv2Mill.typeSymbolBuilder()
        .setName("Natural")
        .setEnclosingScope(this)
        .setFullName("Natural")
        .build());

    add(SysMLv2Mill.typeSymbolBuilder()
        .setName("Positive")
        .setEnclosingScope(this)
        .setFullName("Positive")
        .build());
  }

  private Set<String> findAllSymboltables() {
    return getSymbolPath().getEntries()
        .stream()
        .filter(it -> !it.toFile().isDirectory())
        .map(it -> FilenameUtils.getBaseName(it.toString()))
        .collect(Collectors.toSet());
  }

  /**
   * As the artifact scope is transparent for symbol resolution we have to consider all serialized symbol tables
   * when loading types from global scope
   */
  @Override
  public Set<String> calculateModelNamesForActionDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForActionUsage(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForSysMLType(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForSysMLUsage(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForPartDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForAttributeDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForItemDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForEnumDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForPartProperty(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForSysMLReference(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForSysMLPortDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForSysMLPortUsage(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForConnectionDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForConnectionDefEnd(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForNestedConnection(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForConnectionUsage(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForStateExhibition(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForItemProperty(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForSysMLPackage(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForConstraintDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForConstraintUsage(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForRequirementDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForRequirementUsage(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForRequirementSubject(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForStateDef(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForStateUsage(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForSTMTransition(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForOOType(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForField(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForMethod(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForDiagram(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForType(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForTypeVar(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForVariable(String name) {
    return findAllSymboltables();
  }

  @Override
  public Set<String> calculateModelNamesForFunction(String name) {
    return findAllSymboltables();
  }

  @Override
  public SysMLv2GlobalScope getRealThis() {
    return this;
  }
}
