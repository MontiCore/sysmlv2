/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.features.symbols;

import com.google.common.collect.ImmutableMap;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.DocumentSymbolProvider;
import de.monticore.lang.sysmlconstraints._ast.ASTConstraintDef;
import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLPackage;
import de.monticore.lang.sysmlparts._ast.ASTEnumDef;
import de.monticore.lang.sysmlparts._ast.ASTPartDef;
import de.monticore.lang.sysmlparts._ast.ASTPortDef;
import de.monticore.lang.sysmlparts._symboltable.SysMLPartsSymbols2Json;
import de.monticore.lang.sysmlstates._ast.ASTStateDef;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2ArtifactScope;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Symbols2Json;
import de.monticore.symboltable.ISymbol;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

/**
 * Creates a tree of DocumentSymbols recalled for a Document. Assumes that all symbols are packaged.
 */
public class SysMLv2DocumentSymbolProvider extends SysMLv2DocumentSymbolProviderTOP
    implements DocumentSymbolProvider {

  private static final Logger logger = LoggerFactory.getLogger(SysMLv2DocumentSymbolProvider.class);

  // default mapping from mc symbols to SymbolKind
  private final Map<Class<?>, SymbolKind> SYMBOLKIND_MAPPER =
      ImmutableMap.of(
          ASTPartDef.class, SymbolKind.Class,
          ASTStateDef.class, SymbolKind.Function,
          ASTPortDef.class, SymbolKind.Field,
          ASTConstraintDef.class, SymbolKind.Property,
          ASTSysMLPackage.class, SymbolKind.Namespace,
          ASTEnumDef.class, SymbolKind.Enum
      );

  public SysMLv2DocumentSymbolProvider(DocumentManager documentManager) {
    super(documentManager);
  }

  /**
   * Returns hierarchic DocumentSymbol tree from a flat list of mc Symbols provided by the document's DocumentInformation.
   */
  @Override
  public List<Either<SymbolInformation, DocumentSymbol>> getDocumentSymbols(TextDocumentItem document) {
    Optional<DocumentInformation> documentInformation = documentManager.getDocumentInformation(document);

    if (!documentInformation.isPresent()) {
      logger.error("No document information available for the given URI: " + document.getUri());
      return Collections.emptyList();
    }

    // set reference to original flat list as source while building symbol tree
    final List<ISymbol> flatSymbolList = documentInformation.get().symbols;

    return loadSymbolsToTree(
        flatSymbolList,
        flatSymbolList
            .stream()
            // start recursion with top-level symbols
            .filter(symbol -> symbol.getEnclosingScope() instanceof ISysMLv2ArtifactScope)
            .collect(Collectors.toList())
    )
        .stream()
        .map(Either::<SymbolInformation, DocumentSymbol>forRight)
        .collect(Collectors.toList());
  }

  /**
   * Helper method that creates all subtrees for provided mc symbols recursively and transforms these into {@link org.eclipse.lsp4j.DocumentSymbol}s.
   *
   * @param allSymbols    - the complete symbol list which serves as the data source for building the DocumentSymbol tree
   * @param activeSymbols - {@link de.monticore.symboltable.ISymbol}s passed are the root nodes for which the method creates the subtrees by extracting
   *                      data from the allSymbols list
   * @return returns a hierarchical List of DocumentSymbols containing trees
   */
  private List<DocumentSymbol> loadSymbolsToTree(final List<ISymbol> allSymbols, List<ISymbol> activeSymbols) {

    var toJson = new SysMLv2Symbols2Json();
    toJson.getTraverser().getSysMLPartsVisitorList().clear();
    var toJsonParts = new SysMLPartsSymbols2Json(toJson.getTraverser(), toJson.getJsonPrinter()) {
      @Override
      public void init(){
        super.init();
        partDefSymbolDeSer = new ExtendedPartDefSymbolDeSer();
      }
    };
    toJson.getTraverser().add4SysMLParts(toJsonParts);

    return activeSymbols.stream().map(symbol ->
        new DocumentSymbol(
            symbol.getName(),
            SYMBOLKIND_MAPPER.getOrDefault(symbol.getAstNode().getClass(), SymbolKind.Object),
            new Range(new Position(symbol.getAstNode().get_SourcePositionStart().getLine(),
                symbol.getAstNode().get_SourcePositionEnd().getColumn()),
                new Position(symbol.getAstNode().get_SourcePositionEnd().getLine(),
                    symbol.getAstNode().get_SourcePositionEnd().getColumn())
            ),
            new Range(new Position(symbol.getSourcePosition().getLine(), symbol.getSourcePosition().getColumn()),
                new Position(symbol.getSourcePosition().getLine(), symbol.getSourcePosition().getColumn())
            ),
            toJson.serialize((ISysMLv2Scope) symbol.getEnclosingScope()),
            getChildren(symbol, allSymbols)
        )
    ).collect(Collectors.toList());
  }

  /** load children recursively iff enclosing scope name and symbol name are equal */
  private List<DocumentSymbol> getChildren(ISymbol parent, List<ISymbol> allSymbols) {
    return loadSymbolsToTree(
        allSymbols,
        allSymbols
            .stream()
            .filter(symbol -> {
                  // BooleanSupplier makes sure, that predicates are not executed when not needed
                  BooleanSupplier parentNameIsEqual = () -> symbol.getEnclosingScope().isPresentName() &&
                      symbol.getEnclosingScope().getName().equals(parent.getName());
                  BooleanSupplier parentIsPackage = () -> parent.getAstNode() instanceof ASTSysMLPackage;
                  // Name could still be ambiguous (TODO would need to check FQN)
                  BooleanSupplier parentAstIsEqual = () -> symbol.getEnclosingScope().getAstNode()
                      .deepEquals((ASTSysMLPackage) parent.getAstNode());
                  return parentNameIsEqual.getAsBoolean() && parentIsPackage.getAsBoolean() && parentAstIsEqual.getAsBoolean();
                }
            )
            .collect(Collectors.toList())
    );
  }
}
