package de.monticore.lang.sysml4verification._cocos;

import de.monticore.lang.sysml4verification._ast.ASTSysMLFeatureDirection;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTPartDef;
import de.monticore.lang.sysmlblockdiagrams._ast.ASTSysMLAttribute;
import de.monticore.lang.sysmlstatemachinediagrams._ast.ASTStateDef;
import de.se_rwth.commons.logging.Log;

/**
 * Prüft, dass "final" nur in folgenden zwei Fällen vorkommt:
 * - Attribute von Part Definitions
 * - Parameter von State Definitions
 *
 * @Example
 * part def MyPart {
 *   final value para: Type;
 * }
 * state def MyState(final para: Type);
 */
public class FinalDirection implements SysML4VerificationASTSysMLFeatureDirectionCoCo {

  public final static String errorMessage
      = "\"final\" is only allowed on attributes of part definitions and parameters of state definitions";

  @Override
  public void check(ASTSysMLFeatureDirection node) {
    if(node.isFinal()) {
      if(node.getEnclosingScope().isPresentAstNode()) {
        var ast = node.getEnclosingScope().getAstNode();
        // Erlaubt wäre: part def MyPart { final value para: Type; }
        if (ast instanceof ASTPartDef) {
          var part = (ASTPartDef) ast;
          var isInAttribute = part.getBDUsageMemberList().stream()
              .filter(e -> e instanceof ASTSysMLAttribute)
              .filter(e -> ((ASTSysMLAttribute) e).isPresentSysMLFeatureDirection())
              .anyMatch(e -> ((ASTSysMLAttribute) e).getSysMLFeatureDirection().equals(node));
          if (!isInAttribute) {
            Log.error(errorMessage, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
          }
        }
        // Erlaubt wäre: state def MyState(final para: Type)
        else if (ast instanceof ASTStateDef) {
          var state = (ASTStateDef) ast;
          if (state.isPresentParameterList()) {
            var isInAttribute = state.getParameterList().getSysMLParameterList().stream()
                .filter(p -> p.isPresentSysMLFeatureDirection())
                .anyMatch(p -> p.getSysMLFeatureDirection().equals(node));
            if (!isInAttribute) {
              Log.error(errorMessage, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
            }
          }
        }
        else {
          Log.error(errorMessage, node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
      }
      else {
        Log.warn("Cannot determine context of \"final\" feature. Proceed with caution.",
            node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    }
  }

}
