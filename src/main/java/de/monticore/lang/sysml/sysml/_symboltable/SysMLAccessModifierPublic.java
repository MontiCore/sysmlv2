package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class SysMLAccessModifierPublic implements AccessModifier {
  @Override
  public boolean includes(AccessModifier accessModifier) {
    return true;
  }
}
