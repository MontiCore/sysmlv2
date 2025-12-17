package de.monticore.lang.sysmlparts.symboltable.completers;

import de.monticore.lang.sysmlbasis._ast.ASTAnonymousReference;
import de.monticore.lang.sysmlbasis._ast.ASTSysMLTyping;
import de.monticore.lang.sysmlparts.SysMLPartsMill;
import de.monticore.lang.sysmlparts._ast.ASTEnumDef;
import de.monticore.lang.sysmlparts._ast.ASTEnumUsage;
import de.monticore.lang.sysmlparts._visitor.SysMLPartsVisitor2;
import de.monticore.types.check.SymTypeExpressionFactory;

/**
 * Enum-Literale sollen sich wie Fields verhalten. Allerdings haben die Dinger nicht immer einen Namen (auf
 * Lexer/Parser-Ebene). Deswegen kann man nicht einfach "EnumUsage extends Field = ..." in die Grammatik schreiben. Die
 * empfohlene Lösung des MC-Teams ist es die EnumUsageSymbols einfach duch FieldSymbols auszutauschen bzw. diese
 * hinzuzufügen. Das gleiche für AnonymousReference.
 */
public class ConvertEnumUsagesToFields implements SysMLPartsVisitor2 {

  @Override
  public void visit(ASTEnumDef node) {
    // enum def is an object type
    var type = SymTypeExpressionFactory.createTypeObject(node.getName(), node.getEnclosingScope());

    for(var elem: node.getSysMLElementList()) {
      var baseBuilder = SysMLPartsMill.fieldSymbolBuilder()
          .setAstNodeAbsent()
          .setIsStatic(true)
          .setIsPublic(true)
          .setType(type);

      if(elem instanceof ASTEnumUsage && ((ASTEnumUsage)elem).isPresentName()) {
        // each named enum usage behaves like a static field whose type is the enclosing def
        baseBuilder.setName(((ASTEnumUsage)elem).getName());
        node.getSpannedScope().add(baseBuilder.build());
      }
      else if (elem instanceof ASTAnonymousReference &&
          ((ASTAnonymousReference)elem).getSpecializationList()
              .stream()
              .filter(spec -> spec instanceof ASTSysMLTyping)
              .findAny()
              .isEmpty()) {
        // same for anonymous references in this context that do not have a type
        // cannot use completed type because we are in the ordered ST completion phase
        baseBuilder.setName(((ASTAnonymousReference) elem).getName());
        node.getSpannedScope().add(baseBuilder.build());
      }
    }
  }
}
