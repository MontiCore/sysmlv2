/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml.symboltable;

import de.monticore.lang.kermlelements._ast.ASTDatatype;
import de.monticore.lang.kermlelements._visitor.KerMLElementsVisitor2;
import de.monticore.lang.kermlparts.symboltable.adapters.Datatype2TypeSymbolAdapter;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsArtifactScope;

/** Extracts KerML datatypes and converts them to serializable type symbols. */
public class DatatypeExtractor implements KerMLElementsVisitor2 {

  protected final IBasicSymbolsArtifactScope exportScope;

  public DatatypeExtractor(IBasicSymbolsArtifactScope exportScope) {
    this.exportScope = exportScope;
  }

  @Override
  public void visit(ASTDatatype node) {
    if (!node.isPresentSymbol()) {
      return;
    }

    Datatype2TypeSymbolAdapter type =
        new Datatype2TypeSymbolAdapter(node.getSymbol());
    type.setPackageName(exportScope.getPackageName());
    exportScope.add(type);
  }
}
