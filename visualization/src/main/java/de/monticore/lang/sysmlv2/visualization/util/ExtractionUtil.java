/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.visualization.util;

import de.monticore.lang.sysmlv2.visualization.VisualizationCLI;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtractionUtil {

  /**
   * Extracts contents of the default packaged library to the destination path.
   */
  public void extractSelf(Path destination) throws IOException {
    ProtectionDomain protectionDomain = VisualizationCLI.class.getProtectionDomain();
    URL fileUrl = protectionDomain.getCodeSource().getLocation();

    Path root;

    if (fileUrl.getPath().endsWith(".jar")) {
      // from jar
      URI jarFileUri = URI.create("jar:" + fileUrl);

      Map<String, Object> env = Map.of("Encode", "UTF-8");
      FileSystem zipFS = FileSystems.newFileSystem(jarFileUri, env);

      root = zipFS.getPath("sysml.library");
      extractFromJar(root, destination);
    }
    else {
      // from classpath
      FileUtils.copyDirectory(new File("src/main/resources/sysml.library"),destination.toFile());
    }
  }

  private void extractFromJar(Path path, Path destination) throws IOException {
    for(Path entry: Files.list(path).collect(Collectors.toUnmodifiableList())) {
      if(Files.isRegularFile(entry)) {
        Path extractionPath = destination.resolve(entry.toString());
        extractionPath.getParent().toFile().mkdirs();
        Files.copy(entry, extractionPath);
      }
      else if (Files.isDirectory(entry)){
        extractFromJar(entry, destination);
      }
    }
  }
}
