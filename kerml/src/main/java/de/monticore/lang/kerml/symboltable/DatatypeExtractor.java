/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.kerml.symboltable;

import de.monticore.lang.kermlelements._ast.ASTDatatype;
import de.monticore.lang.kermlelements._ast.ASTKerMLSpecialization;
import de.monticore.lang.kermlelements._visitor.KerMLElementsVisitor2;
import de.monticore.lang.kermlparts.symboltable.adapters.Datatype2TypeSymbolAdapter;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsArtifactScope;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/** Extracts KerML datatypes and converts them to serializable type symbols. */
public class DatatypeExtractor implements KerMLElementsVisitor2 {

  protected final IOOSymbolsArtifactScope exportScope;
  protected final Map<ASTDatatype, Datatype2TypeSymbolAdapter> exportedTypes =
      new LinkedHashMap<>();
  protected final Map<String, Datatype2TypeSymbolAdapter> exportedTypesByName =
      new LinkedHashMap<>();

  public DatatypeExtractor(IOOSymbolsArtifactScope exportScope) {
    this.exportScope = exportScope;
  }

  @Override
  public void visit(ASTDatatype node) {
    if (node.isPresentSymbol()) {
      Datatype2TypeSymbolAdapter type =
          new Datatype2TypeSymbolAdapter(node.getSymbol());
      type.setPackageName(exportScope.getPackageName());
      exportScope.add(type);
      exportedTypes.put(node, type);
      exportedTypesByName.put(type.getName(), type);
    }
  }

  /** Connects specializations after all datatypes have been collected. */
  public void completeSuperTypes() {
    exportedTypes.forEach((node, type) ->
        node.getKerMLRelationClauseList().stream()
            .filter(ASTKerMLSpecialization.class::isInstance)
            .map(ASTKerMLSpecialization.class::cast)
            .flatMap(specialization ->
                specialization.getSpecializedList().stream())
            .map(superTypeName ->
                exportedTypesByName.get(superTypeName.getBaseName()))
            .filter(java.util.Objects::nonNull)
            .map(SymTypeExpressionFactory::createTypeObject)
            .forEach(type::addSuperTypes));
  }
}
