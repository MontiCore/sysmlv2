/* (c) https://github.com/MontiCore/monticore */

import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import org.junit.jupiter.api.Test;
import org.omg.sysml.interactive.SysMLInteractive;
import org.omg.sysml.interactive.SysMLInteractiveResult;
import org.omg.sysml.interactive.VizResult;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.util.SysMLLibraryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

public class VisualizationTest {

  @Test
  public void initialUsageTest() throws IOException {
    SysMLInteractive instance = SysMLInteractive.getInstance();

    String libPath = Paths.get("src/main/resources/sysml.library").toAbsolutePath().toString();

    // following snippet reconstructs instance.loadLibrary by eliminating hardcoded paths
    SysMLLibraryUtil.setModelLibraryDirectory(libPath);
    instance.readAll(libPath, false, ".kerml");
    instance.readAll(libPath, false, ".sysml");
    String inverterContent = Files.readString(Paths.get("src/test/resources/Inverter.sysml"));

    SysMLInteractiveResult rezEval = instance.eval(inverterContent);
    assertThat(rezEval.hasErrors()).isFalse();

    Element rezRoot = rezEval.getRootElement();
    Element resEle = instance.resolve("inverter");
    assertThat(resEle).isNotNull();

    // resolving resulting elements from eval call
    List<String> rezElements = rezRoot
        .getOwnedElement()
        .stream()
        .map(Element::getName)
        // packages can be declared without names
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    VizResult svgRes = instance.viz(rezElements, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    // this does not guarantee that the resulting SVG is complete as no GraphViz executable is provided for this test
    assertThat(svgRes.hasException()).isFalse();
  }
}
