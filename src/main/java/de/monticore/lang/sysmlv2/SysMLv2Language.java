package de.monticore.lang.sysmlv2;

import de.monticore.lang.sysmlrequirementdiagrams._cocos.AssertConstraintNotAllowedInRequirement;
import de.monticore.lang.sysmlrequirementdiagrams._cocos.AtMostSingleSubjectInRequirement;
import de.monticore.lang.sysmlrequirementdiagrams._visitor.RequirementsPostProcessor;
import de.monticore.lang.sysmlv2._ast.ASTSysMLModel;
import de.monticore.lang.sysmlv2._cocos.SysMLv2CoCoChecker;
import de.monticore.lang.sysmlv2._parser.SysMLv2Parser;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2GlobalScope;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SysMLv2Language {

  public static final String FILE_ENDING = "sysml";

  /**
   * Parses, checks and creates symbol table for all models residing in the {@code modelLocation}.
   *
   * @param modelLocation Location of root directory of models
   */
  public static ISysMLv2GlobalScope getGlobalScopeFor(Path modelLocation) throws IOException {
    return getGlobalScopeFor(modelLocation, false);
  }


  /**
   * Parses, checks depending on {@code unchecked} and creates symbol table for all models residing in the
   * {@code modelLocation}.
   *
   * @param modelLocation Location of root directory of models
   * @param unchecked     Whether to run unchecked (i.e., no CoCos) or not
   */
  public static ISysMLv2GlobalScope getGlobalScopeFor(Path modelLocation, boolean unchecked) throws IOException {
    SysMLv2Mill.init();
    SysMLv2Mill.globalScope().clear();

    // 01. Parse Models
    SysMLv2Parser parser = SysMLv2Mill.parser();
    List<ASTSysMLModel> models = Files.walk(modelLocation).parallel()
            .filter(path -> !path.toFile().isDirectory())
            .filter(path -> FilenameUtils.getExtension(path.toFile().getName()).equals(FILE_ENDING))
            .map(path ->
            {
              try {
                Optional<ASTSysMLModel> optAst = parser.parse(path.toAbsolutePath().toString());
                if (!optAst.isPresent()) {
                  Log.warn("Empty AST for " + path);
                }
                return optAst;
              } catch (IOException ex) {
                Log.error("Could not find file at " + path, ex);
                return Optional.<ASTSysMLModel>empty();
              }
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    // 02. Check initial CoCos
    if (!unchecked) {
      SysMLv2CoCoChecker checker1 = getPreSymbolTableCoCoChecker();
      models.forEach(checker1::checkAll);
    }

    // 03. Build Symbol Table
    ISysMLv2GlobalScope globalScope = createSymbolTable(models);

    // 04. Perform any postprocessing
    SysMLv2Traverser traverser = SysMLv2Mill.traverser();
    traverser.add4SysMLRequirementDiagrams(new RequirementsPostProcessor());
    for (ASTSysMLModel model : models) {
      model.accept(traverser);
    }

    // 05. Check further CoCos
    if (!unchecked) {
      SysMLv2CoCoChecker checker2 = getPostSymbolTableCoCoChecker();
      models.forEach(checker2::checkAll);
    }

    return globalScope;
  }


  /**
   * Creates SymbolTable for multiple ASTs and returns the resulting global scope.
   * Symbols will only be added to the existing global scope.
   *
   * @param modelASTs List of AST for which the SymbolTable should be built
   */
  public static ISysMLv2GlobalScope createSymbolTable(List<ASTSysMLModel> modelASTs) {
    ISysMLv2GlobalScope globalScope = SysMLv2Mill.globalScope();
    for (ASTSysMLModel astUnit : modelASTs) {
      SysMLv2Mill.scopesGenitorDelegator().createFromAST(astUnit);
    }
    return globalScope;
  }

  /**
   * Creates a SysMLv2CoCoChecker with added CoCos required to be checked
   * before symbol table creation.
   *
   * @return SysMLv2CoCoChecker
   */
  public static SysMLv2CoCoChecker getPreSymbolTableCoCoChecker() {
    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new AssertConstraintNotAllowedInRequirement());
    return checker;
  }


  /**
   * Creates a SysMLv2CoCoChecker with added CoCos required to be checked
   * after symbol table creation.
   *
   * @return SysMLv2CoCoChecker
   */
  public static SysMLv2CoCoChecker getPostSymbolTableCoCoChecker() {
    SysMLv2CoCoChecker checker = new SysMLv2CoCoChecker();
    checker.addCoCo(new AtMostSingleSubjectInRequirement());
    return checker;
  }

}
