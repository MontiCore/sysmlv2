package de.monticore.lang.sysmlv2.types3;

import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.streams.StreamSymTypeFactory;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;

public class SysMLStreamExpressionsTypeVisitor extends de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor {
  @Override
  public void endVisit(ASTStreamConstructorExpression expr) {
    SymTypeExpression result;
    List<SymTypeExpression> containedExprTypes = expr.getExpressionList().stream()
      .map(e ->
        //Die einzige Änderung ist, dass hier die Typen geboxed werden
        de.monticore.lang.sysmlv2.types3.SysMLSymTypeRelations.box(
          getType4Ast().getPartialTypeOfExpr(e)
        )
      )
      .collect(Collectors.toList());
    Optional<SymTypeExpression> givenElementType =
      expr.isPresentMCTypeArgument() ?
        Optional.of(getType4Ast().getPartialTypeOfTypeId(expr.getMCTypeArgument())) :
        Optional.empty();
    if (containedExprTypes.stream().anyMatch(SymTypeExpression::isObscureType) ||
        givenElementType.stream().anyMatch(SymTypeExpression::isObscureType)) {
      result = createObscureType();
    }
    else if (getType4Ast().hasTypeOfExpression(expr)) {
      // type already calculated
      return;
    }
    else {
      if (containedExprTypes.isEmpty() && givenElementType.isEmpty()) {
        Log.error("0xFD577 empty stream without"
            + " explicit element type argument is not supported."
            + " Add a type argument (e.g., '<>' -> '<int><>')",
          expr.get_SourcePositionStart(),
          expr.get_SourcePositionEnd()
        );
        result = createObscureType();
      }
      else {
        boolean hasBadElem = false;
        if (givenElementType.isPresent()) {
          for (SymTypeExpression containedExprType : containedExprTypes) {
            if (!SymTypeRelations.isCompatible(givenElementType.get(), containedExprType)) {
              Log.error("0xFD578 " +
                  "stream with explicit element type "
                  + givenElementType.get().printFullName()
                  + " contains incompatible expression of type "
                  + containedExprType.printFullName(),
                expr.get_SourcePositionStart(),
                expr.get_SourcePositionEnd()
              );
              hasBadElem = true;
            }
          }
        }
        if (hasBadElem) {
          result = createObscureType();
        }
        else {
          SymTypeExpression elementType = givenElementType
            .orElseGet(() -> createUnion(Set.copyOf(containedExprTypes)));
          if (expr.isEventTimed()) {
            result = StreamSymTypeFactory.createEventStream(elementType);
          }
          else if (expr.isSyncTimed()) {
            result = StreamSymTypeFactory.createSyncStream(elementType);
          }
          else if (expr.isToptTimed()) {
            result = StreamSymTypeFactory.createToptStream(elementType);
          }
          else {
            result = StreamSymTypeFactory.createUntimedStream(elementType);
          }
        }
      }
    }
    getType4Ast().setTypeOfExpression(expr, result);
  }
}
