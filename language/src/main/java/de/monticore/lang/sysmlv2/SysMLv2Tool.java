/* (c) https://github.com/MontiCore/monticore */
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
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlstates.cocos.NoDoActions;
import de.monticore.lang.sysmlstates.cocos.NoExitActions;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.lang.sysmlv2.cocos.NameCompatible4Isabelle;
import de.monticore.lang.sysmlv2.cocos.OneCardinality;
import de.monticore.lang.sysmlv2.cocos.SpecializationExists;
import de.monticore.lang.sysmlv2.cocos.StateSupertypes;
import de.monticore.lang.sysmlv2.cocos.TypeCheckTransitionGuards;
import de.monticore.lang.sysmlv2.symboltable.completers.SpecializationCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.TypesAndDirectionCompleter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class SysMLv2Tool extends SysMLv2ToolTOP {

  @Override
  public void init() {
    super.init();
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.initializePrimitives();
    SysMLv2Mill.addStringType();
    SysMLv2Mill.addCollectionTypes();
    SysMLv2Mill.addStreamType();
  }

  /**
   * Official Language Implementation CoCos
   */
  @Override
  public  void runDefaultCoCos(ASTSysMLModel ast)
  {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateSupertypes());
    checker.addCoCo((SysMLStatesASTStateUsageCoCo) new StateSupertypes());
    checker.addCoCo(new SpecializationExists());
    checker.addCoCo(new ConstraintIsBoolean());
    checker.addCoCo(new TypeCheckTransitionGuards());
    checker.checkAll(ast);
  }

  /**
   * Formal Verification-Specific Language Implementation CoCos
   */
  @Override
  public  void runAdditionalCoCos (de.monticore.lang.sysmlv2._ast.ASTSysMLModel ast)
  {
    var checker = new SysMLv2CoCoChecker();

    // Not-supported language elements
    checker.addCoCo(new NoExitActions());
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new NoDoActions());
    checker.addCoCo((SysMLStatesASTStateUsageCoCo) new NoDoActions());

    // Errors regarding correctness of models, i.e., following "the MontiBelle approach"
    checker.addCoCo(new OneCardinality());
    checker.addCoCo(new PortDefHasOneType());
    checker.addCoCo(new PortDefNeedsDirection());

    // Additional warnings, things might be ignored
    checker.addCoCo(new WarnNonExhibited());

    // Check names to be compatible with Isabelle names
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
    // Phase 1
    SysMLv2Traverser traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(new SpecializationCompleter());

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

    // Phase 2: Sets types for usages and declarations (let-in, quantifiers) in expressions.
    // Based on types of specializations completed in phase 1.
    traverser = SysMLv2Mill.traverser();

    TypesAndDirectionCompleter completer = new TypesAndDirectionCompleter();
    traverser.add4SysMLBasis(completer);
    traverser.add4SysMLParts(completer);
    traverser.add4SysMLRequirements(completer);
    // TODO Currently breaks for MontiBelle Expressions in Data Link Upload Feed
    /*
    // null parameters since we don't really understand any of those (yet)
    var oclCompleter = new OCLExpressionsSymbolTableCompleter(null, null);
    oclCompleter.setDeriver(new SysMLExpressionsDeriver(true));
    oclCompleter.setSynthesizer(new SysMLSynthesizer());
    traverser.add4OCLExpressions(oclCompleter);
    */

    // gleiches Spiel wie oben: Alles besuchen verlangt zwei Calls
    if(node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }
    node.accept(traverser);
  }

  // MontiCore generiert hier leider herzlich wenig Sinnvolles
  @Override
  public  void run (String[] args) {
    init();
    Options options = initOptions();

    try{
      CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
      CommandLine cmd = cliparser.parse(options,args);

      if(cmd.hasOption("help")){
        printHelp(options);
        return;
      }
      else if(cmd.hasOption("version")){
        printVersion();
        return;
      }
      else if(cmd.hasOption("input")){
        check(Path.of(cmd.getOptionValue("input")));
        return;
      }

    } catch (org.apache.commons.cli.ParseException e) {
      Log.error("0xA5C06x33289 Could not process SysMLv2Tool parameters: " + e.getMessage());
    }
  }

  public void check(Path models) {
    try {
      var asts = Files.walk(models)
          .filter(p -> FilenameUtils.getExtension(p.toString()).equals("sysml"))
          .map(p -> parse(p.toString()))
          .collect(Collectors.toList());

      asts.forEach(it -> createSymbolTable(it));
      asts.forEach(it -> completeSymbolTable(it));

      asts.forEach(it -> runDefaultCoCos(it));
      asts.forEach(it -> runAdditionalCoCos(it));
    }
    catch (IOException ex) {
      Log.error("Could not read the input directory: " + ex.getMessage());
    }
  }

}
