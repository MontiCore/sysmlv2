package de.monticore.lang.sysmlv2._symboltable;

import com.google.common.io.Files;
import de.monticore.lang.sysmlv2.symboltable.FieldSymbolDeSer;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class SysMLv2GlobalScope extends SysMLv2GlobalScopeTOP {

  /**
   * Loaded files are added to the file cache by their artifact scope name.
   * When checking if a file was loaded we also need to decode the artifact scope name from the file name.
   * Thus, we only operate under the contract that the artifact scope name matches the file name without its extension.
   */
  public  void loadFileForModelName (String modelName) {
    java.util.Optional<java.net.URL> location = getSymbolPath().find(modelName, getFileExt());

    try {
      if(location.isPresent()) {
        var potArtScopeName = Files.getNameWithoutExtension(Paths.get(location.get().toURI()).getFileName().toString());

        if (!isFileLoaded(potArtScopeName)) {
          addLoadedFile(potArtScopeName);
          ISysMLv2ArtifactScope as = getSymbols2Json().load(location.get());
          addSubScope(as);
        }
      }
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * We inject our handwritten DeSer for FieldSymbols because it cannot be extended through TOP as it comes from an inherited language.
   */
  @Override
  public void init() {
    super.init();
    symbolDeSers.put("de.monticore.symbols.oosymbols._symboltable.FieldSymbol", new FieldSymbolDeSer());
  }

  @Override
  public SysMLv2GlobalScope getRealThis() {
    return this;
  }
}
