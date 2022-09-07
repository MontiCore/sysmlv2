package de.monticore.lang.sysml4verification;

import de.monticore.io.paths.MCPath;
import de.monticore.lang.sysml4verification._ast.ASTSysMLModel;
import de.monticore.lang.sysml4verification._cocos.SysML4VerificationCoCoChecker;
import de.monticore.lang.sysml4verification._parser.SysML4VerificationParser;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationGlobalScope;
import de.monticore.lang.sysml4verification._symboltable.ISysML4VerificationScope;
import de.monticore.lang.sysmlblockdiagrams._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SysML4VerificationLanguage {

  public static final String FILE_ENDING = "sysml";

  public static final String SYMBOLTABLE_ENDING = ".*sym";

  /**
   * Recursively finds all part definitions ("blocks") in the provided global scope.
   */
  public static List<PartDefSymbol> findPartDefinitionsIn(ISysML4VerificationScope globalScope) {
    List<PartDefSymbol> components = new ArrayList<>(globalScope.getLocalPartDefSymbols());
    for (ISysML4VerificationScope s : globalScope.getSubScopes()) {
      components.addAll(findPartDefinitionsIn(s));
    }
    return components;
  }

  /**
   * Recursively finds all requirement definitions in the provided global scope.
   */
  public static List<RequirementDefSymbol> findRequirementDefinitionsIn(ISysML4VerificationScope globalScope) {
    List<RequirementDefSymbol> components = new ArrayList<>(globalScope.getLocalRequirementDefSymbols());
    for (ISysML4VerificationScope s : globalScope.getSubScopes()) {
      components.addAll(findRequirementDefinitionsIn(s));
    }
    return components;
  }

  /**
   * Parses, checks and creates symbol table for all models residing in the {@code modelLocation}.
   *
   * @param modelLocation Location of root directory of models
   */
  public static ISysML4VerificationGlobalScope getGlobalScopeFor(Path modelLocation) throws IOException {
    return getGlobalScopeFor(modelLocation, modelLocation);
  }

  /**
   * Parses, checks and creates symbol table for all models residing in the {@code modelLocation} and
   * sets the {@code symboltableLocation} of stored symboltables.
   * @param modelLocation Location of root directory of models
   * @param symboltableLocation Location of root directory of stored symboltables
   */
  public static ISysML4VerificationGlobalScope getGlobalScopeFor(Path modelLocation, Path symboltableLocation) throws IOException {
    return getGlobalScopeFor(modelLocation, symboltableLocation, false);
  }

  /**
   * Parses, checks depending on {@code unchecked} and creates symbol table for all models residing in the
   * {@code modelLocation}.
   *
   * @param modelLocation Location of root directory of models
   * @param unchecked Whether to run unchecked (i.e., no CoCos) or not
   */
  public static ISysML4VerificationGlobalScope getGlobalScopeFor(Path modelLocation, Path symboltableLocation, boolean unchecked) throws IOException {
    // 00. Initilize Mills
    SysML4VerificationMill.init(); // Dieses Statement cleart Teile des globalScope (das anscheinend zwischen allen Sprachen ausgetauscht wird)
    SysML4VerificationMill.globalScope().clear(); // Explizit auch die restlichen Teile clearen
    BasicSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives(); // Dieses fügt zB. "boolean" ein -> darf nicht vorher gemacht werden

    // 00. Initialize ModelPath for GlobalScope such that symbol resolution loads all stored symbol tables if symbol is not found
    MCPath symboltablePath = new MCPath(Files.walk(symboltableLocation).collect(Collectors.toList()));

    // 01. Parse Models
    SysML4VerificationParser parser = SysML4VerificationMill.parser();
    List<ASTSysMLModel> models = Files.walk(modelLocation).parallel()
        .filter(path -> !path.toFile().isDirectory())
        .filter(path -> FilenameUtils.getExtension(path.toFile().getName()).equals(FILE_ENDING))
        .map(path ->
        {
          try {
            Optional<ASTSysMLModel> optAst = parser.parse(path.toAbsolutePath().toString());
            if(optAst.isEmpty()) {
              Log.warn("Empty AST for " + path);
            }
            return optAst;
          }
          catch (IOException ex) {
            Log.error("Could not find file at " + path, ex);
            return Optional.<ASTSysMLModel>empty();
          }
        })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    // 02. Check initial CoCos
    if(!unchecked) {
      SysML4VerificationCoCoChecker checker1 = SysML4VerificationCoCoChecker.beforeSymbolTableCreation();
      models.forEach(checker1::checkAll);
    }

    // 03. Build Symbol Table
    ISysML4VerificationGlobalScope globalScope = createSymboltable(models, symboltablePath);

    // 05. Check further CoCos
    if(!unchecked) {
      SysML4VerificationCoCoChecker checker2 = SysML4VerificationCoCoChecker.afterSymbolTableCreation();
      models.forEach(checker2::checkAll);
    }

    return globalScope;
  }


  /**
   * Creates Symboltable for multiple ast's and returns the resulting global scope. Symbols will only be added to the
   * global scope. Clear the scope yourself, if you need an empty scope. Beware of missing basic symbols when you do.
   *
   * @param modelASTs List of Ast for which the symboltable should be build
   */
  public static ISysML4VerificationGlobalScope createSymboltable(List<ASTSysMLModel> modelASTs, MCPath symboltablePath) {
    // The global scope is filled by scopesGenitorDelegator().createFromAST(astUnit) below
    ISysML4VerificationGlobalScope globalScope = SysML4VerificationMill.globalScope();
    globalScope.setSymbolPath(symboltablePath);
    globalScope.setFileExt(SYMBOLTABLE_ENDING);

    // Create Symboltables for all AST nodes
    for (ASTSysMLModel astUnit : modelASTs) {
      // build symbols + fill global scope
      SysML4VerificationMill.scopesGenitorDelegator().createFromAST(astUnit);
    }

    return globalScope;
  }

}
