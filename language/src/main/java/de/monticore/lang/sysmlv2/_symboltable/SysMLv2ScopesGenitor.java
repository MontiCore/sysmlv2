package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLType;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLUsage;
import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbolBuilder;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisHandler;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTPartDef;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTPartProperty;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTSysMLPortUsage;
import de.monticore.lang.sysmlblockdiagrams._visitor.SysMLBlockDiagramsHandler;
import de.monticore.lang.sysmlblockdiagrams._visitor.SysMLBlockDiagramsVisitor2;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirementdiagrams._visitor.SysMLRequirementDiagramsHandler;
import de.monticore.lang.sysmlrequirementdiagrams._visitor.SysMLRequirementDiagramsVisitor2;
import de.monticore.lang.sysmlstatemachinediagrams._ast.ASTStateUsage;
import de.monticore.lang.sysmlstatemachinediagrams._visitor.SysMLStateMachineDiagramsHandler;
import de.monticore.lang.sysmlstatemachinediagrams._visitor.SysMLStateMachineDiagramsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Handler;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Visitor2;
import de.monticore.lang.sysmlv2.typecheck.DeriveSysMLTypes;
import de.monticore.lang.sysmlv2.typecheck.SysMLTypesSynthesizer;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolBuilder;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * SysMLv2ScopesGenitor was extended to add postprocessing visitors primarily
 * for enriching symbol table, e.g. SysMLv2SymbolTableCompleter.
 */
