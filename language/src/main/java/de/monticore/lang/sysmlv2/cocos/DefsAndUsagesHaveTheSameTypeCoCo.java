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
import de.monticore.lang.sysmlparts.symboltable.adapters.EnumDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PartDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortDef2TypeSymbolAdapter;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.se_rwth.commons.logging.Log;

public class DefsAndUsagesHaveTheSameTypeCoCo
    implements SysMLPartsASTPartUsageCoCo, SysMLPartsASTPortUsageCoCo,
    SysMLPartsASTAttributeUsageCoCo, SysMLPartsASTEnumUsageCoCo {

  @Override
  public void check(ASTPartUsage node) {
    boolean ok = node.getSpecializationList().stream()
        .filter(t -> t instanceof ASTSysMLTyping)
        .flatMap(t -> ((ASTSysMLTyping) t).getSuperTypesList().stream())
        .map(t -> ((ISysMLv2Scope)t.getEnclosingScope()).resolvePartDef(t.printType()))
        .allMatch(t -> t.isPresent());


    if (!ok) {
      Log.error("0xCOCO002 No valid PartDef found for ASTSysMLTyping",
          node.get_SourcePositionStart());
    }


  }

  @Override
  public void check(ASTPortUsage node) {
    boolean ok = node.getSpecializationList().stream()
        .filter(t -> t instanceof ASTSysMLTyping)
        .flatMap(t -> ((ASTSysMLTyping) t).getSuperTypesList().stream())
        .map(t -> ((ISysMLv2Scope)t.getEnclosingScope()).resolvePortDef(t.printType()))
        .allMatch(t -> t.isPresent());

    if (!ok) {
      Log.error("0xCOCO002 No valid PortDef found for ASTSysMLTyping",
          node.get_SourcePositionStart());
    }

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
                  .filter(type ->
                      !(type instanceof PartDef2TypeSymbolAdapter)
                          && !(type instanceof PortDef2TypeSymbolAdapter))
                  .isPresent();

          if (!valid) {
            Log.error(
                "0xCOCO003 Attribute usages may only be typed by an "
                    + "attribute definition, an enum def or a valid KerML type.",
                node.get_SourcePositionStart(),
                node.get_SourcePositionEnd());
          }
        });
  }

  @Override
  public void check(ASTEnumUsage node) {
    boolean ok = node.getSpecializationList().stream()
        .filter(t -> t instanceof ASTSysMLTyping)
        .flatMap(t -> ((ASTSysMLTyping) t).getSuperTypesList().stream())
        .map(t -> ((ISysMLv2Scope)t.getEnclosingScope()).resolveEnumDef(t.printType()))
        .allMatch(t -> t.isPresent());

    if (!ok) {
      Log.error("0xCOCO002 No valid EnumDef found for ASTSysMLTyping",
          node.get_SourcePositionStart());
    }

  }

}
