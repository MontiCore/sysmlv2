/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sysml.sysml._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;

/**
 * @author Robin Muenstermann
 * @version 1.0
 *
 * check with instance of to resolve if symbol is private
 */
public class SysMLAccessModifierPrivate implements AccessModifier {
  @Override
  public boolean includes(AccessModifier accessModifier) {
    return true;
  }
}
