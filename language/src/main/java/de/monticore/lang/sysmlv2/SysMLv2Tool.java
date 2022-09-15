package de.monticore.lang.sysmlv2;

import de.monticore.lang.sysml4verification.cocos.WarnNonExhibited;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2SymboltableCompleter;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.lang.sysmlv2.cocos.StateSupertypes;

public class SysMLv2Tool extends SysMLv2ToolTOP {

  @Override
  public void init() {
    super.init();
    SysMLv2Mill.globalScope().clear();
  }

  /**
   * Official Language Implementation CoCos
   */
  @Override
  public  void runDefaultCoCos (de.monticore.lang.sysmlv2._ast.ASTSysMLModel ast)
  {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateSupertypes());
    checker.addCoCo((SysMLStatesASTStateUsageCoCo) new StateSupertypes());
    checker.addCoCo(new ConstraintIsBoolean());
    checker.checkAll(ast);
  }

  /**
   * Formal Verification-Specific Language Implementation CoCos
   */
  @Override
  public  void runAdditionalCoCos (de.monticore.lang.sysmlv2._ast.ASTSysMLModel ast)
  {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new WarnNonExhibited());
    checker.checkAll(ast);
  }

  public ISysMLv2GlobalScope getGlobalScope() {
    return SysMLv2Mill.globalScope();
  }

  public SysMLv2Traverser getTraverser() {
    return SysMLv2Mill.traverser();
  }

  public SysMLv2Traverser getInheritanceTraverser() {
    return SysMLv2Mill.inheritanceTraverser();
  }

  @Override
  public void completeSymbolTable(ASTSysMLModel node) {
    SysMLv2Traverser traverser = SysMLv2Mill.traverser();

    SysMLv2SymboltableCompleter completer = new SysMLv2SymboltableCompleter();
    traverser.add4SysMLBasis(completer);
    traverser.add4SysMLParts(completer);
    traverser.add4SysMLv2(completer);

    // Aus mir unerklärlichen Gründen ist die Traversierung so implementiert, dass nur das SpannedScope des jeweiligen
    // AST-Knoten visitiert wird. Wenn wir hier also das ASTSysMLModel reinstecken (was kein Scope spannt (wieso eigtl.
    // nicht?)), dann werden die Elements visitiert/traversiert. Beim traverisieren wird dann auch das gespannte Scope
    // (zB. das Scope einer PortDef) visitiert. Das ArtifactScope steht in dieser Hierarchie aber über dem ASTSysMLModel
    // und man muss hier allen Ernstes einmal RAUF navigieren und dann den Traverser loslassen.
    if(node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }

    // Und dann wird nicht das Scope traversiert um die darin gefindlichen Symbole und daranhängende AST-Knoten zu
    // besuchen, sondern es wird nichts getan. Der Default-Traverser geht nämlich davon aus, dass man sich am AST
    // entlang hangelt.
    node.accept(traverser);
  }
}
