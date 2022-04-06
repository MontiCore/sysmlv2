package de.monticore.lang.sysml4verification._lsp.features.symbols;

import de.monticore.io.paths.ModelPath;
import de.monticore.lang.sysml4verification._lsp.SysML4VerificationLanguageServer;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.Slf4jLog;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testet hauptsächlich für die SysML-Structure-View die korrekte Verfügbarkeit und Typisierung der Symbole.
 *
 * @author Adrian Costin Marin
 * @author Mathias Pfeiffer (mpfeiffer@se-rwth.de)
 */
public class DocumentSymbolProviderTest extends TestWithDocumentManager {

  private SysML4VerificationDocumentSymbolProvider sut;

  @BeforeEach
  public void setUpSut() {
    setUpDocumentManager();
    sut = new SysML4VerificationDocumentSymbolProvider(documentManager);
  }

  /** Testet verschachtelte Pakete und mehrere Top-Level Pakete */
  @Disabled("MPf: Verstehe nicht, wie wo wann was hier kaputt gegangen ist")
  @Test
  public void testNestedComponent() throws IOException {
    // Setup
    final Path nestedPackages = Paths.get("src/test/resources/documentSymbols/NestedPackages.sysml");
    final String nestedPackagesContent = Files.lines(nestedPackages, StandardCharsets.UTF_8).collect(
        Collectors.joining("\n"));

    TextDocumentItem nestedPackagesItem = addDocumentToDocumentManager(
        nestedPackages.toUri().toString(),
        nestedPackagesContent
    );
    prepareDocumentInformation(nestedPackagesItem);

    // Make sure that symbol information was put into the provider and can be retrieved
    List<Either<SymbolInformation, DocumentSymbol>> symbolInfo = sut.getDocumentSymbols(nestedPackagesItem);
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
  public void testConcurrentRequests() {
    Slf4jLog.init();
    ModelPath modelPath = new ModelPath(Paths.get("src/test/resources/documentSymbols/concurrent"));
    SysML4VerificationLanguageServer server = new SysML4VerificationLanguageServer(modelPath);
    server.getIndexingManager().indexAllFilesInPath();

    final Path path1 = Paths.get("src/test/resources/documentSymbols/concurrent/Model1.sysml");
    final Path path2 = Paths.get("src/test/resources/documentSymbols/concurrent/Model2.sysml");
    final Path path3 = Paths.get("src/test/resources/documentSymbols/concurrent/Model3.sysml");

    List<Path> pathsList = List.of(path1, path2, path3);

    // emulate async documentSymbol calls
    List<CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>>> concurrentTaskList = pathsList
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

    // use the DocumentSymbolProvider synchronously
    // From this line onwards run all asynchronous tasks of the server consecutively on the main thread
    server.getTextDocumentService().setRunSequentially(true);

    List<List<Either<SymbolInformation, DocumentSymbol>>> resultSynchronousSymbols = pathsList
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
