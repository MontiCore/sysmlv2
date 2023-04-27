package de.monticore.lang.sysmlv2._symboltable;

import java.util.*;

import de.monticore.lang.sysmlimportsandpackages._ast.ASTSysMLImportStatement;
import de.monticore.lang.sysmlv2.generator.utils.resolve.ImportUtils;
import de.monticore.symboltable.ImportStatement;

public class SysMLv2ScopesGenitorDelegator extends SysMLv2ScopesGenitorDelegatorTOP {

  public ISysMLv2ArtifactScope createFromAST(de.monticore.lang.sysmlv2._ast.ASTSysMLModel rootNode) {
    ISysMLv2ArtifactScope as = symbolTable.createFromAST(rootNode);
    if(as.isPresentName()) {
      if(!as.getPackageName().isEmpty()) {
        globalScope.addLoadedFile(as.getPackageName() + "." + as.getName());
      }
      else {
        globalScope.addLoadedFile(as.getName());
      }
    }
    List<ImportStatement> mcImportStatements = new ArrayList<>();
    List<ASTSysMLImportStatement> sysMLImportStatements = ImportUtils.getImportStatements(rootNode);
    for (ASTSysMLImportStatement sysMLImportStatement : sysMLImportStatements) {
      ImportStatement importStatement = new ImportStatement(sysMLImportStatement.getQName(), true);
      mcImportStatements.add(importStatement);
    }
    as.setImportsList(mcImportStatements);
    return as;
  }

}


