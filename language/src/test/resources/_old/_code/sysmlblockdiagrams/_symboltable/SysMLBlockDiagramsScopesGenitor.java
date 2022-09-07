package de.monticore.lang.sysmlblockdiagrams._symboltable;

import de.monticore.lang.sysmlblockdiagrams.SysMLBlockDiagramsMill;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTStateExhibition;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTSysMLAttribute;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolBuilder;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SysMLBlockDiagramsScopesGenitor extends SysMLBlockDiagramsScopesGenitorTOP {

  private int counterConnectionUsage = 1;

  private int counterPartProperty = 1;

  private int counterConnectionDefEnd = 1;

  private int counterStateExhibition = 1;

  /**
   * Method overridden to set type in field symbol (original method only creates field symbol with name).
   */
  @Override
  public void visit(ASTSysMLAttribute node) {
    super.visit(node);
    if(node.isPresentSymbol()) {
      // Extract type info. from the node and resolve for type symbol.
      Optional<TypeSymbol> typeSymbol = Optional.empty();
      if(node.getSysMLPropertyModifier().sizeTypes() > 0) {
        ASTMCType type = node.getSysMLPropertyModifier().getTypes(0);

        String typeName;

        if(node.getSysMLPropertyModifier().getTypes(0) instanceof ASTMCQualifiedType) {
          // Type is an Object, could also be a default SysML type
          typeName = ((ASTMCQualifiedType) type).getMCQualifiedName().getQName();
          typeSymbol = node.getEnclosingScope().resolveType(typeName);
        }
        else {
          // Otherwise, it's basic mcType, then look for it in the global scope.
          typeName = node.getSysMLPropertyModifier().getTypes(0)
              .printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
          typeSymbol = SysMLv2Mill.globalScope().resolveType(typeName);
        }
      }

      typeSymbol.ifPresent(value -> node.getSymbol().setType(SymTypeExpressionFactory.createTypeExpression(value)));
    }
  }

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTConnectionUsage node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed ConncectionUsage" + counterConnectionUsage);
      counterConnectionUsage += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }

  @Override
  public void visit(ASTStateExhibition node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed StateExhibition" + counterStateExhibition);
      counterStateExhibition += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTPartProperty node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed PartProperty" + counterPartProperty);
      counterPartProperty += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }

  @Override
  public void visit(de.monticore.lang.sysmlblockdiagrams._ast.ASTConnectionDefEnd node) {
    // Name Symbol if it is unnamed. Otherwise scopes will be set incorrect. See issue #23
    if (!node.isPresentName()) {
      node.setName("Unnamed ConnectionDefEnd" + counterConnectionDefEnd);
      counterConnectionDefEnd += 1;
    }

    // Call super method to continue execution of previous logic.
    super.visit(node);
  }
}
