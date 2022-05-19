package de.monticore.lang.sysmlcommons._symboltable;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysmlbasis._symboltable.SysMLUsageSymbol;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTSysMLPortUsage;
import de.monticore.lang.sysmlblockdiagrams._symboltable.SysMLPortUsageSymbol;
import de.monticore.lang.sysmlcommons.SysMLCommonsMill;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlexpressions._ast.ASTSysMLFieldAccessExpression;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SysMLCommonsScopesGenitor extends SysMLCommonsScopesGenitorTOP {

  @Override
  public void visit(ASTSysMLParameter node) {
    super.visit(node);
    if(node.isPresentSymbol()) {
      Optional<TypeSymbol> typeSymbol;
      if(node.isPresentMCType()) {
        // type is provided
        String typeName;
        // If the type is mcQualifiedType, then look for it in the enclosing scope.
        if(node.getMCType() instanceof ASTMCQualifiedType) {
          typeName = ((ASTMCQualifiedType) node.getMCType()).getMCQualifiedName().getQName();
          typeSymbol = node.getEnclosingScope().resolveType(typeName);
        }
        // Otherwise, it's basic mcType, then look for it in the global scope.
        else {
          typeName = node.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
          typeSymbol = SysMLv2Mill.globalScope().resolveType(typeName);
        }

        typeSymbol.ifPresent(symbol -> node.getSymbol().setType(SymTypeExpressionFactory.createTypeExpression(symbol)));
      }
      else if(node.isPresentBinding()) {
        // type is inferred from binding
        if(node.getBinding() instanceof ASTFieldAccessExpression) {
          ASTFieldAccessExpression binding = (ASTFieldAccessExpression) node.getBinding();
          String portUsageName = ((ASTNameExpression)binding.getExpression()).getName();
          String portField = binding.getName();

          SysMLUsageSymbol symbol = node.getEnclosingScope().resolveSysMLUsage(portUsageName).get();
          // Usage in same scope thus still has ast info
          if(symbol instanceof SysMLPortUsageSymbol) {
            ASTMCType mcType = ((ASTSysMLPortUsage) symbol.getAstNode()).getMCType();
            String portDefName = null;
            if(mcType instanceof ASTMCQualifiedType) {
              portDefName = ((ASTMCQualifiedType) mcType).getMCQualifiedName().getQName();
            }
            Optional<SysMLTypeSymbol> portDefSymbol = node.getEnclosingScope().resolveSysMLType(portDefName);
            if(portDefSymbol.isPresent()) {
              Optional<FieldSymbol> portFieldSymbol = portDefSymbol.get().getSpannedScope().resolveFieldLocally(portField);
              node.getSymbol().setType(portFieldSymbol.get().getType());
            }
          }
        }
      }
    }
  }
}
