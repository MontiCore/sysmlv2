package de.monticore.lang.sysmlv2._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.lang.componentconnector._symboltable.AutomatonSymbol;
import de.monticore.lang.componentconnector._symboltable.EventAutomatonSymbol;
import de.monticore.lang.componentconnector._symboltable.MildComponentSymbol;
import de.monticore.lang.componentconnector._symboltable.MildPortSymbol;
import de.monticore.lang.componentconnector._symboltable.MildSpecificationSymbol;
import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._symboltable.AnonymousUsageSymbol;
import de.monticore.lang.sysmlconstraints._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlconstraints._symboltable.RequirementSubjectSymbol;
import de.monticore.lang.sysmlconstraints.symboltable.adapters.RequirementSubject2VariableSymbolAdapter;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesVisitor2;
import de.monticore.lang.sysmloccurrences.symboltable.adapters.ItemDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlparts.symboltable.adapters.AnonymousUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeUsage2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.AttributeUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.EnumDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PartDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PartUsage2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlstates._symboltable.StateUsageSymbol;
import de.monticore.lang.sysmlstates.symboltable.adapters.StateDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Visitor2;
import de.monticore.lang.sysmlv2.symboltable.adapters.AttributeUsage2PortSymbolAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.Constraint2SpecificationAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.PartDef2ComponentAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.Requirement2RequirementCCAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.Requirement2SpecificationAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.StateUsage2AutomatonAdapter;
import de.monticore.lang.sysmlv2.symboltable.adapters.StateUsage2EventAutomatonAdapter;
import de.monticore.lang.componentconnector._symboltable.RequirementSymbol;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.se_rwth.commons.Names.getSimpleName;

public interface ISysMLv2Scope extends ISysMLv2ScopeTOP {

  /**
   * In SysML, namespaces live inside SysML models (keyword "package") and
   * there can be multiple namespaces in a single model. This is sometimes
   * referred to as "first class support" of namespaces. In Java-like
   * programming languages, the namespace of an artifact is handled implicitly
   * through the file system path (in conjunction with a package declaration
   * for easier inside-out-resolving). Blocks, methods, or classes are not
   * considered full namespaces (resolving "bar" from within a class "Foo"
   * does not yield the potential qualified name "Foo.bar" at the global scope).
   * MontiCore's default resolve-mechanism is built to behave Java-like, i.e.,
   * it assumes that namespaces exist only at the file level and only package
   * declarations matter for the calculation of potential names. Therefore, the
   * logic of looking for all potential qualified names is only executed when
   * leaving the artifact scope and does not account for any scope names passed
   * on the way up.
   * <br>
   * This override changes this. It explicitly adds one new potential name to
   * the list of potential names every time a package is passed while continuing
   * with the enclosing scope. Assume we look for "bar", we pass "package Foo",
   * then the list of potential names we are resolving for is now
   * ["bar", "Foo.bar"].
   * <br>
   * <b>Notice</b>: SysML comes with a large number of keywords
   * (e.g., occurrence, item, attribute, part) that have no or very little
   * meaning wrt. to symbol resolution. In MontiCore, we already established the
   * basic set of symbols (aptly named "BasicSymbols"), namely Types, Variables,
   * and Functions. To avoid re-implementing resolving functionality for all
   * keywords, we use symbol adapters from SysML definitions to MontiCore types,
   * SysML usages to MontiCore variables, and SysML constraints (including
   * calc defs) to MontiCore functions. This method here handles resolving of
   * MontiCore types, i.e., SysML definitions.
   */
  @Override
  default List<TypeSymbol> continueTypeWithEnclosingScope(
    boolean foundSymbols,
    String name,
    AccessModifier modifier,
    Predicate<TypeSymbol> predicate
  ) {
    final LinkedHashSet<TypeSymbol> result = new LinkedHashSet<>();
    if (
      checkIfContinueWithEnclosingScope(foundSymbols)
      && getEnclosingScope() != null
    ) {

      var importStatements = new LinkedList<ASTSysMLImportStatement>();
      if(getEnclosingScope().isPresentAstNode()) {
        // TODO this only finds the imports in enclosing. Why visitor?
        var visitor = new SysMLImportsAndPackagesVisitor2() {
          @Override
          public void visit(ASTSysMLImportStatement node) {
            if (getEnclosingScope().equals(node.getEnclosingScope())) {
              importStatements.add(node);
            }
          }
        };
        var traverser = SysMLv2Mill.inheritanceTraverser();
        traverser.add4SysMLImportsAndPackages(visitor);
        getEnclosingScope().getAstNode().accept(traverser);
      }

      Set<String> potentialNames = calcQNamesForEnclosingScope(name, importStatements);

      for (String potentialName : potentialNames) {
        var resolvedEnclosing = getEnclosingScope().resolveTypeMany( foundSymbols,
            potentialName,
            modifier,
            predicate
        );
        result.addAll(resolvedEnclosing);
        foundSymbols = foundSymbols || !resolvedEnclosing.isEmpty();
      }
    }

    return new ArrayList<>(result);
  }

