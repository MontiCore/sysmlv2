package de.monticore.lang.sysmlbasis;

import java.util.List;

public interface TESTSymbolWithImports {

  // Demo interface welches wir im resolve verwenden. Wir markieren hier
  // nur symbole, welche imports unterstützen e.g. PartDef, Packages


  List<de.monticore.symboltable.ImportStatement> getImportsList();

  void setImportsList(List<de.monticore.symboltable.ImportStatement> imports);

}
