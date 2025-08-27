/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2;

import de.monticore.lang.componentconnector.SerializationUtil;
import de.monticore.lang.sysmlactions._cocos.SysMLActionsASTActionDefCoCo;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTConstraintDefCoCo;
import de.monticore.lang.sysmlimportsandpackages._cocos.SysMLImportsAndPackagesASTSysMLPackageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartDefCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortDefCoCo;
import de.monticore.lang.sysmlparts.coco.PortDefHasOneType;
import de.monticore.lang.sysmlparts.coco.PortDefNeedsDirection;
import de.monticore.lang.sysmlparts.symboltable.completers.ConvertEnumUsagesToFields;
import de.monticore.lang.sysmlconstraints._cocos.SysMLConstraintsASTRequirementDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateDefCoCo;
import de.monticore.lang.sysmlstates._cocos.SysMLStatesASTStateUsageCoCo;
import de.monticore.lang.sysmlstates.cocos.NoDoActions;
import de.monticore.lang.sysmlstates.cocos.NoExitActions;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._prettyprint.SysMLv2FullPrettyPrinter;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.lang.sysmlv2.cocos.AssignActionTypeCheck;
import de.monticore.lang.sysmlv2.cocos.ConstraintIsBoolean;
import de.monticore.lang.sysmlv2.cocos.FlowCheckCoCo;
import de.monticore.lang.sysmlv2.cocos.NameCompatible4Isabelle;
import de.monticore.lang.sysmlv2.cocos.OneCardinality;
import de.monticore.lang.sysmlv2.cocos.PartBehaviorCoCo;
import de.monticore.lang.sysmlv2.cocos.PortDefinitionExistsCoCo;
import de.monticore.lang.sysmlv2.cocos.RefinementCyclic;
import de.monticore.lang.sysmlv2.cocos.SendActionTypeCheck;
import de.monticore.lang.sysmlv2.cocos.SpecializationExists;
import de.monticore.lang.sysmlv2.cocos.StateSupertypes;
import de.monticore.lang.sysmlv2.cocos.TypeCheckTransitionGuards;
import de.monticore.lang.sysmlv2.cocos.WarnNonExhibited;
import de.monticore.lang.sysmlv2.symboltable.completers.CausalityCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.DirectRefinementCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.DirectionCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.RequirementClassificationCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.SpecializationCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.StateExhibitionCompleter;
import de.monticore.lang.sysmlv2.symboltable.completers.TypesCompleter;
import de.monticore.lang.sysmlv2.types.SysMLDeriver;
import de.monticore.lang.sysmlv2.types.SysMLSynthesizer;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.units.qual.A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class SysMLv2Tool extends SysMLv2ToolTOP {

  @Override
  public void init() {
    super.init();
    SysMLv2Mill.globalScope().clear();
    SysMLv2Mill.prepareGlobalScope();
    loadStreamSymbolsFromJar();
  }

  @Override
  public void prettyPrint(ASTSysMLModel ast, String file) {
    if (file == null) {
      // Print to stdout if no path is supplied.
      var prettyPrinter = new SysMLv2FullPrettyPrinter(new IndentPrinter());
      String printed = prettyPrinter.prettyprint(ast);
      System.out.println(printed);
    }
    else {
      super.prettyPrint(ast, file);
    }
  }

  /**
   * Official Language Implementation CoCos
   */
  @Override
  public void runDefaultCoCos(ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new StateSupertypes());
    checker.addCoCo((SysMLStatesASTStateUsageCoCo) new StateSupertypes());
    checker.addCoCo(new SpecializationExists());
    checker.addCoCo(new ConstraintIsBoolean());
    checker.addCoCo(new TypeCheckTransitionGuards());
    checker.addCoCo(new SendActionTypeCheck());
    checker.addCoCo(new AssignActionTypeCheck());
    checker.checkAll(ast);
  }

  /**
   * CoCos assuring models are fit for semantic analysis using MontiBelle
   */
  @Override
  public void runAdditionalCoCos(
      de.monticore.lang.sysmlv2._ast.ASTSysMLModel ast) {
    var checker = new SysMLv2CoCoChecker();

    // Not-supported language elements
    checker.addCoCo(new NoExitActions());
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new NoDoActions());
    checker.addCoCo((SysMLStatesASTStateUsageCoCo) new NoDoActions());

    // Restrictions on language Elements
    checker.addCoCo(new OneCardinality());
    checker.addCoCo(new PortDefHasOneType());
    checker.addCoCo(new PortDefNeedsDirection());
    checker.addCoCo(new RefinementCyclic());

    // Additional warnings, things might be ignored
    checker.addCoCo(new WarnNonExhibited());

    // Check names to be compatible with Isabelle names
    checker.addCoCo((SysMLStatesASTStateDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPartDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo((SysMLPartsASTPortDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo(
        (SysMLConstraintsASTConstraintDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo(
        (SysMLActionsASTActionDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo(
        (SysMLConstraintsASTRequirementDefCoCo) new NameCompatible4Isabelle());
    checker.addCoCo(
        (SysMLImportsAndPackagesASTSysMLPackageCoCo) new NameCompatible4Isabelle());
    checker.addCoCo(
        (SysMLPartsASTAttributeDefCoCo) new NameCompatible4Isabelle());

    // SpesML CoCos
    checker.addCoCo(new FlowCheckCoCo());
    checker.addCoCo(new PortDefinitionExistsCoCo());
    checker.addCoCo(new PartBehaviorCoCo());

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
    SysMLv2Symbols2Json symbols2Json =
        new de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json();
    var artifactScope = symbols2Json.load(symbolPath);

    getGlobalScope().addSubScope(artifactScope);

    return artifactScope;
  }

  @Override
  public void completeSymbolTable(ASTSysMLModel node) {
    // Phase 1
    SysMLv2Traverser traverser = SysMLv2Mill.inheritanceTraverser();
    traverser.add4SysMLBasis(new SpecializationCompleter());
    traverser.add4SysMLBasis(new DirectionCompleter());
    traverser.add4SysMLParts(new DirectionCompleter());
    traverser.add4SysMLParts(new ConvertEnumUsagesToFields());

    // Visiting artifact scope _and_ the AST requires two calls
    if (node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }
    node.accept(traverser);

    // Phase 2: Sets types for usages and declarations (let-in, quantifiers) in
    // expressions. Based on types of specializations completed in phase 1.
    traverser = SysMLv2Mill.traverser();
    TypesCompleter completer = new TypesCompleter();
    traverser.add4SysMLBasis(completer);
    traverser.add4SysMLParts(completer);
    traverser.add4SysMLConstraints(completer);

    traverser.add4SysMLParts(new RequirementClassificationCompleter());
    traverser.add4SysMLParts(new DirectRefinementCompleter());
    traverser.add4SysMLParts(new CausalityCompleter());
    traverser.add4SysMLStates(new StateExhibitionCompleter());

    // Visiting artifact scope _and_ the AST requires two calls
    if (node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }
    node.accept(traverser);
  }

  /**
   * Da der OCL-Completer davon ausgeht, dass alle anderen Symbole vorhanden
   * sind, muss dieser in einer Extra-Etappe laufen. Die Vervollständigung
   * beschäftigt sich mit Let-ins und Quantifizierten Variablen.
   * <p>
   * Beispiel: "forall v in input.data.values(): ..."
   * <p>
   * Dabei versucht der Completer das Feld (den Kanal) "data" in "input" (Port)
   * zu finden. Dieses Feld wird als Variable adaptiert von unserem SysMLv2Scope
   * geliefert. Allerdings klappt das nur, wenn das Attribut seinen Typen
   * bereits kennt. Da dieses aber auch im Completer gesetzt wird, können wir
   * die Reihenfolge nicht garantieren.
   * <p>
   */
  public void finalizeSymbolTable(ASTSysMLModel node) {
    var traverser = SysMLv2Mill.traverser();
    var oclCompleter = new OCLExpressionsSymbolTableCompleter();
    // The "true" here assumes, that OCL-Expr. are only ever used in
    // history-oriented constraints
    oclCompleter.setDeriver(new SysMLDeriver(true));
    oclCompleter.setSynthesizer(new SysMLSynthesizer());
    traverser.add4OCLExpressions(oclCompleter);

    // Visiting artifact scope _and_ the AST requires two calls
    if (node.getEnclosingScope() != null) {
      node.getEnclosingScope().accept(traverser);
    }
    node.accept(traverser);
  }

  public Options addAdditionalOptions(Options options) {
    options.addOption(Option
        .builder("ex")
        .longOpt("extended")
        .desc("Run additional checks to assure well-defined semantics")
        .build());

    options.addOption(Option
        .builder("cc")
        .longOpt("compcon")
        .desc("Serialize the symbol table of the given artifact using "
            + "component-connector symbols.")
        .hasArg(true)
        .optionalArg(false)
        .argName("output file")
        .build());

    options.addOption(Option
        .builder("nc")
        .longOpt("nococo")
        .desc("Only parse the file, dont check cocos")
        .build());

    options.addOption(Option
        .builder("log")
        .longOpt("log_parser_results")
        .desc("When iterating a dir, print successfully parseable models")
        .build());

    options.addOption(Option
        .builder("ia")
        .longOpt("interactive")
        .desc("When iterating a dir, print which models were parsable")
        .build());

    return options;
  }

  private void runwrap(CommandLine cmd, Options options) {
    if (cmd.hasOption("help")) {
      printHelp(options);
      return;
    }
    else if (cmd.hasOption("version")) {
      printVersion();
      return;
    }
    else {
      // We process input, be it via file/folder (--input) or STDIN
      List<ASTSysMLModel> asts;
      if (cmd.hasOption("input")) {
        var input = Path.of(cmd.getOptionValue("input"));
        if (Files.isDirectory(input)) {
          // New Option to print parser results to cmd line in case of
          // directory walking
          if (cmd.hasOption("log_parser_results")) {
            // parsed asts
            asts = new ArrayList<>();
            // store success
            List<Boolean> successes = new ArrayList<>();

            try {
              List<Path> files = Files.walk(input).filter(
                  p -> FilenameUtils.getExtension(p.toString()).equals(
                      "sysml")).collect(Collectors.toList());

              for (Path file : files) {
                Log.clearFindings();
                asts.add(parse(file.toString()));
                if (Log.getFindings().size() > 0) {
                  successes.add(false);
                }
                else {
                  successes.add(true);
                }
              }
              for (int i = 0; i < successes.size(); i++) {
                String template = "[PARSER_LOG]%s::%b";

                System.out.println(
                    String.format(template, files.get(i).toString(),
                        successes.get(i)));
              }

            }
            catch (IOException ex) {
              Log.error("0x00001 Could not read the input directory: "
                  + ex.getMessage());
              return;
            }
          }
          else {
            try {
              asts = Files.walk(input).filter(
                  p -> FilenameUtils.getExtension(p.toString()).equals(
                      "sysml")).map(p -> parse(p.toString())).collect(
                  Collectors.toList());
            }
            catch (IOException ex) {
              Log.error("0x00001 Could not read the input directory: "
                  + ex.getMessage());
              return;
            }
          }
        }
        else {
          asts = List.of(parse(cmd.getOptionValue("input")));
        }
      }
      else {
        var modelReader = new BufferedReader(new InputStreamReader(System.in));
        try {
          asts = List.of(SysMLv2Mill.parser().parse(modelReader).get());
        }
        catch (IOException ex) {
          Log.error(
              "0x00002 Could not read standard input: " + ex.getMessage());
          return;
        }
      }

      asts.forEach(it -> createSymbolTable(it));
      asts.forEach(it -> completeSymbolTable(it));
      asts.forEach(it -> finalizeSymbolTable(it));

      if (!cmd.hasOption("nococo")) {
        asts.forEach(it -> runDefaultCoCos(it));
        if (cmd.hasOption("extended")) {
          asts.forEach(it -> runAdditionalCoCos(it));
        }
      }

      if (cmd.hasOption("prettyprint")) {
        String target = cmd.getOptionValue("prettyprint");
        asts.forEach(it -> prettyPrint(it, target));
      }

      if (cmd.hasOption("symboltable")) {
        Log.warn("0xA0003 Not implemented yet.");
      }
      if (cmd.hasOption("compcon")) {
        // Setup the serialization to produce base symbols
        SerializationUtil.setupComponentConnectorSerialization();

        // Gather PartDefs into a new Scope
        var artifact = SysMLv2Mill.artifactScope();
        var extractor = new SerializationUtil.PartDefExtractor(artifact);
        var traverser = getTraverser();
        traverser.add4SysMLParts(extractor);
        asts.stream().forEach(s -> s.accept(traverser));

        // Store to file
        storeSymbols(artifact, cmd.getOptionValue("compcon"));
      }
      if (cmd.hasOption("report")) {
        Log.warn("0xA0004 Not implemented yet.");
      }
    }

  }

  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();

    try {
      CommandLineParser cliparser = new DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      if (cmd.hasOption("interactive")) {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in));
        String inp = "";
        while (true) {
          runwrap(cmd, options);
          System.out.println("Run again?");
          try {
            inp = br.readLine();
          }
          catch (IOException ex) {
            System.exit(-1);
          }
          if (inp.charAt(0) == 'y' || inp.charAt(0) == 'Y') {
            System.out.println("Ok, running again :)");
            runwrap(cmd, options);
          }
          else {
            System.exit(0);
          }
        }
      }
      else {
        runwrap(cmd, options);
      }
    }
    catch (ParseException e) {
      Log.error("0xA5C06x33289 Could not process SysMLv2Tool parameters: "
          + e.getMessage());
    }
  }

  public void loadStreamSymbolsFromJar() {
    URL streamDefUrl = SysMLv2Tool.class.getClassLoader().getResource(
        "Stream.symtabdefinitionsym");

    if (streamDefUrl == null) {
      Log.error(
          "0xPA090 Failed to find Stream.symtabdefinitionsym on the classpath"
              + ".");
      return;
    }

    if (!"jar".equals(streamDefUrl.getProtocol())) {
      Log.error(
          "0xPA091 Expected Stream.symtabdefinitionsym to be loaded from a "
              + "JAR");
      return;
    }

    JarURLConnection conn = null;
    JarFile jar = null;

    try {
      conn = (JarURLConnection) streamDefUrl.openConnection();
      jar = conn.getJarFile();
    }
    catch (IOException e) {
      Log.error("0xPA092 Failed to open symbol definition from JAR URL");
    }

    Path jarPath = Path.of(jar.getName());

    SysMLv2Mill.globalScope().getSymbolPath().addEntry(jarPath);

    SysMLv2Mill.globalScope().putSymbolDeSer(
        "de.monticore.cdbasis._symboltable.CDTypeSymbol",
        new OOTypeSymbolDeSer());
    SysMLv2Mill.globalScope().putSymbolDeSer(
        "de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol",
        new MethodSymbolDeSer());
  }
}
