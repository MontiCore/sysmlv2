package de.monticore.lang.sysmlv2;

import de.monticore.lang.sysml4verification.cocos.WarnNonExhibited;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTConstraintDefCoCo;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTSysMLPackageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlparts.coco.PortDefHasOneType;
import de.monticore.lang.sysmlparts.coco.PortDefNeedsDirection;
import de.monticore.lang.sysmlrequirements._cocos.SysMLRequirementsASTRequirementDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTDoActionCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTExitActionCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlstates.cocos.NoDoActions;
import de.monticore.lang.sysmlstates.cocos.NoExitActions;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2.symboltable.completers.ScopeNamingCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.SpecializationCompleter;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.lang.sysmlv2.symboltable.completers.TypesAndDirectionCompleter;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.lang.sysmlv2.cocos.NameCompatible4Isabelle;
import de.monticore.lang.sysmlv2.cocos.OneCardinality;
import de.monticore.lang.sysmlv2.cocos.SpecializationExists;
import de.monticore.lang.sysmlv2.cocos.StateSupertypes;

public class SysMLv2Tool extends SysMLv2ToolTOP {

  @Override
  public void init() {
    super.init();
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addStringType();
    SysMLv2Mill.addStreamType();
    SysMLv2Mill.addCollectionTypes();
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
    checker.addCoCo(new SpecializationExists());
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPartDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPortDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLConstraintsASTConstraintDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLActionsASTActionDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLRequirementsASTRequirementDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLImportsAndPackagesASTSysMLPackageCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTAttributeDefCoCo) new NameCompatible4Isabelle());
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
    checker.addCoCo(new OneCardinality());
    checker.addCoCo(new NoExitActions());
    checker.addCoCo(new NoDoActions());
    checker.addCoCo(new PortDefHasOneType());
    checker.addCoCo(new PortDefNeedsDirection());
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

  public ISysMLv2ArtifactScope loadSymbols(String symbolPath) {
    SysMLv2Symbols2Json symbols2Json = new de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json();
    var artifactScope = symbols2Json.load(symbolPath);

    getGlobalScope().addSubScope(artifactScope);

    return artifactScope;
  }

  @Override
  public void completeSymbolTable(ASTSysMLModel node) {
    SysMLv2Traverser traverser = SysMLv2Mill.traverser();

    traverser.add4SysMLv2(new ScopeNamingCompleter());

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

    // Phase 2: Requires that all scopes have a name (done in phase 1).
    // reset traverser
    traverser = SysMLv2Mill.inheritanceTraverser();

    traverser.add4SysMLBasis(new SpecializationCompleter());

    if(node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }

    node.accept(traverser);

    // Phase 3: Sets types for usages. Bases on types of specializations completed in phase 1.
    traverser = SysMLv2Mill.traverser();

    TypesAndDirectionCompleter completer = new TypesAndDirectionCompleter();
    traverser.add4SysMLBasis(completer);
    traverser.add4SysMLParts(completer);

    if(node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }

    node.accept(traverser);
  }
}
