package de.monticore.lang.sysmlv2._symboltable;

import com.google.common.io.Files;
import de.monticore.lang.sysmlv2.symboltable.FieldSymbolDeSer;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class SysMLv2GlobalScope extends SysMLv2GlobalScopeTOP {

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
