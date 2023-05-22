package de.monticore.lang.sysmlv2._lsp.features.code_action.utils;

import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._lsp.language_access.SysMLv2ScopeManager;
import org.antlr.v4.runtime.misc.Pair;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;

import java.util.concurrent.atomic.AtomicReference;

public abstract class DocumentUtils {

  public static String getFolder(String uri){
    return uri.substring(0, uri.lastIndexOf("/") + 1);
  }

  public static String getNewPartDefName(String reference, Pair<String, String> replace, String suffix){
    AtomicReference<String> result = new AtomicReference<>(reference);
    SysMLv2ScopeManager.getInstance().syncAccessGlobalScope(gs -> {
      var partDefName  = reference;

      if (replace != null){
        if (partDefName.toLowerCase().contains(replace.a.toLowerCase())) {
          partDefName = partDefName.replaceAll("(?i)" + replace.a, replace.b);
        } else {
          partDefName = "Llr" + partDefName;
        }
      }

      if (suffix != null){
        partDefName = partDefName + suffix;
      }

      int i = 1;
      while (gs.resolvePartDef(partDefName).isPresent()){
        partDefName = partDefName + ++i;
      }
      result.set(partDefName);
    });
    return result.get();
  }

  public static VersionedTextDocumentIdentifier getFreeDocumentUri(DocumentManager documentManager, String folder, String modelname){

    var newDocUri = folder + modelname + ".sysml";
    var newDocument = new VersionedTextDocumentIdentifier(newDocUri, null);

    int i = 1;
    while (documentManager.hasDocument(newDocument)){
      newDocUri = folder + modelname + ++i + ".sysml";
      newDocument = new VersionedTextDocumentIdentifier(newDocUri, null);
    }

    return newDocument;
  }
}
