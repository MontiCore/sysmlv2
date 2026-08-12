<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature( "simpleName", "symbolFullName")}
  final LinkedHashSet<${symbolFullName}> result = new LinkedHashSet<>();
  if (checkIfContinueWithEnclosingScope(foundSymbols) && getEnclosingScope() != null) {
    Set<String> potentialNames = calcQNamesForEnclosingScope(name, getImportStatementsInCurrentScope());

    for (String potentialName : potentialNames) {
      var resolvedEnclosing = getEnclosingScope().resolve${simpleName}Many(foundSymbols,
      potentialName,
      modifier,
      predicate);
      result.addAll(resolvedEnclosing);
      foundSymbols = foundSymbols || !resolvedEnclosing.isEmpty();
    }
  }

  return new ArrayList<>(result);
