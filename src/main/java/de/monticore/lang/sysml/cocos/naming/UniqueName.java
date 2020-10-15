package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLType;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._cocos.SysMLNamesBasisASTSysMLTypeCoCo;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class UniqueName implements SysMLNamesBasisASTSysMLTypeCoCo {
  @Override
  public void check(ASTSysMLType node) {
    // System.out.println("Visiting node " + node.getName());
    if(node.getName().equals("")){
      return;
    }
    List<SysMLTypeSymbol> symbols = node.getEnclosingScope().resolveSysMLTypeMany(node.getName());
    // System.out.println("Resolved following symbols for " + node.getName() + symbols.toString());
    if(symbols.size() != 1){
      if(symbols.size() == 0 ){
        Log.error("Internal error. Resolved a symbol in its own scope and could not resolve it. "+
            this.getClass().getName());
      }else {
        String allnames = new String();
        for(SysMLTypeSymbol symbol: symbols){
          allnames += ("," + symbol.getSourcePosition().toString());
        }
        allnames = allnames.substring(1);
        Log.error( SysMLCoCos.getErrorCode(SysMLCoCoName.NameReference) + " "+
            "Name " + node.getName() + " is not unique in its scope. Check definitions in scope at: " +
                allnames
            );
      }

    }
  }
}