public class SysMLv2ScopesGenitor extends SysMLv2ScopesGenitorTOP
    implements
    SysMLv2Visitor2, SysMLv2Handler,
    SysMLBasisVisitor2, SysMLBasisHandler,
    SysMLBlockDiagramsVisitor2, SysMLBlockDiagramsHandler,
    SysMLRequirementDiagramsVisitor2, SysMLRequirementDiagramsHandler,
    SysMLStateMachineDiagramsVisitor2, SysMLStateMachineDiagramsHandler {

  protected SysMLv2Traverser traverser;
  protected TypeCheck typeCheck;

  @Override
  public ISysMLv2ArtifactScope createFromAST(ASTSysMLModel rootNode) {
    ISysMLv2ArtifactScope artifactScope = super.createFromAST(rootNode);

    // ST Completion
    this.typeCheck = new TypeCheck(new SysMLTypesSynthesizer(), new DeriveSysMLTypes());
    this.traverser = SysMLv2Mill.traverser();
    traverser.add4SysMLBasis(this);
    traverser.add4SysMLBlockDiagrams(this);
    rootNode.accept(this.getTraverser());

    return artifactScope;
  }

  @Override
  public void visit(ASTPartDef node) {
    if (node.isPresentSysMLSpecialization()) {
      this.setSuperTypes(node, node.getSysMLSpecialization());
    }
  }

  @Override
  public void visit(ASTRequirementDef node) {
    if (node.isPresentSysMLSpecialization()) {
      this.setSuperTypes(node, node.getSysMLSpecialization());
    }
  }

  /**
   * Following method sets super types for a SysMLType.
   * Additionally, reflection is used to handle execution of the function for all types
   * that meet our criteria, e.g. PartDef, PostDef, RequirementDef, etc.
   * We call this method from visit of the eligible usages.
   *
   * @param node           ASTSysMLType
   * @param specialization ASTSysMLSpecialization
   */
  protected void setSuperTypes(ASTSysMLType node, ASTSysMLSpecialization specialization) {
    if (specialization.sizeSuperDef() > 0) {
      ArrayList<SymTypeExpression> superTypes = new ArrayList<>();
      List<ASTMCObjectType> superDef = specialization.getSuperDefList();
      for (ASTMCObjectType objectType : superDef) {
        SymTypeExpression objType = typeCheck.symTypeFromAST(objectType);
        superTypes.add(objType);
      }
      node.getSymbol().setIsClass(true);
      node.getSymbol().setSuperTypesList(superTypes);
    }
  }

  @Override
  public void visit(ASTPartProperty node) {
    if (node.getSysMLPropertyModifier().sizeTypes() > 0) {
      this.createVariableForUsage(node, (ASTMCQualifiedType) node.getSysMLPropertyModifier().getTypes(0));
    } else {
      this.createVariableForUsage(node);
    }
  }

  @Override
  public void visit(ASTSysMLPortUsage node) {
    if (node.isPresentMCType()) {
      this.createVariableForUsage(node, (ASTMCQualifiedType) node.getMCType());
    }
  }

  @Override
  public void visit(ASTStateUsage node) {
    if (node.isPresentMCType()) {
      this.createVariableForUsage(node, (ASTMCQualifiedType) node.getMCType());
    }
  }

  @Override
  public void visit(ASTRequirementUsage node) {
    if (node.isPresentMCType()) {
      this.createVariableForUsage(node, (ASTMCQualifiedType) node.getMCType());
    }
  }

  /**
   * Following method creates a VariableSymbol for a SysMLUsage (that exists inside some definition/usage)
   * to allow TypeCheck to resolve types of bounded expressions.
   * VariableSymbol is only created if these conditions are met:
   * 1. Symbol has a name
   * 2. Symbol has an associated AST
   * 3. Symbol does not directly lie in a package
   *
   * @param node            ASTSysMLUsage
   * @param mcQualifiedType ASTMCQualifiedType
   */
  protected void createVariableForUsage(ASTSysMLUsage node, ASTMCQualifiedType mcQualifiedType) {
    if (node.getSymbol() != null
        && !node.getName().isEmpty()
        // Following two checks to verify that we are not lying directly in a package.
        && node.getEnclosingScope().isPresentAstNode()
        && !(node.getEnclosingScope().getAstNode() instanceof ASTSysMLPackage)) {
      if (mcQualifiedType != null) {
        Optional<TypeSymbol> typeSym = node.getEnclosingScope().resolveType(mcQualifiedType.getMCQualifiedName().getQName());
        typeSym.ifPresent(typeSymbol -> addVariableForUsage(node, typeSymbol));
      }
    }
  }

  /**
   * Following method creates a VariableSymbol for a SysMLUsage (that exists inside some definition/usage)
   * and does not define a type. In the process, it also creates TypeSymbol of the same name to allow the type checker
   * to resolve for type.
   * VariableSymbol is only created if these conditions are met:
   * 1. Symbol has a name
   * 2. Symbol has an associated AST
   * 3. Symbol does not directly lie in a package
   *
   * @param node ASTSysMLUsage
   */
  protected void createVariableForUsage(ASTSysMLUsage node) {
    if (node.getSymbol() != null
        && !node.getName().isEmpty()
        // Following two checks to verify that we are not lying directly in a package.
        && node.getEnclosingScope().isPresentAstNode()
        && !(node.getEnclosingScope().getAstNode() instanceof ASTSysMLPackage)) {
      // Create TypeSymbol of the same name and add in the enclosing scope.
      SysMLTypeSymbol typeSymbol = new SysMLTypeSymbolBuilder()
          // Append a randomly generated string to create anonymous type,
          // this type will only exist in the current scope.
          .setName(node.getName() + "_" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10))
          .setAstNodeAbsent()
          .setSpannedScope(node.getSpannedScope())
          .setEnclosingScope(node.getEnclosingScope()).build();
      node.getEnclosingScope().add(typeSymbol);
      addVariableForUsage(node, typeSymbol);
    }
  }

  /**
   * Creates a VariableSymbol and adds it to the node's enclosing scope.
   *
   * @param node       ASTSysMLUsage
   * @param typeSymbol TypeSymbol
   */
  private void addVariableForUsage(ASTSysMLUsage node, TypeSymbol typeSymbol) {
    // Create a variable symbol and add in the enclosing scope.
    node.getEnclosingScope().add(
        new VariableSymbolBuilder()
            .setName(node.getName())
            .setAstNodeAbsent()
            .setType(SymTypeExpressionFactory.createTypeExpression(typeSymbol))
            .setEnclosingScope(node.getEnclosingScope()).build());
  }

  @Override
  public SysMLv2Traverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SysMLv2Traverser traverser) {
    this.traverser = traverser;
  }
}