  /**
   * @see ISysMLv2Scope#continueTypeWithEnclosingScope(boolean, String, AccessModifier, Predicate)
   */
  @Override
  default List<VariableSymbol> continueVariableWithEnclosingScope(
    boolean foundSymbols,
    String name,
    AccessModifier modifier,
    Predicate<VariableSymbol> predicate
  ) {
    final LinkedHashSet<VariableSymbol> result = new LinkedHashSet<>();
    if (
      checkIfContinueWithEnclosingScope(foundSymbols)
      && getEnclosingScope() != null
    ) {

      var importStatements = new LinkedList<ASTSysMLImportStatement>();
      if(getEnclosingScope().isPresentAstNode()) {
        var visitor = new SysMLImportsAndPackagesVisitor2() {
          @Override
          public void visit(ASTSysMLImportStatement node) {
            if (getEnclosingScope().equals(node.getEnclosingScope())) {
              importStatements.add(node);
            }
          }
        };
        var traverser = SysMLv2Mill.inheritanceTraverser();
        traverser.add4SysMLImportsAndPackages(visitor);
        getEnclosingScope().getAstNode().accept(traverser);
      }

      Set<String> potentialNames = calcQNamesForEnclosingScope(name, importStatements);

      for (String potentialName : potentialNames) {
        var resolvedEnclosing = getEnclosingScope().resolveVariableMany( foundSymbols,
            potentialName,
            modifier,
            predicate
        );
        result.addAll(resolvedEnclosing);
        foundSymbols = foundSymbols || !resolvedEnclosing.isEmpty();
      }
    }
    return new ArrayList<>(result);
  }

  /**
   * @see ISysMLv2Scope#continueTypeWithEnclosingScope(boolean, String, AccessModifier, Predicate)
   */
  @Override
  default List<FunctionSymbol> continueFunctionWithEnclosingScope(
    boolean foundSymbols,
    String name,
    AccessModifier modifier,
    Predicate<FunctionSymbol> predicate
  ) {
    final LinkedHashSet<FunctionSymbol> result = new LinkedHashSet<>();
    if (
      checkIfContinueWithEnclosingScope(foundSymbols)
      && (getEnclosingScope() != null)
    ) {

      var importStatements = new LinkedList<ASTSysMLImportStatement>();
      if(getEnclosingScope().isPresentAstNode()) {
        var visitor = new SysMLImportsAndPackagesVisitor2() {
          @Override
          public void visit(ASTSysMLImportStatement node) {
            if (getEnclosingScope().equals(node.getEnclosingScope())) {
              importStatements.add(node);
            }
          }
        };
        var traverser = SysMLv2Mill.inheritanceTraverser();
        traverser.add4SysMLImportsAndPackages(visitor);
        getEnclosingScope().getAstNode().accept(traverser);
      }

      Set<String> potentialNames = calcQNamesForEnclosingScope(name, importStatements);

      for (String potentialName : potentialNames) {
        var resolvedEnclosing = getEnclosingScope().resolveFunctionMany( foundSymbols,
            potentialName,
            modifier,
            predicate
        );
        result.addAll(resolvedEnclosing);
        foundSymbols = foundSymbols || !resolvedEnclosing.isEmpty();
      }
    }
    return new ArrayList<>(result);
  }

