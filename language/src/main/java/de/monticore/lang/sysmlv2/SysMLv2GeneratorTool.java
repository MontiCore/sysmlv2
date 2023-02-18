package de.monticore.lang.sysmlv2;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateController;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.lang.sysml4verification.cocos.WarnNonExhibited;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionUsageCoCo;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTConstraintDefCoCo;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTSysMLPackageCoCo;
import de.monticore.lang.sysmlinterfaces._cocos.SysMLInterfacesASTInterfaceDefCoCo;
import de.monticore.lang.sysmlinterfaces._cocos.SysMLInterfacesASTInterfaceUsageCoCo;
import de.monticore.lang.sysmlitems._cocos.SysMLItemsASTItemDefCoCo;
import de.monticore.lang.sysmlitems._cocos.SysMLItemsASTItemUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.monticore.lang.sysmlparts.coco.PortDefHasOneType;
import de.monticore.lang.sysmlparts.coco.PortDefNeedsDirection;
import de.monticore.lang.sysmlrequirements._cocos.SysMLRequirementsASTRequirementDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.cocos.ActionControlGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.ActionGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.ActionNameCoCos;
import de.monticore.lang.sysmlv2.cocos.ActionSupertypes;
import de.monticore.lang.sysmlv2.cocos.AttributeGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.ConnectionGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.InterfaceSupertypes;
import de.monticore.lang.sysmlv2.cocos.ItemsSupertypes;
import de.monticore.lang.sysmlv2.cocos.NameCompatible4Isabelle;
import de.monticore.lang.sysmlv2.cocos.OneCardinality;
import de.monticore.lang.sysmlv2.cocos.PartsGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.PartsSupertypes;
import de.monticore.lang.sysmlv2.cocos.PortsGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.StateGeneratorCoCo;
import de.monticore.lang.sysmlv2.cocos.StateNameCoCos;
import de.monticore.lang.sysmlv2.cocos.StateSupertypes;
import de.monticore.lang.sysmlv2.cocos.SuccessionCoCo;
import de.monticore.lang.sysmlv2.cocos.SuccessionReachabilityGeneratorCoCos;
import de.monticore.lang.sysmlv2.cocos.TransitionResolvableCoCo;
import de.monticore.lang.sysmlv2.generator.helper.ActionsHelper;
import de.monticore.lang.sysmlv2.generator.helper.AutomatonHelper;
import de.monticore.lang.sysmlv2.generator.helper.ComponentHelper;
import de.monticore.lang.sysmlv2.generator.SysML2CDConverter;
import de.monticore.lang.sysmlv2.symboltable.completers.ScopeNamingCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.SpecializationCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.TypesAndDirectionCompleter;
import de.monticore.lang.sysmlv2.visitor.ActionSuccessionVisitor;
import de.monticore.lang.sysmlv2.visitor.PartsTransitiveVisitor;
import de.monticore.lang.sysmlv2.visitor.StateVisitor;
import de.monticore.lang.sysmlv2.visitor.TransitionVisitor;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SysMLv2GeneratorTool extends SysMLv2ToolTOP {

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
  public void runDefaultCoCos(ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateSupertypes());
    checker.addCoCo((SysMLStatesASTStateUsageCoCo) new StateSupertypes());
    checker.addCoCo((SysMLActionsASTActionUsageCoCo) new ActionGeneratorCoCos());
    checker.addCoCo((SysMLActionsASTActionDefCoCo) new ActionGeneratorCoCos());
    checker.addCoCo((SysMLActionsASTActionDefCoCo) new ActionSupertypes());
    checker.addCoCo((SysMLActionsASTActionUsageCoCo) new ActionSupertypes());
    checker.addCoCo((SysMLInterfacesASTInterfaceDefCoCo) new InterfaceSupertypes());
    checker.addCoCo((SysMLInterfacesASTInterfaceUsageCoCo) new InterfaceSupertypes());
    checker.addCoCo((SysMLItemsASTItemDefCoCo) new ItemsSupertypes());
    checker.addCoCo((SysMLItemsASTItemUsageCoCo) new ItemsSupertypes());
    checker.addCoCo((SysMLPartsASTPartDefCoCo) new PartsSupertypes());
    checker.addCoCo((SysMLPartsASTPartUsageCoCo) new PartsSupertypes());
    // TODO Not ready for prime time. see ConstraintCoCoTest input 8_valid.sysml
    //  checker.addCoCo(new ConstraintIsBoolean());
    // TODO Erroring when checking Generics. See disabled test in SpecializationExistsTest
    //  checker.addCoCo(new SpecializationExists());
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPartDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPortDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLConstraintsASTConstraintDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLActionsASTActionDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLRequirementsASTRequirementDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLImportsAndPackagesASTSysMLPackageCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTAttributeDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLActionsASTActionDefCoCo) new ActionNameCoCos());
    checker.addCoCo((SysMLActionsASTActionUsageCoCo) new ActionNameCoCos());
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateNameCoCos());
    checker.addCoCo((SysMLStatesASTStateUsageCoCo) new StateNameCoCos());
    checker.addCoCo(new ConnectionGeneratorCoCos());
    checker.checkAll(ast);
  }

  /**
   * Formal Verification-Specific Language Implementation CoCos
   */
  @Override
  public void runAdditionalCoCos(ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new WarnNonExhibited());
    checker.addCoCo(new OneCardinality());
    checker.addCoCo(new PortDefHasOneType());
    checker.addCoCo(new PortDefNeedsDirection());
    checker.addCoCo(new ActionControlGeneratorCoCos());
    checker.addCoCo((SysMLPartsASTPortUsageCoCo) new PortsGeneratorCoCos());
    checker.addCoCo((SysMLPartsASTPortDefCoCo)new PortsGeneratorCoCos());
    checker.addCoCo((SysMLPartsASTAttributeDefCoCo) new AttributeGeneratorCoCos());
    checker.addCoCo((SysMLPartsASTAttributeUsageCoCo) new AttributeGeneratorCoCos());
    checker.addCoCo((SysMLPartsASTPartDefCoCo) new PartsGeneratorCoCos());
    checker.addCoCo((SysMLPartsASTPartUsageCoCo) new PartsGeneratorCoCos());
    checker.addCoCo((SysMLStatesASTStateUsageCoCo) new StateGeneratorCoCo());
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateGeneratorCoCo());
    checker.addCoCo((SysMLActionsASTActionUsageCoCo) new SuccessionReachabilityGeneratorCoCos());
    checker.addCoCo((SysMLActionsASTActionDefCoCo) new SuccessionReachabilityGeneratorCoCos());
    checker.addCoCo(new TransitionResolvableCoCo());
    checker.addCoCo(new SuccessionCoCo());
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
    SysMLv2Symbols2Json symbols2Json = new SysMLv2Symbols2Json();
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
    traverser.add4SysMLRequirements(completer);

    if(node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }

    node.accept(traverser);
  }

  @Override
  public void run(String[] args) {

    //transform(ast);

    init();
    org.apache.commons.cli.Options options = initOptions();
    try {
      //create CLI Parser and parse input options from commandline
      org.apache.commons.cli.CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
      org.apache.commons.cli.CommandLine cmd = cliparser.parse(options, args);

      //help: when --help
      if(cmd.hasOption("h")) {
        printHelp(options);
        //do not continue, when help is printed.
        return;
      }
      //version: when --version
      else if(cmd.hasOption("v")) {
        printVersion();
        //do not continue when help is printed
        return;
      }
      ISysMLv2ArtifactScope modelTopScope = null;
      if(!cmd.hasOption("i")) {
        Log.error("Please specify only one single path to the input model");
      }
      else {
        final Optional<ASTSysMLModel> ast;
        try {

          Log.info(System.getProperty("user.dir") + "\\" + cmd.getOptionValue("i").trim(),
              SysMLv2GeneratorTool.class.getName());
          //1. Parse input
          Path model = Paths.get(cmd.getOptionValue("i"));
          ast = SysMLv2Mill.parser().parse(model.toString());
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
        if(ast.isPresent()) {
          //2. Build symboltable
          modelTopScope = createSymbolTable(ast.get());
          modelTopScope.setName(cmd.getOptionValue("i").substring(cmd.getOptionValue("i").lastIndexOf("/") + 1,
              cmd.getOptionValue("i").lastIndexOf(".")));
          //3. Run some cocos
          runDefaultCoCos(ast.get());
          //4. run Transformations
          transform(ast.get());
          //5. run additional CoCos
          runAdditionalCoCos(ast.get());

          String outputDir = cmd.hasOption("o")
              ?
              cmd.getOptionValue("o")
              :
              "target/gen-test/" + "SysMLGeneration"; //TODO richtigen Namen suchen
          Path model = Paths.get(cmd.getOptionValue("i"));

          String fileName = FilenameUtils.removeExtension(model.toFile().getName());
          generateCD(ast.get(), outputDir, fileName);

        }
      }
      if(cmd.hasOption("s")) {
        if(modelTopScope != null) {
          storeSymbols(modelTopScope, cmd.getOptionValue("s"));
        }
      }

    }
    catch (org.apache.commons.cli.ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xA5C06x33289 Could not process SysMLv2Tool parameters: " + e.getMessage());
    }
  }

  public void transform(ASTSysMLModel ast) {
    transformSuccession(ast);
    transformTransitions(ast);
    transformStates(ast);
    transformTransitiveSupertypes(ast);
  }

  public void transformTransitiveSupertypes(ASTSysMLModel ast) {
    PartsTransitiveVisitor partsTransitiveVisitor = new PartsTransitiveVisitor();
    SysMLv2Traverser sysMLv2Traverser = getTraverser();
    sysMLv2Traverser.add4SysMLParts(partsTransitiveVisitor);
    sysMLv2Traverser.handle(ast);
  }

  public void transformSuccession(ASTSysMLModel ast) {
    ActionSuccessionVisitor actionSuccessionVisitor = new ActionSuccessionVisitor();
    SysMLv2Traverser sysMLv2Traverser = getTraverser();
    sysMLv2Traverser.add4SysMLActions(actionSuccessionVisitor);
    sysMLv2Traverser.handle(ast);
  }
  public void transformStates(ASTSysMLModel ast) {
    StateVisitor stateVisitorveVisitor = new StateVisitor();
    SysMLv2Traverser sysMLv2Traverser = getTraverser();
    sysMLv2Traverser.add4SysMLStates(stateVisitorveVisitor);
    sysMLv2Traverser.handle(ast);
  }
  public void transformTransitions(ASTSysMLModel ast) {
    TransitionVisitor transitionVisitor = new TransitionVisitor();
    SysMLv2Traverser sysMLv2Traverser = getTraverser();
    sysMLv2Traverser.add4SysMLStates(transitionVisitor);
    sysMLv2Traverser.handle(ast);
  }
  public void generateCD(ASTSysMLModel ast, String outputDir, String fileName) {

    GeneratorSetup setup = new GeneratorSetup();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("autHelper", new AutomatonHelper());
    glex.setGlobalValue("actionsHelper", new ActionsHelper());
    glex.setGlobalValue("compHelper", new ComponentHelper());
    setup.setGlex(glex);
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());

    if(!outputDir.isEmpty()) {
      File targetDir = new File(outputDir);
      setup.setOutputDirectory(targetDir);
    }

    String configTemplate = "sysml2cd.SysML2CD";
    TemplateController tc = setup.getNewTemplateController(configTemplate);
    CDGenerator generator = new CDGenerator(setup);
    TemplateHookPoint hpp = new TemplateHookPoint(configTemplate);
    List<Object> configTemplateArgs;
    // select the conversion variant:
    SysML2CDConverter converter = new SysML2CDConverter();
    configTemplateArgs = Arrays.asList(glex, converter, setup.getHandcodedPath(), generator, fileName);

    hpp.processValue(tc, ast, configTemplateArgs);
  }
}
