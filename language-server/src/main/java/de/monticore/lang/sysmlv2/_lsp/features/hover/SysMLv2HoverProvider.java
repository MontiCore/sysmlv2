package de.monticore.lang.sysmlv2._lsp.features.hover;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.symboltable.ISymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class SysMLv2HoverProvider extends SysMLv2HoverProviderTOP {

  public SysMLv2HoverProvider(DocumentManager documentManager,
                              ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(documentManager, symbolUsageResolutionProvider);
  }

  @Override
  public Hover getHoverInformation(TextDocumentItem document, Position position) {
    Optional<DocumentInformation> diOpt = documentManager.getDocumentInformation(document);
    if(diOpt.isEmpty()){
      return null;
    }

    var symbols = symbolUsageResolutionProvider.getSymbols(diOpt.get(), position);
    for (ISymbol s : symbols){
      var info = new ArrayList<String>();

      if (s instanceof PartUsageSymbol){
        info.addAll(getInfo((PartUsageSymbol) s));
      } else if (s instanceof PartDefSymbol){
        info.addAll(getInfo((PartDefSymbol) s));
      }

      if (!info.isEmpty()){
        var m = new MarkupContent();
        m.setKind("markdown");
        m.setValue(String.join("\n\n---\n\n", info));
        return new Hover(m);
      }
    }
    return super.getHoverInformation(document, position);
  }


  public ArrayList<String> getInfo(PartUsageSymbol symbol){
    if (symbol.getPartDef().isPresent()){
      return getInfo(symbol.getPartDef().get());
    }
    return new ArrayList<>();
  }

  public ArrayList<String> getInfo(PartDefSymbol symbol){
    var info = new ArrayList<String>();
    if (symbol.getRequirementType() != null){
      info.add("**RequirementType:** " + symbol.getRequirementType().name().toLowerCase());
    }

    var refiners = symbol.getRefiners().collect(Collectors.toList());
    if (refiners.size() > 0){
      info.add("**Refined by:** " + refiners.stream().map(ISymbol::getName).collect(Collectors.joining(", ")));
    }
    return info;
  }
}