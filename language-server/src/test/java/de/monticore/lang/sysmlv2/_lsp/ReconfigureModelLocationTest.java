package de.monticore.lang.sysmlv2._lsp;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.monticore.io.paths.MCPath;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReconfigureModelLocationTest {

  @Test
  public void testIndexing() {
    MCPath initialModelPath = new MCPath(
        Paths.get("src", "test", "resources", "ReconfigureModelLocationTest", "modelRootA"));
    SysMLv2LanguageServer languageServer = new SysMLv2LanguageServer(initialModelPath);
    languageServer.getIndexingManager().indexAllFilesInPath();

    {
      List<DocumentInformation> allDocumentInformation = languageServer.documentManager.getAllDocumentInformation(x->true);
      assertEquals(1, allDocumentInformation.size());
      DocumentInformation di = allDocumentInformation.get(0);
      assertTrue(di.uri.endsWith("CompA.sysml"));
    }

    JsonObject settings = new JsonObject();
    settings.add("source", new JsonPrimitive("src/test/resources/ReconfigureModelLocationTest/modelRootB"));
    languageServer.getWorkspaceService().didChangeConfiguration(new DidChangeConfigurationParams(settings));

    {
      List<DocumentInformation> allDocumentInformation = languageServer.documentManager.getAllDocumentInformation(x->true);
      assertEquals(1, allDocumentInformation.size());
      DocumentInformation di = allDocumentInformation.get(0);
      assertTrue(di.uri.endsWith("CompB.sysml"));
    }
  }
}
