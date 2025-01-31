package de.monticore.lang.sysmlparts.symboltable.completers;

import de.monticore.lang.sysmlbasis.symboltable.DirectionAccessModifier;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.CompoundAccessModifier;

import java.util.ArrayList;

/**
 * Siehe DirectionAccessModifier für Schritt 1
 *
 * 2. ST-Completer erstellen (Eigenkreation)
 * 2.1. Registrieren: siehe SysMLv2Tool.completeSymbolTable()
 */
public class AddDirectionToAttributeUsages implements SysMLPartsVisitor2 {

  @Override
  public void visit(ASTAttributeUsage ast) {
    var modifier = new ArrayList<AccessModifier>();
    if(ast.getModifier().isIn()) {
      modifier.add(DirectionAccessModifier.IN);
    }
    if(ast.getModifier().isOut()) {
      modifier.add(DirectionAccessModifier.OUT);
    }
    if(ast.getModifier().isInout()) {
      modifier.add(DirectionAccessModifier.INOUT);
    }
    ast.getSymbol().setAccessModifier(new CompoundAccessModifier(modifier));
  }

}
