package de.monticore.lang.sysmlcommons._symboltable;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.Optional;

public class SysMLCommonsScopesGenitor extends SysMLCommonsScopesGenitorTOP {

  @Override
  public void visit(ASTSysMLParameter node) {
    super.visit(node);
    if(node.isPresentSymbol()) {
      Optional<TypeSymbol> typeSymbol;
      // Set type of SysMLParameter from the provided type.
      if(node.isPresentMCType()) {
        String typeName;
        // If the type is mcQualifiedType, then look fot it in the enclosing scope.
        if(node.getMCType() instanceof ASTMCQualifiedType) {
          typeName = ((ASTMCQualifiedType) node.getMCType()).getMCQualifiedName().getQName();
          typeSymbol = node.getEnclosingScope().resolveType(typeName);
        }
        // Otherwise, it's basic mcType, then look for it in the global scope.
        else {
          typeName = node.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
          typeSymbol = SysMLv2Mill.globalScope().resolveType(typeName);
        }
        typeSymbol.ifPresent(value -> node.getSymbol().setType(SymTypeExpressionFactory.createTypeExpression(value)));
      }
    }
  }
}
