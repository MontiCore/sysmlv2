package de.monticore.lang.sysmlv2.types3;

import de.monticore.expressions.streamexpressions._ast.ASTStreamConstructorExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.streams.StreamSymTypeFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createUnion;

public class SysMLStreamExpressionsTypeVisitor extends de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor {
  @Override
  public void endVisit(ASTStreamConstructorExpression expr) {
    super.endVisit(expr);

    SymTypeExpression result;
    boolean isNotObscure = !getType4Ast().getTypeOfExpression(expr).isObscureType();

    /* If expr is a valid StreamConstructorExpression, then super has
     * successfully built a streamtype 'Stream<t>'. We then extract those types
     * 't' and "repackage" them in a new stream with now boxed types 'Stream<T>'.
     * This process is invisible to an outside Observer.
     */
    if (isNotObscure) {
      List<SymTypeExpression> argumentList = getType4Ast()
        .getTypeOfExpression(expr)
        .asGenericType()
        .getArgumentList();

      if (argumentList.size() != 1) {
        //Something is amiss... abort
        return;
      }

      List<SymTypeExpression> boxedContainedExprTypes =
        argumentList.get(0).asUnionType()
        .getUnionizedTypeSet().stream()
        .map(SysMLSymTypeRelations::box)
        .collect(Collectors.toList());

      SymTypeExpression elementType = createUnion(Set.copyOf(boxedContainedExprTypes));

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

      getType4Ast().setTypeOfExpression(expr, result);
    }
  }
}
