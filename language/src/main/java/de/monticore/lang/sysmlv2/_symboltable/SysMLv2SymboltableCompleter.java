package de.monticore.lang.sysmlv2._symboltable;

import de.monticore.lang.sysmlbasis._ast.ASTSpecialization;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlbasis._visitor.SysMLBasisVisitor2;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._symboltable.AttributeUsageSymbol;
import de.monticore.lang.sysmlparts._symboltable.PortUsageSymbol;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.types.SysMLBasisTypesFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.List;
import java.util.stream.Collectors;

public class SysMLv2SymboltableCompleter implements SysMLBasisVisitor2, SysMLPartsVisitor2 {

  /**
   * Returns type completion for Usages. The type is not resolved, and we only store the qualified name as SymTypeExpression.
   */
  private List<SymTypeExpression> getTypeCompletion(List<ASTSpecialization> specializationList, boolean conjugated) {
    return specializationList
        .stream()
        .filter(astSpecialization -> astSpecialization instanceof ASTSysMLTyping &&
            ((ASTSysMLTyping) astSpecialization).isConjugated() == conjugated)
        .flatMap(astTyping ->
            astTyping
                .getSuperTypesList()
                .stream()
                .map(astmcQualifiedName ->
                    // set enclosing scope to globalscope to force the fully qualified name to the provided value
                    (SymTypeExpression) SymTypeExpressionFactory.createTypeObject(
                        astmcQualifiedName.printType(new SysMLBasisTypesFullPrettyPrinter(new IndentPrinter())),
                        SysMLv2Mill.globalScope()
                    )
                )
        )
        .collect(Collectors.toList());
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

  @Override
  public void visit(ASTAttributeUsage node) {
    if(node.isPresentSymbol()) {
      AttributeUsageSymbol symbol = node.getSymbol();
      // type
      List<SymTypeExpression> types = getTypeCompletion(node.getSpecializationList(), false);

      symbol.setTypesList(types);

      // feature direction
      if(node.isPresentSysMLFeatureDirection()) {
        symbol.setDirection(node.getSysMLFeatureDirection());
      }
      else {
        symbol.setDirection(ASTSysMLFeatureDirection.IN);
      }
    }
  }
}
