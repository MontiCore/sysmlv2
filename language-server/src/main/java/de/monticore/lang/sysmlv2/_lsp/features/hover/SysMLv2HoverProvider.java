package de.monticore.lang.sysmlv2._lsp.features.hover;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentInformation;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.features.hover.HoverRule;
import de.monticore.ast.ASTNode;
import de.monticore.lang.sysmlbasis._symboltable.SysMLBasisScope;
import de.monticore.lang.sysmlbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlparts._symboltable.PartUsageSymbol;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentItem;

import java.util.Optional;
import java.util.function.Consumer;

public class SysMLv2HoverProvider extends SysMLv2HoverProviderTOP {

  public SysMLv2HoverProvider(DocumentManager documentManager,
                              ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(documentManager, symbolUsageResolutionProvider);
  }

  @Override
  public Hover getHoverInformation(TextDocumentItem document, Position position) {
    Optional<DocumentInformation> diOpt = documentManager.getDocumentInformation(document);
    if(!diOpt.isPresent()){
      return null;
    }

    var symbols = symbolUsageResolutionProvider.getSymbols(diOpt.get(), position);
    for (ISymbol s : symbols){
      if (s instanceof PartDefSymbol){
        if (((PartDefSymbol) s).getRequirementType() == null){
          continue;
        }
        var m = new MarkupContent();
        m.setKind("plaintext");
        m.setValue("RequirementType: " + ((PartDefSymbol) s).getRequirementType().name().toLowerCase());
        return new Hover(m);
      }
      if (s instanceof PartUsageSymbol){
        var partUsage = (PartUsageSymbol)s;
        if (partUsage.getPartDef().isPresent()){
          if (partUsage.getPartDef().get().getRequirementType() == null){
            continue;
          }
          var m = new MarkupContent();
          m.setKind("plaintext");
          m.setValue("RequirementType: " + partUsage.getPartDef().get().getRequirementType().name().toLowerCase());
          return new Hover(m);
        }
      }
    }
    return super.getHoverInformation(document, position);
  }
}
