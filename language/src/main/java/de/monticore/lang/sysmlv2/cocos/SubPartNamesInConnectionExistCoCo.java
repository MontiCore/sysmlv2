/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTEndpoint;
import de.monticore.lang.sysmlparts._ast.ASTConnectionUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTConnectionUsageCoCo;
import de.monticore.lang.sysmlparts._symboltable.ISysMLPartsScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo3: Jeder in "connect a.b to c.d" verwendete Name von Subkomponenten (a und c)
 * muss im Modell vorhanden sein.
 */
public class SubPartNamesInConnectionExistCoCo implements SysMLPartsASTConnectionUsageCoCo {

  @Override
  public void check(ASTConnectionUsage node) {
    if (!node.isPresentSrc() || !node.isPresentTgt()) {
      return; // keine Verbindung
    }

    var scope = node.getEnclosingScope();

    checkSubcomponentQualifier(scope, node.getSrc(), node);
    checkSubcomponentQualifier(scope, node.getTgt(), node);
  }

  protected void checkSubcomponentQualifier(ISysMLPartsScope scope,
                                            ASTEndpoint endpoint,
                                            ASTConnectionUsage node) {
    String qname = endpointQName(endpoint);
    String subName = extractFirstSegment(qname);

    if (subName == null) {
      return;
    }

    int matches = scope
        .resolvePartUsageLocallyMany(false, subName,
            AccessModifier.ALL_INCLUSION, p -> true)
        .size();

    if (matches > 1) {
      /*
      * ambiguous names in PartDefinition are caught by UniqueSubPartNamesInParentCoCo
      */
    } else if (matches == 0) {
      Log.error(
          "0x10AA3 The subcomponent name used in 'connect' \"" + subName
              + "\" does not exist in the model.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  protected String endpointQName(ASTEndpoint ep) {
    return ep.getMCQualifiedName() != null ? ep.getMCQualifiedName().toString() : "";
  }

  protected String extractFirstSegment(String qname) {
    int dot = qname.indexOf('.');
    if (dot > 0) {
      return qname.substring(0, dot);
    }
    return null;
  }
}
