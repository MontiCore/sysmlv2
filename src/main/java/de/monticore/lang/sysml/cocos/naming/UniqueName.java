package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.basics.sysmlnamesbasis._ast.ASTSysMLType;
import de.monticore.lang.sysml.basics.sysmlnamesbasis._ast.ResolveQualifiedNameHelper;
import de.monticore.lang.sysml.basics.sysmlnamesbasis._cocos.SysMLNamesBasisASTSysMLTypeCoCo;
import de.monticore.lang.sysml.basics.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class UniqueName implements SysMLNamesBasisASTSysMLTypeCoCo {
  @Override
  public void check(ASTSysMLType node) {
    if (node.getName().equals("")) {
      return;
    }
    //All name definitions in a scope have to be unique. Multiple same packages in different artifacts is okay, but
    // not in the same artifact or two equal package definitions in the same enclosing scope (e.g., in a package).
    List<SysMLTypeSymbol> symbolsWithEqualName = ResolveQualifiedNameHelper.
        resolveNameAsSysMLType(node.getName(), node.getEnclosingScope());
    if (symbolsWithEqualName.size() != 1) {
      if (symbolsWithEqualName.size() == 0) {
        Log.error("Internal error. Resolved a symbol in its own scope and could not resolve it. " + this.getClass().getName());
      }
      else {
        String allnames = new String();
        for (SysMLTypeSymbol symbol : symbolsWithEqualName) {
          allnames += ("," + symbol.getSourcePosition().toString());
        }
        allnames = allnames.substring(1);
        Log.warn(SysMLCoCos.getErrorCode(SysMLCoCoName.UniqueName) + " " + "Name \"" + node.getName() + "\" is not " + "unique in " + "its scope. Check definitions in scope at: " + allnames);
      }
    }
    else {
      return;
    }
  }
}
