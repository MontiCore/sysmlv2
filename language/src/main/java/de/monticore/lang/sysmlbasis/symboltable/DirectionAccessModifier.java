package de.monticore.lang.sysmlbasis.symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.Map;

/**
 * Anleitung zur Verwendung von Modifiers in MontiCore-Handbuch und auf der
 * Website sind unklar.
 *
 * 1. Anlegen einer Subklasse von AccessModifier
 * 1.1. Kopiert von de.monticore.symboltable.modifiers.ReadableAccessModifier
 * 2. Anlegen und registrieren eines ST-Completers für die AST-Klassen mit
 *    Modifier
 * 2.1. Unklar wie und wo
 */
public enum DirectionAccessModifier implements AccessModifier {

  IN {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier direction = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(direction != null) {
        return direction.equals(IN);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }
  },

  OUT {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier direction = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(direction != null) {
        return direction.equals(OUT);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }
  },

  INOUT {
    @Override
    public boolean includes(AccessModifier modifier) {
      AccessModifier direction = modifier.getDimensionToModifierMap().get(DIMENSION);
      if(direction != null) {
        return direction.equals(INOUT);
      }
      return true;
    }

    @Override
    public Map<String, AccessModifier> getDimensionToModifierMap() {
      return Map.of(DIMENSION, this);
    }
  }
  ;

  public static final String DIMENSION = "Direction";

}
