package de.monticore.lang.sysmlv2.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.generics.bounds.Bound;

import java.util.Collections;
import java.util.List;

public class SysMLSymTypeCompatibilityCalculator extends de.monticore.types3.util.SymTypeCompatibilityCalculator{

  @Override
  protected List<Bound> constrainCompatiblePreNormalized(
      SymTypeExpression target,
      SymTypeExpression source
  ) {
    List<Bound> result;
    if (target.isTypeVariable() ||
        target.isInferenceVariable() ||
        target.isWildcard() ||
        source.isTypeVariable() ||
        source.isInferenceVariable() ||
        source.isWildcard()
    ) {
      result = typeSetConstrainCompatible(target, source);
    }
    else if (source.deepEquals(target)) {
      result = Collections.emptyList();
    }
    else if (source.isNullType()) {
      result = nullConstrainCompatible(target, source.asNullType());
    }
    else if (source.isArrayType()
        && target.isGenericType()
        && target.asGenericType().getTypeConstructorFullName()
          .equals("Collections.OrderedCollection")
        && target.asGenericType().getArgumentList().size() == 1
    ) {
      // T[] repräsentiert ein Array als SymTypeArray, aber ohne eigenes
      // Array-Typsymbol. Für den Bibliothekstyp gilt
      // Collections.Array<T> <: Collections.OrderedCollection<T>;
      // diese Beziehung ist im Symbol hinterlegt und wird durch die reguläre
      // Subtypprüfung erkannt.
      // Die Sonderregel erlaubt auch T[] als Eingabe für Collections.OrderedCollection<T>
      SymTypeExpression collectionView = SymTypeExpressionFactory.createGenerics(
          target.getTypeInfo(), source.asArrayType().cloneWithLessDim(1));
      result = constrainSubTypeOf(collectionView, target);
    }
    else {
      SymTypeExpression boxedTarget = de.monticore.lang.sysmlv2.types3.SysMLSymTypeRelations.box(target);
      SymTypeExpression boxedSource = de.monticore.lang.sysmlv2.types3.SysMLSymTypeRelations.box(source);

      result = constrainSubTypeOf(source, target);
      if (result.stream().anyMatch(Bound::isUnsatisfiableBound)) {
        result = constrainSubTypeOf(boxedSource, target);
      }
      if (result.stream().anyMatch(Bound::isUnsatisfiableBound)) {
        result = constrainSubTypeOf(source, boxedTarget);
      }
      if (result.stream().anyMatch(Bound::isUnsatisfiableBound)) {
        result = constrainSubTypeOf(boxedSource, boxedTarget);
      }
    }
    if (result.stream().anyMatch(Bound::isUnsatisfiableBound)) {
      if (target.isRegExType() || source.isRegExType()) {
        result = regExConstrainCompatible(target, source);
      }
    }
    return result;
  }
}