  public static <T> String getRelativeFromFqn(String relative, String fqn) {
    if (relative == null || fqn == null || relative.length() > fqn.length()) {
      return "";
    }
    if (relative.isEmpty()) {
      return "";
    }

    // 1. Collections.indexOfSubList finds where the relative path starts inside the fqn
    int startIndex = fqn.indexOf(relative);

    // 2. If 'relative' is not found inside 'fqn', return an empty list
    if (startIndex == -1) {
      return "";
    }

    // 3. Extract the slice using .subList() and wrap it in a new ArrayList
    return fqn.substring(startIndex);
  }

  public default void findAllScopeNamesForRecursive(String name, ASTSysMLImportStatement statement, Set<String> names) {
    // TODO theoretically usages cannot be imported. see if true. if true then no namespace symbols is needed
    var namespaceCollector = new SysMLv2Visitor2() {
      @Override
      public void visit(ISysMLv2Scope scope) {
        // only look in named scopes
        if (scope.isPresentName() && scope.isPresentSpanningSymbol()) {
          // TODO also public imports
          names.add(
              getRelativeFromFqn(statement.getQName(),
                  scope.getSpanningSymbol().getFullName()));
          findAllNamespacesForImports(name, scope.getSysMLImportsList(), names);
        }
      }
    };

    var traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLv2(namespaceCollector);
    var namespace = resolveSysMLType(statement.getQName());
    var packageNamespace = resolveSysMLPackage(statement.getQName());
    if (namespace.isPresent() && namespace.get() instanceof IScopeSpanningSymbol) {
      ((IScopeSpanningSymbol)namespace.get()).getSpannedScope().accept(traverser);
    } else packageNamespace.ifPresent(sysMLPackageSymbol -> sysMLPackageSymbol.getSpannedScope().accept(traverser));
  }

  /**
   * This method is essentially copied from artifact scopes. See explanation
   * on continueTypeWithEnclosingScope(4): MontiCore's symbol resolution is
   * Java-like out-of-the-box and needs to be extended for SysMLv2's usage
   * of packages (namespaces) as proper modeling elements.
   * Also, the Scopes-Included Imports are used for potential name qualification.
   * Therefore: 1. Matching, direct Imports are taken as FQNs
   *            2. Star and recursive Imports do try to resolve the import
   *               where the wildcard is replaced by the resolved Symbols name.
   *               Therefore, recursive imports are only supported as star-imports.
   */
  default Set<String> calcQNamesForEnclosingScope(String name,
                                                  List<ASTSysMLImportStatement> importStatements) {
    Set<String> potentialNamespaces = new LinkedHashSet<>();

    // if name is already qualified, no further (potential) names exist by imports
    // qualify names based on the import statements of enclosing scope
    // 1. qualify star imports by replacing the start with symbolname
    // 2. qualify direct imports when the name matches
    findAllNamespacesForImports(name, importStatements, potentialNamespaces);
    var res = potentialNamespaces.stream().map(namespace -> namespace + "." + name).collect(Collectors.toSet());
    res.add(name);

    return res;
  }

