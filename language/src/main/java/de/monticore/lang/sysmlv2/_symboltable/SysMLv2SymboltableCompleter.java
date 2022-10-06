package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLElement;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Visitor2;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SysMLv2SymboltableCompleter implements SysMLBasisVisitor2, SysMLPartsVisitor2, SysMLv2Visitor2 {

  /**
   * Returns type completion for Usages. The type is not resolved, and we only store the qualified name as
   * SymTypeExpression.
   */
  private List<SymTypeExpression> getTypeCompletion(List<ASTSpecialization> specializationList, boolean conjugated) {
    return specializationList.stream().filter(astSpecialization -> astSpecialization instanceof ASTSysMLTyping
        && ((ASTSysMLTyping) astSpecialization).isConjugated() == conjugated).flatMap(
        astTyping -> astTyping.getSuperTypesList().stream().map(astmcQualifiedName ->
            // set enclosing scope to globalscope to force the fully qualified name to the provided value
            (SymTypeExpression) SymTypeExpressionFactory.createTypeObject(
                astmcQualifiedName.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())),
                SysMLv2Mill.globalScope()))).collect(Collectors.toList());
  }

  @Override
  public void visit(ASTSysMLParameter node) {
    List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);

    if(node.isPresentSymbol() && !types.isEmpty()) {
      node.getSymbol().setType(types.get(0));
    }
  }

  @Override
  public void visit(ASTPartUsage node) {
    List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);

    if(node.isPresentSymbol()) {
      node.getSymbol().setTypesList(types);
    }
  }

  @Override
  public void visit(ASTPortUsage node) {
    if(node.isPresentSymbol()) {
      PortUsageSymbol symbol = node.getSymbol();

      List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);
      List<SymTypeExpression> conjugatedTypes = getTypeCompletion(node.getSpecializationList(), true);

      symbol.setTypesList(types);
      symbol.setConjugatedTypesList(conjugatedTypes);
    }
  }

  // TODO Die fliegt ganz weg, stattdessenn ein "Stream" ins GlobalScope mit der entsprechenden Methode (Doku von
  // Mathias folgt am Issue)
  @Override
  public void visit(ASTAttributeUsage node) {
    if(node.isPresentSymbol()) {
      AttributeUsageSymbol symbol = node.getSymbol();
      // type
      List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);

      symbol.setTypesList(types);

      // we generate get-functions for each specialized return type
      // TODO it could be useful to generate a get-function only for the most specialized
      //  type and implement a type checker that handles specializations.
      for (SymTypeExpression type : types) {
        var functionSymbol = symbolOfSnthFunction(type);
        if(type.getTypeInfo().getSpannedScope() == null) {
          type.getTypeInfo().setSpannedScope(new BasicSymbolsScope());
        }
        type.getTypeInfo().getSpannedScope().add(functionSymbol);
      }

      // feature direction
      if(node.isPresentSysMLFeatureDirection()) {
        symbol.setDirection(node.getSysMLFeatureDirection());
      }
      else {
        symbol.setDirection(ASTSysMLFeatureDirection.IN);
      }
    }
  }

  /**
   * Sets the name of the artifact scope. This enables cross-artifact resolution. SysML v2 seems to assume that the
   * filename and top-level element (typicallya package) share the same name. There are not "packages" in the MontiCore/
   * Java-sense. Packages in SysML are first-class citizens, i.e., model elements instead of model properties.
   */
  @Override
  public void visit(ISysMLv2ArtifactScope scope) {
    if(!scope.isPresentAstNode() || !(scope.getAstNode() instanceof ASTSysMLModel)) {
      Log.debug("The AST was Unexpectedly missing or had the wrong type.", getClass().getName());
    }
    else {
      ASTSysMLModel ast = (ASTSysMLModel) scope.getAstNode();
      if(ast.sizeSysMLElements() == 1) {
        ASTSysMLElement topLevelElement = ast.getSysMLElement(0);
        // TODO Kann man irgendwie generisch (oder mit dem bösen R-Wort, das auf "eflection" endet) den Namen finden?
        if(topLevelElement instanceof ASTSysMLPackage) {
          scope.setName(((ASTSysMLPackage) topLevelElement).getName());
          return;
        }
      }
    }
    // Make it unique but unguessable
    scope.setName("AnonymousArtifact_" + UUID.randomUUID());
  }

  private FunctionSymbol symbolOfSnthFunction(SymTypeExpression returnType) {
    var symbol = new FunctionSymbol("snth");
    symbol.setType(returnType);
    symbol.setSpannedScope(spannedScopedOfGet());
    return symbol;
  }

  private BasicSymbolsScope spannedScopedOfGet() {
    var scope = new BasicSymbolsScope();
    var parameter = new VariableSymbol("int");
    parameter.setType(new SymTypePrimitive(new TypeSymbol("int")));
    scope.add(parameter);
    return scope;
  }
}
