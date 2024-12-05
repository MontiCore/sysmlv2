/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2._lsp.features.symbols;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysmlv2._lsp.SysMLv2LanguageServer;
import de.se_rwth.commons.logging.Slf4jLog;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testet hauptsächlich für die SysML-Structure-View die korrekte Verfügbarkeit und Typisierung der Symbole.
 */
public class DocumentSymbolProviderTest {

  /** Testet verschachtelte Pakete und mehrere Top-Level Pakete */
  @Test
  @Disabled
  public void testNestedComponent() throws ExecutionException, InterruptedException {
    Path base = Paths.get("src/test/resources/documentSymbols");
    Path model = base.resolve("NestedPackages.sysml");
    SysMLv2LanguageServer languageServer = new SysMLv2LanguageServer(new MCPath(base));
    languageServer.getIndexingManager().indexAllFilesInPath();


    // Make sure that symbol information was put into the provider and can be retrieved
    List<Either<SymbolInformation, DocumentSymbol>> symbolInfo = languageServer.getTextDocumentService().documentSymbol(
        new DocumentSymbolParams(new TextDocumentIdentifier(
            model.toUri().toString()
        ))
    ).get();
    assertThat(symbolInfo).isNotEmpty();

    // "package TopLevel"
    DocumentSymbol packageTopLevel = symbolInfo.get(0).getRight();
    assertThat(packageTopLevel.getKind()).isEqualTo(SymbolKind.Namespace);
    assertThat(packageTopLevel.getChildren()).hasSize(2);

    // Find "package Nested"
    var childrenThatAreNamespaces = packageTopLevel.getChildren()
        .stream()
        .filter(s -> s.getKind().equals(SymbolKind.Namespace))
        .collect(Collectors.toList());
    assertThat(childrenThatAreNamespaces).hasSize(1);

    var nestedPackage = childrenThatAreNamespaces.get(0);
    assertThat(nestedPackage.getChildren()).hasSize(1);

    var partDefB = nestedPackage.getChildren().get(0);
    assertThat(partDefB.getName()).isEqualTo("B");
    assertThat(partDefB.getKind()).isEqualTo(SymbolKind.Field);

    // package "TopLevel2"
    var topLevel2Package = symbolInfo.get(1).getRight();
    assertThat(topLevel2Package.getChildren()).hasSize(1);

    var stateDefC = topLevel2Package.getChildren().get(0);
    assertThat(stateDefC.getName()).isEqualTo("C");
    assertThat(stateDefC.getKind()).isEqualTo(SymbolKind.Function);
  }

  /**
   * Repeat test so that there's a high probability of dangerous context switches happening
   */
  @RepeatedTest(5)
  @Disabled
  public void testConcurrentRequests() {
    //Slf4jLog.init();
    var resources = Paths.get("src/test/resources/documentSymbols/concurrent");

    MCPath modelPath = new MCPath(resources);
    SysMLv2LanguageServer server = new SysMLv2LanguageServer(modelPath);
    server.getIndexingManager().indexAllFilesInPath();

    List<Path> models = List.of(
        resources.resolve("Model1.sysml"),
        resources.resolve("Model2.sysml"),
        resources.resolve("Model3.sysml"));

    // emulate async documentSymbol calls
    List<CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>>> concurrentTaskList = models
        .parallelStream()
        .map(it -> new DocumentSymbolParams(new TextDocumentIdentifier(it.toUri().toString())))
        .map(docItem -> server.getTextDocumentService().documentSymbol(docItem))
        .collect(Collectors.toList());

    // wait for them to finish
    List<List<Either<SymbolInformation, DocumentSymbol>>> resultConcurrentSymbols = concurrentTaskList.stream().map(
        future -> {
          try {
            return future.get();
          }
          catch (Exception e) {
            e.printStackTrace();
          }
          return List.of(Either.<SymbolInformation, DocumentSymbol>forRight(new DocumentSymbol()));
        }).collect(Collectors.toList());

    List<List<Either<SymbolInformation, DocumentSymbol>>> resultSynchronousSymbols = models
        .stream()
        .map(it -> new DocumentSymbolParams(new TextDocumentIdentifier(it.toUri().toString())))
        .map(docItem -> server.getTextDocumentService().documentSymbol(docItem))
        .map(cf -> {
          try {
            return cf.get();
          }
          catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
          }
          return null;
        })
        .collect(Collectors.toList());

    // results should be identical
    assertThat(resultConcurrentSymbols).isNotEmpty();
    assertThat(resultConcurrentSymbols).isEqualTo(resultSynchronousSymbols);
  }
}
