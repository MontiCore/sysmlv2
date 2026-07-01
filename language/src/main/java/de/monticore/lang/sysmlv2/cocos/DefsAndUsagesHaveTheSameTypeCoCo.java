package de.monticore.lang.sysmlv2.cocos;

import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts._ast.ASTAttributeUsage;
import de.monticore.lang.sysmlparts._ast.ASTEnumUsage;
import de.monticore.lang.sysmlparts._ast.ASTPartUsage;
import de.monticore.lang.sysmlparts._ast.ASTPortUsage;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTAttributeUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTEnumUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPartUsageCoCo;
import de.monticore.lang.sysmlparts._cocos.SysMLPartsASTPortUsageCoCo;
import de.se_rwth.commons.logging.Log;

public class DefsAndUsagesHaveTheSameTypeCoCo
    implements SysMLPartsASTPartUsageCoCo, SysMLPartsASTPortUsageCoCo,
    SysMLPartsASTAttributeUsageCoCo, SysMLPartsASTEnumUsageCoCo {

  @Override
  public void check(ASTPartUsage node) {
    boolean ok = node.getSpecializationList().stream()
        .filter(t -> t instanceof ASTSysMLTyping)
        .anyMatch(t -> node.getEnclosingScope()
            .resolvePartDef(t.getSuperTypes(0).printType())
            .isPresent());

    if (!ok) {
      Log.error("0xCOCO002 No valid PartDef found for ASTSysMLTyping",
          node.get_SourcePositionStart());
    }


   /* node.getSymbol().getTypesList().stream()
        .filter(t -> !(t.getTypeInfo() instanceof PartDef2TypeSymbolAdapter))// This type actually came from a part def
        .forEach(t -> Log.error(
            "0xCOCO001 Part usages must be typed by part definitions, but found: "
                + t.print(),
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd()));*/
  }

  @Override
  public void check(ASTPortUsage node) {
    boolean ok = node.getSpecializationList().stream()
        .filter(t -> t instanceof ASTSysMLTyping)
        .anyMatch(t -> node.getEnclosingScope()
            .resolvePortDef(t.getSuperTypes(0).printType())
            .isPresent());

    if (!ok) {
      Log.error("0xCOCO002 No valid PortDef found for ASTSysMLTyping",
          node.get_SourcePositionStart());
    };

  }
  @Override
  public void check(ASTAttributeUsage node) {

    node.getSpecializationList().stream()
        .filter(ASTSysMLTyping.class::isInstance)
        .forEach(t -> {

          String typeName = t.getSuperTypes(0).printType();

          boolean valid =
              node.getEnclosingScope()
                  .resolveAttributeDef(typeName)
                  .isPresent()

                  || node.getEnclosingScope()
                  .resolveType(typeName)
                  .isPresent();

          if (!valid) {
            Log.error(
                "0xCOCO003 Attribute usages may only be typed by an attribute definition "
                    + "or a valid type (e.g. ScalarValues::Boolean).",
                node.get_SourcePositionStart(),
                node.get_SourcePositionEnd());
          }
        });
  }

  @Override
  public void check(ASTEnumUsage node) {
    boolean ok = node.getSpecializationList().stream()
        .filter(t -> t instanceof ASTSysMLTyping)
        .anyMatch(t -> node.getEnclosingScope()
            .resolveEnumDef(t.getSuperTypes(0).printType())
            .isPresent());

    if (!ok) {
      Log.error("0xCOCO002 No valid EnumDef found for ASTSysMLTyping",
          node.get_SourcePositionStart());
    }

  }

}
