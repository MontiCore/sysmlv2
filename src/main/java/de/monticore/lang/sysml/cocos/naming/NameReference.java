package de.monticore.lang.sysml.cocos.naming;

import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._cocos.SysMLNamesBasisASTQualifiedNameCoCo;
import de.monticore.lang.sysml.basics.sysmlclassifiers._ast.ASTClassifierDeclarationCompletionStd;
import de.monticore.lang.sysml.basics.sysmlclassifiers._ast.ASTSuperclassingList;
import de.monticore.lang.sysml.basics.sysmlclassifiers._cocos.SysMLClassifiersASTClassifierDeclarationCompletionStdCoCo;
import de.monticore.lang.sysml.basics.sysmlclassifiers._symboltable.ISysMLClassifiersScope;
import de.monticore.lang.sysml.basics.sysmlclassifiers._symboltable.SysMLClassifiersScope;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlnames._ast.ASTColonQualifiedName;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlnames._ast.ASTDotQualifiedName;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlnames._ast.ASTSimpleName;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlnames._cocos.SysMLNamesASTColonQualifiedNameCoCo;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlnames._symboltable.ISysMLNamesScope;
import de.monticore.lang.sysml.bdd._symboltable.BlockSymbol;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.cocos.SysMLCoCos;
import de.monticore.lang.sysml.sysml._symboltable.SysMLArtifactScope;
import de.monticore.lang.sysml.sysml._symboltable.SysMLGlobalScope;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class NameReference implements SysMLClassifiersASTClassifierDeclarationCompletionStdCoCo {

  /* @Override
  public void check(ASTQualifiedName node) {
    List<ASTSysMLName> fullQualifiedName = new ArrayList<>();
    String reference;
    if(node instanceof ASTSimpleName){
      ASTSimpleName current = (ASTSimpleName) node;
      reference = current.getSysMLName().getName();
    }else if(node instanceof ASTDotQualifiedName) {
      ASTDotQualifiedName current = (ASTDotQualifiedName) node;
      reference = current.getSysMLName(current.getSysMLNameList().size()-1).getName();
      fullQualifiedName = current.getSysMLNameList();
    }else if(node instanceof ASTColonQualifiedName){
      ASTColonQualifiedName current = (ASTColonQualifiedName) node;
      reference = current.getSysMLName(current.getSysMLNameList().size()-1).getName();
      fullQualifiedName = current.getSysMLNameList();
    }else {
      Log.error("Internal error. Please add a coco for the this Qualified Name instance " + node.toString() +
          " in class " + this.getClass().getName());
      return;
    }

    System.out.println("Checking to resolve name " + reference);
  }*/

  @Override
  public void check(ASTClassifierDeclarationCompletionStd node) {
    if(!node.isPresentSuperclassingList()){
      Log.info("Node has no superclassing " + node.getName(), this.getClass().getName());
      return;
    }
    ASTSuperclassingList superclassing = node.getSuperclassingList();
    for (ASTQualifiedName qualifiedName : superclassing.getQualifiedNameList()) {
      List<ASTSysMLName> fullQualifiedName = new ArrayList<>();
      String reference;
      if(qualifiedName instanceof ASTSimpleName){
        ASTSimpleName current = (ASTSimpleName) qualifiedName;
        reference = current.getSysMLName().getName();
      }else if(qualifiedName instanceof ASTDotQualifiedName) {
        ASTDotQualifiedName current = (ASTDotQualifiedName) qualifiedName;
        reference = current.getSysMLName(current.getSysMLNameList().size()-1).getName();
        fullQualifiedName = current.getSysMLNameList();
      }else if(qualifiedName instanceof ASTColonQualifiedName){
        ASTColonQualifiedName current = (ASTColonQualifiedName) qualifiedName;
        reference = current.getSysMLName(current.getSysMLNameList().size()-1).getName();
        fullQualifiedName = current.getSysMLNameList();
      }else {
        Log.error("Internal error. Please add a coco for the this Qualified Name instance " + node.toString() +
            " in class " + this.getClass().getName());
        return;
      }

      Log.info("Checking to resolve name " + reference, this.getClass().getName());
      ISysMLClassifiersScope scope =  node.getEnclosingScope();
      /*SysMLClassifiersScope scope;
      if(iSysMLClassifiersScope instanceof SysMLClassifiersScope){
        scope = (SysMLClassifiersScope) iSysMLClassifiersScope;
      }else {
        Log.error("Internal error. Could not cast to SysMLArtifactScope." + this.getClass().getName());
        return;
      }*/
      Optional<TypeSymbol> type = scope.resolveType(reference);
      if(type.isPresent()){
        Log.info("Block could be resolved. " + reference, this.getClass().getName());
      }else {
        Log.error(SysMLCoCos.getErrorCode(SysMLCoCoName.NameReference) + " "+
            node.getName() + " does superclass "
            + " " +reference +", but " + reference + " could not be resolved.");
      }
      // node.getEnclosingScope().resolveType(reference);
      //TODO
    }

  }
}
