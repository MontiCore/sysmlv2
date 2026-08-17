package de.monticore.lang.sysmlv2.types3;

import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.util.BuiltInTypeRelations;
import de.monticore.types3.util.SymTypeCompatibilityCalculator;
import de.monticore.types3.util.SymTypeRelationsDefaultDelegatee;

import java.util.HashSet;
import java.util.Set;

public abstract class SysMLSymTypeRelations extends OCLSymTypeRelations {

  public static void init() {
    setDelegate(new SysMLSymTypeRelationsDelegatee());
  }

  // selecting the concrete implementations
  protected static class SysMLSymTypeRelationsDelegatee extends
      SymTypeRelationsDefaultDelegatee {
    public SysMLSymTypeRelationsDelegatee() {
      builtInRelationsDelegate = new BuiltInTypeRelations() {
        @Override
        public boolean isIntegralType(SymTypeExpression type) {
          return super.isIntegralType(type) ||
              type.isPrimitive() &&
                  type.asPrimitive().getPrimitiveName().equals("nat");
        }

        /*
        Any Type that is included here will be compatible with any other Type
        in the list.
         */
        @Override
        public boolean isBoolean(SymTypeExpression type) {
          return (super.isBoolean(type) ||
            (
              type.hasTypeInfo() &&
              type.getTypeInfo().getFullName().equals("ScalarValues.Boolean")
            )
          );
        }
      };

      compatibilityDelegate = new SymTypeCompatibilityCalculator() {
        @Override
        public boolean isCompatible(
          SymTypeExpression target,
          SymTypeExpression source
        ) {
          return (super.isCompatible(target, source) ||
            (
              builtInRelationsDelegate.isBoolean(target) &&
              builtInRelationsDelegate.isBoolean(source)
            ) ||
            areCompatibleGenericTypes(target, source)
          );
        }

        /**
         * Prüft, ob zwei generische Typen miteinander kompatibel sind.
         * Zwei generische Typen gelten als kompatibel, wenn:
         * - beide Seiten generische Typen sind
         * - denselben Basistyp besitzen
         * - gleich viele Typargumente haben und jedes Typargument paarweise
         *   kompatibel ist
         */
        private boolean areCompatibleGenericTypes(
          SymTypeExpression target,
          SymTypeExpression source
        ) {
          if (!target.isGenericType() || !source.isGenericType()) {
            return false;
          }

          var targetGeneric = target.asGenericType();
          var sourceGeneric = source.asGenericType();

          if (!targetGeneric.getTypeInfo().getFullName()
              .equals(sourceGeneric.getTypeInfo().getFullName())) {
            return false;
          }

          if (targetGeneric.getArgumentList().size()
              != sourceGeneric.getArgumentList().size()) {
            return false;
          }

          for (int i = 0; i < targetGeneric.getArgumentList().size(); i++) {
            var targetArgument = targetGeneric.getArgument(i);
            var sourceArgument = sourceGeneric.getArgument(i);

            if (!areCompatibleGenericArguments(targetArgument, sourceArgument)) {
              return false;
            }
          }

          return true;
        }

        /**
         * Prüft, ob zwei Typargumente eines generischen Typs kompatibel sind.
         * Vor dem Vergleich werden beide Argumente normalisiert. Falls eines
         * der normalisierten Argumente ein Union-Typ ist, müssen beide Seiten
         * Union-Typen sein.
         * In allen anderen Fällen wird die normale Typkompatibilität verwendet.
         */
        private boolean areCompatibleGenericArguments(
          SymTypeExpression targetArgument,
          SymTypeExpression sourceArgument
        ) {
          var normalizedTarget = normalize(targetArgument);
          var normalizedSource = normalize(sourceArgument);

          if (normalizedTarget.isUnionType() || normalizedSource.isUnionType()) {
            return normalizedTarget.isUnionType()
                && normalizedSource.isUnionType()
                && areCompatibleUnionTypes(normalizedTarget, normalizedSource);
          }

          return isCompatible(normalizedTarget, normalizedSource);
        }

        /**
         * Prüft, ob zwei Union-Typen kompatibel sind.
         *
         * Das ist nötig, weil z. B. {@code <5, true>} als generischer Typ mit genau
         * einem Typargument {@code Union(int, boolean)} betrachtet wird.
         */
        private boolean areCompatibleUnionTypes(
          SymTypeExpression target,
          SymTypeExpression source
        ) {
          Set<SymTypeExpression> targetTypes =
              new HashSet<>(target.asUnionType().getUnionizedTypeSet());
          Set<SymTypeExpression> sourceTypes =
              new HashSet<>(source.asUnionType().getUnionizedTypeSet());

          if (targetTypes.size() != sourceTypes.size()) {
            return false;
          }

          for (SymTypeExpression targetType : targetTypes) {
            var matchingSourceType = sourceTypes.stream()
              .filter(
                sourceType -> isCompatible(targetType, sourceType)
              )
              .findAny();

            if (matchingSourceType.isEmpty()) {
              return false;
            }

            sourceTypes.remove(matchingSourceType.get());
          }

          return sourceTypes.isEmpty();
        }
      };
    }
  }
}