  public default void findAllNamespacesForImports(String name, List<ASTSysMLImportStatement> importStatements, Set<String> namespaces) {
    for (var importStatement : importStatements) {
      // in sysml it is valid to resolve B::C somewhere where A::B was imported
      List<String> partsList = importStatement.getMCQualifiedName().getPartsList();
      List<String> nameList = Splitters.DOT.splitToList(name);
      if (partsList.get(partsList.size() - 1).equals(nameList.get(0)) && !importStatement.isStar()) {
        // in NonStar Recursive imports you also can address the statement itself
        namespaces.add(Names.constructQualifiedName(partsList.subList(0, partsList.size() - 1))); // import A; What is that you can find S in there (so go after its orig fqn)
      }
      if (importStatement.isRecursive()) {
        var traverser = SysMLv2Mill.inheritanceTraverser();
        traverser.add4SysMLv2( new SysMLv2Visitor2() {
          @Override
          public void visit(ISysMLv2Scope scope) {
            // only look in named scopes
            if (scope.isPresentName() && scope.isPresentSpanningSymbol()) {
              namespaces.add(
                  getRelativeFromFqn(importStatement.getQName(),
                      scope.getSpanningSymbol().getFullName()));
              findAllNamespacesForImports(name, scope.getSysMLImportsList(), namespaces);
            }
          }
        });
        var namespace = resolveSysMLType(importStatement.getQName());
        var packageNamespace = resolveSysMLPackage(importStatement.getQName());
        if (namespace.isPresent() && namespace.get() instanceof IScopeSpanningSymbol) {
          ((IScopeSpanningSymbol)namespace.get()).getSpannedScope().accept(traverser);
        } else packageNamespace.ifPresent(sysMLPackageSymbol -> sysMLPackageSymbol.getSpannedScope().accept(traverser));
      }
      if (importStatement.isStar() && !importStatement.isRecursive()) {
        namespaces.add(importStatement.getQName());
        var potNamespace = resolveSysMLType(importStatement.getQName());
        var potNamespacePackage = resolveSysMLPackage(importStatement.getQName());
        if (potNamespace.isPresent()) {
          findAllNamespacesForImports(name,
              ((ISysMLv2Scope) ((IScopeSpanningSymbol) potNamespace.get()).getSpannedScope()).getSysMLImportsList(),
              namespaces);
        } else if (potNamespacePackage.isPresent()) {
          findAllNamespacesForImports(name, ((ISysMLv2Scope)((IScopeSpanningSymbol)potNamespacePackage.get()).getSpannedScope()).getSysMLImportsList(), namespaces);
        }
      }
    }
  }

