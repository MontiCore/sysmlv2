/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.visualization;

import de.monticore.lang.sysmlv2.visualization.util.ExtractionUtil;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.omg.sysml.interactive.SysMLInteractive;
import org.omg.sysml.interactive.SysMLInteractiveResult;
import org.omg.sysml.interactive.VizResult;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.util.SysMLLibraryUtil;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static de.monticore.lang.sysmlv2.visualization.util.VisualizationUtil.STYLE_HELP;
import static de.monticore.lang.sysmlv2.visualization.util.VisualizationUtil.VIEW_HELP;

public class VisualizationCLI {
  public static void main(String[] args) throws IOException {
    Options options = new Options()
        .addOption("e", "extractlib", true, "Extract packaged default SyMLv2 library to specified path")
        .addOption("l", "library", true, "Path to default SysMLv2 library. "
            + "This can match the extraction path and be used simultaneously")
        .addOption("g", "graphviz", true, "Path to graphviz executable")
        .addOption("v", "view", true, VIEW_HELP)
        .addOption("s", "style", true, STYLE_HELP)
        .addOption("h", "help", false, "help");

    // Parse input
    CommandLine cmd;
    try {
      cmd = new DefaultParser().parse(options, args);
    } catch (ParseException ex) {
      new HelpFormatter().printHelp("java -jar <JAR-Name>", options);
      return;
    }

    if (cmd.hasOption("h")) {
      new HelpFormatter().printHelp("java -jar <JAR-Name>", options);
      return;
    }

    Log.init();

    SysMLInteractive instance = SysMLInteractive.getInstance();
    // allows using stdout to print
    instance.setVerbose(false);

    if (cmd.hasOption("e")) {
      Path destination = Paths.get(cmd.getOptionValue("e"));
      new ExtractionUtil().extractSelf(destination);
    }

    if (cmd.hasOption("l")) {
      // required so that spaces don't get double %-escaped by omg resource finder
      String path = Paths.get(cmd.getOptionValue("l")).toUri().getPath();

      // reproduces instance.loadLibrary functionality while avoiding hard-coded folder structure
      SysMLLibraryUtil.setModelLibraryDirectory(path);
      instance.readAll(path, false, ".kerml");
      instance.readAll(path, false, ".sysml");
    }

    if (cmd.hasOption("g")) {
      instance.setGraphVizPath(Paths.get(cmd.getOptionValue("g")).toUri().getPath());
    }

    List<String> view = new ArrayList<>();

    if (cmd.hasOption("v")) {
      view.add(cmd.getOptionValue("v"));
    }

    List<String> styles = new ArrayList<>();

    if (cmd.hasOption("s")) {
      styles.addAll(List.of(cmd.getOptionValues("s")));
    }

    // if it has no args it is used only for extraction
    if (cmd.getArgs().length == 0) {
      return;
    }

    Path modelPath = Paths.get(cmd.getArgs()[0]);
    String input = Files.readString(modelPath);

    // adds resource to index
    SysMLInteractiveResult result = instance.eval(input);
    if (result.hasErrors()) {
      Log.error("Could not parse model at " + cmd.getArgs()[0] + "\n"
          + result.formatIssues());
    } else {
      // resolving resulting elements from eval call
      List<String> elements = result
          .getRootElement()
          .getOwnedElement()
          .stream()
          .map(Element::getName)
          // packages can be declared without names
          .filter(Objects::nonNull)
          .collect(Collectors.toList());

      VizResult vizResult = instance.viz(elements, view, styles, Collections.emptyList());

      if (vizResult.hasException()) {
        Log.error("Could not create visualization for " + cmd.getArgs()[0] + vizResult.formatException());
      } else {
        String resultingString = "";
        // send visualization result to stdout
        switch (vizResult.kind) {
          case PLANTUML:
            resultingString = vizResult.getPlantUML();
            break;
          case TEXT:
            resultingString = vizResult.getText();
            break;
          case SVG:
            resultingString = vizResult.getSVG();
          default:
            break;
        }
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.println(resultingString);
      }
    }
  }
}