  @Override
  default List<RequirementSymbol> resolveAdaptedRequirementLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<RequirementSymbol> predicate) {
    var adapted = new ArrayList<RequirementSymbol>();
    var usages = resolveRequirementUsageLocallyMany(foundSymbols, name, modifier, x -> true);

    for(var req : usages) {
      var ccAdaptedReq = new Requirement2RequirementCCAdapter(req);
      if(predicate.test(ccAdaptedReq)) {
        adapted.add(ccAdaptedReq);
      }
    }

    return adapted;
  }

  /**
   * Adaptiert AttributeUsages oder PortUsages zu Variablen.
   * <br>
   * Wird für den TypeCheck benötigt, da wir in der Grammatik uns dafür
   * entschieden haben, die SysML-Konzepte nicht von Type, Field, etc.
   * abzuleiten. Das ist konsistent mit den Empfehlungen aus dem MC-Buch.
   */
  @Override
  default List<VariableSymbol> resolveAdaptedVariableLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<VariableSymbol> predicate
  ) {
    // We ignore modifiers and predicates for the moment
    var ports = resolvePortUsageLocallyMany(false, name,
        AccessModifier.ALL_INCLUSION, x -> true);
    var attributes = resolveAttributeUsageLocallyMany(false, name,
        AccessModifier.ALL_INCLUSION, x -> true);
    var requirementSubjects = resolveRequirementSubjectLocallyMany(false, name,
        AccessModifier.ALL_INCLUSION, x -> true);
    var anonymous = resolveAnonymousUsageLocallyMany(false, name,
        AccessModifier.ALL_INCLUSION, x -> true);

    var adapted = new ArrayList<VariableSymbol>();
    for (PortUsageSymbol portUsage : ports) {
      var types = new ArrayList<SymTypeExpression>();
      types.addAll(portUsage.getTypesList());
      types.addAll(portUsage.getConjugatedTypesList());

      if (types.size() == 1) {
        var resolved = types.get(0).getTypeInfo();

        // we omit to set the ASTNode
        var variable = new PortUsage2VariableSymbolAdapter(portUsage);

        if (portUsage.hasCardinality()) {
          variable.setType(SymTypeExpressionFactory.createTypeArray(resolved, 1,
              SymTypeExpressionFactory.createTypeObject(resolved)));
        }
        else {
          variable.setType(SymTypeExpressionFactory.createTypeObject(resolved));
        }

        adapted.add(variable);
      }
    }

    for (AttributeUsageSymbol attrUsage : attributes) {
      var types = attrUsage.getTypesList();

      if (types.size() == 1) {
        var attributeType = types.get(0);

        // we omit to set the ASTNode
        var variable = new AttributeUsage2VariableSymbolAdapter(attrUsage);

        // Jetzt direkt korrekt im Completer
        /*if(attrUsage.getAstNode().getCardinality().isPresent()) {
          variable.setType(SymTypeExpressionFactory.createTypeArray
          (attributeType.getTypeInfo(), 1,
              attributeType));
        }else {
          variable.setType(attributeType);
        }*/

        adapted.add(variable);
      }
    }

    for (RequirementSubjectSymbol reqSub : requirementSubjects) {
      var variable = new RequirementSubject2VariableSymbolAdapter(reqSub);
      adapted.add(variable);
    }

    for (AnonymousUsageSymbol anonymousUsage : anonymous) {
      var types = new ArrayList<SymTypeExpression>();
      types.addAll(anonymousUsage.getTypesList());

      if (types.size() == 1) {
        var resolved = types.get(0).getTypeInfo();

        // we omit to set the ASTNode
        var variable = new AnonymousUsage2VariableSymbolAdapter(anonymousUsage);
        variable.setType(SymTypeExpressionFactory.createTypeObject(resolved));
        adapted.add(variable);
      }
    }

    // Falls Requirement, dann subject befragen
    if (this.isPresentAstNode()
        && this.getAstNode() instanceof ASTRequirementUsage) {
      var ast = (ASTRequirementUsage) this.getAstNode();
      if (ast.isPresentRequirementSubject()) {
        // Alle Typen kommen in Frage
        ast.getRequirementSubject().getSpecializationList().stream().flatMap(
            ASTSpecialization::streamSuperTypes).forEach(t -> {
          var typeSymbol = t.getDefiningSymbol();
          if (typeSymbol.isPresent()
              && typeSymbol.get() instanceof IScopeSpanningSymbol) {
            var scope =
                ((IScopeSpanningSymbol) typeSymbol.get()).getSpannedScope();
            if (scope instanceof IBasicSymbolsScope) {
              var variable = ((IBasicSymbolsScope) scope).resolveVariableDown(
                  name, modifier, predicate);
              if (variable.isPresent()) {
                adapted.add(variable.get());
              }
            }
          }
        });
      }
    }

    return adapted;
  }

  /**
   * Adaptiert SysML Definitions (PortDefs, PartDefs, StateDef) zu Types. Dient
   * dazu, dass man an verschiedenen Stellen nach "Type" resolven kann
   * (scope.resolveType('MyPortDef') zB.) und ein (adaptiertes) Type-Symbol
   * findet. Eingesetzt für TypeCheck.
   */
  @Override
  default List<TypeSymbol> resolveAdaptedTypeLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<TypeSymbol> predicate
  ) {
    var adapted = new ArrayList<TypeSymbol>();

    // Adaptiert PortDefs zu Types, dh. wenn wir für "port myPort: MyPortDef;
    // " nach "MyPortDef" suchen, dann können wir
    // das mit "scope.resolveType('MyPortDef')" tun!
    var portDef = resolvePortDefLocally(name);
    if (portDef.isPresent()) {
      adapted.add(new PortDef2TypeSymbolAdapter(portDef.get()));
    }

    // PartDefs zu Types
    var partDef = resolvePartDefLocally(name);
    if (partDef.isPresent()) {
      adapted.add(new PartDef2TypeSymbolAdapter(partDef.get()));
    }

    // PartUsages zu Types
    var partUsage = resolvePartUsageLocally(name);
    if (partUsage.isPresent()) {
      adapted.add(new PartUsage2TypeSymbolAdapter(partUsage.get()));
    }

    // StateDef zu Types
    var stateDef = resolveStateDefLocally(name);
    if (stateDef.isPresent()) {
      adapted.add(new StateDef2TypeSymbolAdapter(stateDef.get()));
    }

    // AttributeDef zu Types
    var attributeDef = resolveAttributeDefLocally(name);
    if (attributeDef.isPresent()) {
      adapted.add(new AttributeDef2TypeSymbolAdapter(attributeDef.get()));
    }

    // AttributeUsage zu Types
    var attributeUsage = resolveAttributeUsageLocally(name);
    if (attributeUsage.isPresent()) {
      adapted.add(new AttributeUsage2TypeSymbolAdapter(attributeUsage.get()));
    }

    // EnumDef zu Types
    var enumDef = resolveEnumDefLocally(name);
    if (enumDef.isPresent()) {
      adapted.add(new EnumDef2TypeSymbolAdapter(enumDef.get()));
    }

    // ItemDef zu Types
    var itemDef = resolveItemDefLocally(name);
    if (itemDef.isPresent()) {
      adapted.add(new ItemDef2TypeSymbolAdapter(itemDef.get()));
    }

    return adapted;
  }

  @Override
  default List<MildComponentSymbol> resolveAdaptedMildComponentLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<MildComponentSymbol> predicate
  ) {
    var adapted = new ArrayList<MildComponentSymbol>();

    var partDef = resolvePartDefLocally(name);
    if (partDef.isPresent()) {
      adapted.add(new PartDef2ComponentAdapter(partDef.get()));
    }

    return adapted;
  }

  @Override
  default List<MildSpecificationSymbol> resolveAdaptedMildSpecificationLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<MildSpecificationSymbol> predicate
  ) {
    var adapted = new ArrayList<MildSpecificationSymbol>();

    // TODO Check "satisfy" is present
    var requirementUsage = resolveRequirementUsageLocally(name);
    if (requirementUsage.isPresent()) {
      adapted.add(new Requirement2SpecificationAdapter(requirementUsage.get()));
    }

    // TODO Check "assert" is present
    var constraintUsage = resolveConstraintUsageLocally(name);
    if (constraintUsage.isPresent()) {
      adapted.add(new Constraint2SpecificationAdapter(constraintUsage.get()));
    }

    return adapted;
  }

  @Override
  default List<MildPortSymbol> resolveAdaptedMildPortLocallyMany(
      boolean foundSymbols,
      String name,
      AccessModifier modifier,
      Predicate<MildPortSymbol> predicate
  ) {
    var adapted = new ArrayList<MildPortSymbol>();

    // TODO MC-Team fragen, ob man das so macht? Gemeint ist das händische
    //  Splitten von Namen - oder ob man irgendwie
    //  nach vollqualifizierten Sachen suchen kann (und vielleicht auch erst
    //  danach schaut, was es war)?
    String delimiter;
    if (name.contains(".")) {
      delimiter = ".";
    }
    else if (name.contains("::")) {
      delimiter = "::";
    }
    else {
      delimiter = null;
    }

    if (delimiter != null) {
      List<String> parts = new ArrayList<>();
      int pos = 0;
      int idx;
      while ((idx = name.indexOf(delimiter, pos)) != -1) {
        parts.add(name.substring(pos, idx));
        pos = idx + delimiter.length();
      }
      parts.add(name.substring(pos));
      String[] nameParts = parts.toArray(new String[0]);

      // usage could be fully qualified
      var port = String.join(delimiter, Arrays.copyOfRange(nameParts, 0, nameParts.length - 1));
      var portUsage = resolvePortUsageLocally(port);
      if (portUsage.isPresent()) {
        var attr = nameParts[nameParts.length -1];
        var input = portUsage.get().getInputAttributes().stream().filter(
            a -> a.getName().equals(attr)).findFirst();
        var output = portUsage.get().getOutputAttributes().stream().filter(
            a -> a.getName().equals(attr)).findFirst();
        if (input.isPresent()) {
          adapted.add(
              new AttributeUsage2PortSymbolAdapter(input.get(), portUsage.get(),
                  true));
        }
        else if (output.isPresent()) {
          adapted.add(new AttributeUsage2PortSymbolAdapter(output.get(),
              portUsage.get(), false));
        }
      }
    }

    return adapted;
  }

  @Override
  default List<AutomatonSymbol> resolveAdaptedAutomatonLocallyMany(
      boolean foundSymbols, String name,
      AccessModifier modifier,
      Predicate<AutomatonSymbol> predicate
  ) {
    var adapted = new ArrayList<AutomatonSymbol>();

    var partDef = resolvePartDefLocally(name);

    partDef.ifPresent(part -> {
      var optAut = ((ISysMLv2Scope) part.getSpannedScope())
          .getLocalStateUsageSymbols()
          .stream()
          .filter(StateUsageSymbol::isExhibited)
          .filter(sym -> sym.getUserDefinedKeywordsList().contains("tsyn"))
          .findFirst()
          .map(state -> new StateUsage2AutomatonAdapter(part, state));

      if (optAut.isPresent()) {
        adapted.add(optAut.get());
      }
    });

    return adapted;
  }

  @Override
  default List<EventAutomatonSymbol> resolveAdaptedEventAutomatonLocallyMany(
      boolean foundSymbols, String name,
      AccessModifier modifier,
      Predicate<EventAutomatonSymbol> predicate
  ) {
    var adapted = new ArrayList<EventAutomatonSymbol>();

    var partDef = resolvePartDefLocally(name);

    partDef.ifPresent(part -> {
      var optAut = ((ISysMLv2Scope) part.getSpannedScope())
          .getLocalStateUsageSymbols()
          .stream()
          .filter(StateUsageSymbol::isExhibited)
          .filter(sym -> !sym.getUserDefinedKeywordsList().contains("tsyn"))
          .findFirst()
          .map(state -> new StateUsage2EventAutomatonAdapter(part, state));

      if (optAut.isPresent()) {
        adapted.add(optAut.get());
      }
    });

    return adapted;
  }

  // Alongside filtering for "name" and "fullname", we also filter for
  // SysMLIdentifier ("part def <this_is_an_identifier> ThisIsTheName;")
  @Override
  default Optional<PartUsageSymbol> filterPartUsage(
      String name,
      LinkedListMultimap<String, PartUsageSymbol> symbols
  ) {
    final Set<PartUsageSymbol> resolvedSymbols = new LinkedHashSet<>();

    // Skip the filter on the map key, because we might be looking for a
    // SysMLIdentifier. Instead, iterate all symbols.
    for (PartUsageSymbol symbol : symbols.values()) {
      // Checks both the symbol and the AST for SysMLIdentifier information
      // to make it work with symbols from sym-files (where the symbol
      // information was completed and stored in  previous run) and symbols
      // that were created from AST (where the symbol completion has not been
      // completed, e.g., if this code is called as part of symbol completion).
      if (symbol.getName().equals(name) ||
          symbol.getFullName().equals(name) ||
          symbol.isPresentSysMLIdentifier() // when loaded from sym files
              && symbol.getSysMLIdentifier().equals(name) ||
          symbol.isPresentAstNode() // when parsed / built to AST
              && symbol.getAstNode().isPresentSysMLIdentifier()
              && symbol.getAstNode().getSysMLIdentifier().getName().equals(name)
      ) {
        resolvedSymbols.add(symbol);
      }
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }

  // Alongside filtering for "name" and "fullname", we also filter for
  // SysMLIdentifier ("part def <this_is_an_identifier> ThisIsTheName;")
  @Override
  default Optional<StateUsageSymbol> filterStateUsage(
      String name,
      LinkedListMultimap<String, StateUsageSymbol> symbols
  ) {
    final Set<StateUsageSymbol> resolvedSymbols = new LinkedHashSet<>();

    // Skip the filter on the map key, because we might be looking for a
    // SysMLIdentifier. Instead, iterate all symbols.
    for (StateUsageSymbol symbol : symbols.values()) {
      // Checks both the symbol and the AST for SysMLIdentifier information
      // to make it work with symbols from sym-files (where the symbol
      // information was completed and stored in  previous run) and symbols
      // that were created from AST (where the symbol completion has not been
      // completed, e.g., if this code is called as part of symbol completion).
      if (symbol.getName().equals(name) ||
          symbol.getFullName().equals(name) ||
          symbol.isPresentSysMLIdentifier() // when loaded from sym files
              && symbol.getSysMLIdentifier().equals(name) ||
          symbol.isPresentAstNode() // when parsed / built to AST
              && symbol.getAstNode().isPresentSysMLIdentifier()
              && symbol.getAstNode().getSysMLIdentifier().getName().equals(name)
      ) {
        resolvedSymbols.add(symbol);
      }
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }
}
