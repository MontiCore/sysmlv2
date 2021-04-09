package de.monticore.lang.sysml.sysml._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.lang.sysml.basics.interfaces.sysmlimportbasis._ast.ASTImportUnit;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTQualifiedName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._ast.ASTSysMLName;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.ISysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLNamesBasisScope;
import de.monticore.lang.sysml.basics.interfaces.sysmlnamesbasis._symboltable.SysMLTypeSymbol;
import de.monticore.lang.sysml.basics.interfaces.sysmlshared._ast.ASTUnit;
import de.monticore.lang.sysml.basics.interfaces.sysmlvisibilitybasis._ast.ASTPackageElementVisibilityIndicator;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTAliasPackagedDefinitionMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTImportUnitStd;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackage;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._ast.ASTPackageMember;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlimportsandpackages._visitor.SysMLImportsAndPackagesVisitor2;
import de.monticore.lang.sysml.basics.sysmldefault.sysmlvisibility._ast.ASTPackageElementVisibilityIndicatorStd;
import de.monticore.lang.sysml.cocos.CoCoStatus;
import de.monticore.lang.sysml.cocos.SysMLCoCoName;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverser;
import de.monticore.lang.sysml.sysml._visitor.SysMLTraverserImplementation;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * @author Robin Muenstermann
 * @version 1.0
 */
public class AddImportToScopeVisitor implements SysMLImportsAndPackagesVisitor2 {
  int phase = 0;

  SysMLTraverser traverser = null;

  public AddImportToScopeVisitor(){}

  public AddImportToScopeVisitor(SysMLTraverser traverser) {
    this.traverser = traverser;
    this.traverser.add4SysMLImportsAndPackages(this);
  }

  public void init() {
    if(traverser != null)
      return;
    this.traverser = new SysMLTraverserImplementation();
    traverser.add4SysMLImportsAndPackages(this);
  }

  public void memorizeImportsPhase1of5(ASTUnit ast) { // Resolves all qualified names for imports.
    this.phase = 1;
    init();
    ast.accept(traverser);
  }

  public void addReexportedSymbolsOfPackagesPhase3of5(ASTUnit ast) {
    // Collects all imports recusively, which are exported from a packedDefinitionMember with public.
    // Because of phase 1 we can assume, that all imports are already resolved here.
    // This works basically like this:
    //   if the current import resolves to a package, we check all members of this package,
    //   if a member is a ASTUnitImport or AST Alias Import we check the visibility. If it is public
    //   we import all symbols from that package and do this again for the import.
    //   We manage symbols in a Set => no adding the same symbol twice.
    //   We save all visited import statements, so that we do not run into infinity loops, if two packages import
    //   each other.
    this.phase = 2;
    init();
    ast.accept(traverser);
  }

  public void addImportsToScopePhase5of5(ASTUnit ast) { // Adds all resolved imports to the enclosing scope.
    this.phase = 3;
    init();
    ast.accept(traverser);
  }

  @Override
  public void visit(ASTAliasPackagedDefinitionMember node) {

    if (phase == 1) {
      if (node.isAlias() && node.getQualifiedName().getNamesList().size() == 1) { //Just renaming of current scope
        List<SysMLTypeSymbol> resolvedTypes = new ArrayList<>();
        resolvedTypes.addAll(node.getEnclosingScope().resolveSysMLTypeMany(node.getQualifiedName().getReferencedName()));
        node.setResolvedTypes(resolvedTypes);
      }
      else {
        List<SysMLTypeSymbol> resolvedTypes = node.getQualifiedName().resolveSymbols();
        node.setResolvedTypes(resolvedTypes);
      }
    }
    else if (phase == 2) {
      // node.setTransitiveImports(getTransitiveImports(node.getResolvedTypes())); We can skip this, because an ALias
      // does not have a star import.
    }
    else if (phase == 3) {
      Optional<ASTSysMLName> importAs = Optional.empty();
      Optional<SysMLTypeSymbol> currentType = Optional.empty();
      if (node.isPresentSysMLName()) {
        importAs = Optional.of(node.getSysMLName());
        currentType = Optional.of(node.getSymbol());
      }
      boolean importSuccess = true;
      //Check if alias is necessary
      if(node.getResolvedTypes().size()==1 && !node.isPresentSysMLName() && !node.isAlias()){
        if(node.getEnclosingScope().resolveSysMLTypeMany(node.getResolvedTypes().get(0).getName()).size()>0){
          if(!(node.getResolvedTypes().get(0).getAstNode() instanceof ASTPackage)) {
            //If it is a package, another CoCo catches the case, that an alias should not import an package.
            //Do not add => alias is necessary
            importSuccess = false;
            List<CoCoStatus> warnings = new ArrayList<>();
            warnings.add(new CoCoStatus(SysMLCoCoName.ImportAliasNecessary, "An alias name" + " is required for importing symbols, which are already defined in the importing scope."));
            node.setWarnings(warnings);
          }
        }
      }
      if(importSuccess){ //Add
        List<CoCoStatus> warnings = this.addToScope(node.getResolvedTypes(), node.getTransitiveImports(),
            node.getEnclosingScope(), false, importAs, currentType, node.getQualifiedName(),
            node.isAlias(), node.getWarnings());
        node.setWarnings(warnings);
      }
    }
    else {
      Log.error("Internal error in " + this.getClass().getName() + " Do not call this Visitor with ast.accept. There "
          + "are explicit methods.");
    }
  }

  @Override
  public void visit(ASTImportUnitStd node) {
    if (phase == 1) {
      List<SysMLTypeSymbol> resolvedTypes = node.getQualifiedName().resolveSymbols();
      node.setResolvedTypes(resolvedTypes);
    }
    else if (phase == 2) {
      if (node.isStar()) {
        node.setTransitiveImports(getTransitiveImports(node.getResolvedTypes()));
        //Adding symbol of the first package:
        for (SysMLTypeSymbol shouldBePackage : node.getResolvedTypes()) {
          if (!(shouldBePackage.getAstNode() instanceof ASTPackage)) {
            node.getWarnings().add(new CoCoStatus(SysMLCoCoName.PackageImportWithStar, "Importing a package " +
                "without a " + "star (e.g.\"::*\") will have no effect. " + "If this Statement imports something " +
                "else then a package, this should not be a star import."));
          }
          else {
            ASTPackage astPackage = (ASTPackage) shouldBePackage.getAstNode();
            ISysMLNamesBasisScope importThis = astPackage.getPackageBody().getSpannedScope();
            LinkedListMultimap<String, SysMLTypeSymbol> imports = importThis.getSysMLTypeSymbols();
            for (SysMLTypeSymbol importSymbol : imports.values()) {
              if (!isAlreadyInScopeAndAddWarning(node.getEnclosingScope(), importSymbol, node.getWarnings(), false)) {
                if (!(importSymbol.getAccessModifier() instanceof SysMLAccessModifierPrivate)) {
                  // Do not import private symbols
                  node.getTransitiveImports().add(importSymbol);
                  //System.out.println("Adding symbol to transitive imports " + importSymbol.getName());
                }
              }
            }
          }
        }
        //System.out.print("There were " + node.getTransitiveImports().size() + " transitive import symbols.");
      }

    }
    else if (phase == 3) {
      Optional<ASTSysMLName> importAs = Optional.empty();
      Optional<SysMLTypeSymbol> currentType = Optional.empty();
      List<CoCoStatus> warnings = this.addToScope(node.getResolvedTypes(), node.getTransitiveImports(),
          node.getEnclosingScope(), node.isStar(), importAs, currentType, node.getQualifiedName(), false, node.getWarnings());
      node.setWarnings(warnings);
    }
    else {
      Log.error("Internal error in " + this.getClass().getName() + " Do not call this Visitor with ast.accept. There "
          + "are explicit methods.");
    }
  }

  public List<CoCoStatus> addToScope(List<SysMLTypeSymbol> resolvedTypes,
      List<SysMLTypeSymbol> transitiveImportedTypes, ISysMLNamesBasisScope scopeToAddTo, boolean starImport,
      Optional<ASTSysMLName> importAs, Optional<SysMLTypeSymbol> importAsCorrespondingSymbol,
      ASTQualifiedName importName, boolean onlyAlias,  List<CoCoStatus> existingWarnings) {
    List<CoCoStatus> warnings = new ArrayList<>();
    if (importIsOfTypeKerML(resolvedTypes, importName)) {
      // Do nothing, import is fine.
    }
    else if (resolvedTypes.size() == 0 && existingWarnings.size()==0) {
      warnings.add(new CoCoStatus(SysMLCoCoName.ImportResolves,
          "Could not resolve import \"" + importName.getFullQualifiedName() + "\"."));
    }
    else if (resolvedTypes.size() == 0 && starImport) {
      for (SysMLTypeSymbol importTransitiveSymbol : transitiveImportedTypes) {
        //System.out.println("Adding import of transitive symbol " + importTransitiveSymbol.getName());
        if (!isAlreadyInScopeAndAddWarning(scopeToAddTo, importTransitiveSymbol, warnings, false)) {
          scopeToAddTo.add(importTransitiveSymbol);
        }
      }
    }
    else if (resolvedTypes.size() == 1 && resolvedTypes.get(0).getAstNode() instanceof ASTPackage) {
      //Importing a package.
      ASTPackage astPackage = (ASTPackage) resolvedTypes.get(0).getAstNode();
      ISysMLNamesBasisScope importThis = astPackage.getPackageBody().getSpannedScope();
      if (starImport) {
        /*LinkedListMultimap<String, SysMLTypeSymbol> imports = importThis.getSysMLTypeSymbols();
        for (SysMLTypeSymbol importSymbol : imports.values()) {
          if (!isAlreadyInScopeAndAddWarning(scopeToAddTo, importSymbol.getName(), warnings, false)) {
            if (!(importSymbol.getAccessModifier() instanceof SysMLAccessModifierPrivate)) {
              // Do not import private symbols
              scopeToAddTo.add(importSymbol);
            }
          }
        }*/
        for (SysMLTypeSymbol importTransitiveSymbol : transitiveImportedTypes) {
          //System.out.println("Adding import of transitive symbol " + importTransitiveSymbol.getName());
          if (!isAlreadyInScopeAndAddWarning(scopeToAddTo, importTransitiveSymbol, warnings, false)) {
            scopeToAddTo.add(importTransitiveSymbol);
          }
        }
      }
      else {
        warnings.add(new CoCoStatus(SysMLCoCoName.PackageImportWithStar,
            "Importing a package without a star (e.g" + ".\"::*\") will have no effect. " + "If this Statement "
                + "imports something else then a package, this should not be a star import"));
      }
    }
    else if (resolvedTypes.size() > 1) {
      warnings.add(new CoCoStatus(SysMLCoCoName.NoAmbiguousImport, "The import statement was ambiguous, nothing will "
          + "be" + " imported. "));
    }
    else if (resolvedTypes.size() == 1) {
      if (importAs.isPresent()) {
        if (!importAsCorrespondingSymbol.isPresent()) {
          Log.error("Internal error \"Import as, but no given TypeSymbol\" in " + this.getClass().getName());
        }
        else {
          if (onlyAlias) {
            importAsCorrespondingSymbol.get().setAstNode(resolvedTypes.get(0).getAstNode());
          }
          else if (!isAlreadyInScopeAndAddWarning(scopeToAddTo, importAsCorrespondingSymbol.get(), warnings, true)) {
            importAsCorrespondingSymbol.get().setAstNode(resolvedTypes.get(0).getAstNode());
          }

        }
      }
      else {
        if (!isAlreadyInScopeAndAddWarning(scopeToAddTo, resolvedTypes.get(0), warnings, false)) {
          scopeToAddTo.add(resolvedTypes.get(0));
        }
      }

    }
    else if(existingWarnings.size()==0){
      Log.error("Internal Error in " + AddImportToScopeVisitor.class.getName() + ". Unexpected case.");
    }else{
      // Some other check removed the resolved types such as Ambigous Import Check and added warnings
      // => Nothing to do here
    }
    return warnings;
  }

  private ISysMLNamesBasisScope cloneScope(ISysMLNamesBasisScope original){ //This can be extended for cloning deep.
    ISysMLNamesBasisScope clone = (ISysMLNamesBasisScope) new SysMLNamesBasisScope();
    clone.setName(original.getName());
    LinkedListMultimap<String, SysMLTypeSymbol> imports = original.getSysMLTypeSymbols();
    for (SysMLTypeSymbol importSymbol : imports.values()) {
      clone.add(importSymbol);
    }
    return clone;
  }

  private boolean isAlreadyInScopeAndAddWarning(ISysMLNamesBasisScope scopeToAddTo, SysMLTypeSymbol symbolToAdd,
      List<CoCoStatus> warnings, boolean importAs) {
    String name = symbolToAdd.getName();
    //If already in scope by other import
    if(scopeToAddTo.resolveSysMLTypeMany(name).size()==1){
      if(scopeToAddTo.resolveSysMLTypeMany(name).get(0) == symbolToAdd){
        //Exactly the same symbol exists in scope => because of other import -> that is fine, but we do not have to
        // add this. So we do not warn, but also do not add this.
        return true;
      }
    }
    if (name.equals("")) {
      return true;
    }
    if (scopeToAddTo.resolveSysMLTypeMany(name).size() != 0 && !importAs) {
      String scopeName = "";
      if (scopeToAddTo.isPresentName()) {
        scopeName = scopeToAddTo.getName();
      }
      warnings.add(new CoCoStatus(SysMLCoCoName.ImportedElementNameAlreadyExists, "The element \"" + name + "\" "
          + "could" + " not be imported, because it already exists in the scope " + scopeName + "."));
      return true;
    }
    else if (scopeToAddTo.resolveSysMLTypeMany(name).size() != 1 && importAs) {
      //We should have exactly on type,
      // because of import AS
      String scopeName = "";
      if (scopeToAddTo.isPresentName()) {
        scopeName = scopeToAddTo.getName();
      }
      warnings.add(new CoCoStatus(SysMLCoCoName.ImportedElementNameAlreadyExists, "The element \"" + name + "\" "
          + "could" + " not be imported, because it already exists in the scope " + scopeName + "."));
      return true;
    }
    return false;
  }

  private boolean importIsOfTypeKerML(List<SysMLTypeSymbol> resolvedTypes, ASTQualifiedName importName) {
    // This is of course not complete, but supports most common KerML-imports of SysML.
    if (importName.getReferencedName().equals("ScalarValues")) {
      return true;
    }
    else if (importName.getReferencedName().equals("ScalarFunctions")) {
      return true;
    }
    else if (importName.getReferencedName().equals("Base")) {
      return true;
    }
    else {
      return false;
    }
  }

  private boolean isPrivate(ASTPackageElementVisibilityIndicator visNoCasted) {
    if (visNoCasted instanceof ASTPackageElementVisibilityIndicatorStd) {
      ASTPackageElementVisibilityIndicatorStd vis = (ASTPackageElementVisibilityIndicatorStd) visNoCasted;

      if (vis.getVis().getIntValue() == 2) {
        return true;
      }
    }
    return false;
  }

  //The currentResolvedTypes are the symbols in the importing scope. E.g., package a imports package b, then
  // the currentResolvedTypes are the symbols of package b.
  private List<SysMLTypeSymbol> getTransitiveImports(List<SysMLTypeSymbol> currentResolvedTypes) {
    List<SysMLTypeSymbol> resultingResolvedTypesWithPackages = new ArrayList<>();
    for (SysMLTypeSymbol symbol : currentResolvedTypes) {
      if (symbol.getAstNode() instanceof ASTPackage) {
        //System.out.println("Transitive package import found at line" +symbol.getAstNode().get_SourcePositionStart()
        // .getLine() +
        //    " in "
        //  + symbol.getAstNode().get_SourcePositionStart().getFileName());
        List<ASTPackage> alreadyVisitedPackages = new ArrayList<>();
        resultingResolvedTypesWithPackages.addAll(getSymbolsFromRexportedImports((ASTPackage) symbol.getAstNode(),
            alreadyVisitedPackages));

      }
    }
    List<SysMLTypeSymbol> transitiveImports = new ArrayList<>();

    //Adding the transitive symbols. If something is a package (from a star import), we resolved this and now we add
    // the corresponding subsymbols.
    for (SysMLTypeSymbol symbol : resultingResolvedTypesWithPackages) {
      if (symbol.getAstNode() instanceof ASTPackage) {
        //System.out.println("--Adding Symbols from package "+ symbol.getName());
        transitiveImports.addAll(addSymbolsOfPackageByStarImport((ASTPackage) symbol.getAstNode()));
      }
      else {
        //System.out.println("--Adding Symbols from "+ symbol.getName());
        transitiveImports.add(symbol);
      }
    }

    return transitiveImports;
  }

  //This function assumes that all qualified names are already resolved from Phase1
  //alreadyVisitedPackages is checked, so that we do not run in an infinty loop
  // returns a list of Symbols. If this is a package and a star import, we have to extract the symbols of the star
  // import in a later step.
  private Set<SysMLTypeSymbol> getSymbolsFromRexportedImports(ASTPackage checkThisPackageForRexports,
      List<ASTPackage> alreadyVisitedPackages) {
    Set<SysMLTypeSymbol> resolvedTypes = new HashSet<>();
    for (ASTPackageMember member : checkThisPackageForRexports.getPackageBody().getPackageMemberList()) {
      boolean isPrivate = false;
      if (member.getPackageMemberPrefix().isPresentVisibility()) {
        isPrivate = isPrivate(member.getPackageMemberPrefix().getVisibility());
      }

      //Now if we have a public import add all the symbols also to the AST.
      if (!isPrivate && member.isPresentPackagedDefinitionMember()) {
        if (member.getPackagedDefinitionMember() instanceof ASTAliasPackagedDefinitionMember) {
          ASTAliasPackagedDefinitionMember aliasOrImport =
              (ASTAliasPackagedDefinitionMember) member.getPackagedDefinitionMember();

          //We have a public import.
          resolvedTypes.addAll(aliasOrImport.getResolvedTypes());
          for (SysMLTypeSymbol type : aliasOrImport.getResolvedTypes()) {
            if (type.getAstNode() instanceof ASTPackage) {
              //Call recursively.
              ASTPackage astPackage = (ASTPackage) type.getAstNode();

              if (!alreadyVisitedPackages.contains(astPackage)) {//If we did not visit it yet.
                alreadyVisitedPackages.add(astPackage);
                resolvedTypes.addAll(getSymbolsFromRexportedImports(astPackage, alreadyVisitedPackages));
              }
            }
          }
        }
      }
    }

    for (ASTImportUnit importUnit : checkThisPackageForRexports.getPackageBody().getImportUnitList()) {
      if (importUnit instanceof ASTImportUnitStd) {
        ASTImportUnitStd importUnitStd = (ASTImportUnitStd) importUnit;
        boolean isPrivate = false;
        if (importUnitStd.isPresentVisibility()) {
          isPrivate = isPrivate(importUnitStd.getVisibility());
        }

        //We have a public import.
        resolvedTypes.addAll(importUnitStd.getResolvedTypes());
        for (SysMLTypeSymbol type : importUnitStd.getResolvedTypes()) {
          if (type.getAstNode() instanceof ASTPackage) {
            //Call recursively.
            ASTPackage astPackage = (ASTPackage) type.getAstNode();

            if (!alreadyVisitedPackages.contains(astPackage)) {//If we did not visit it yet.
              alreadyVisitedPackages.add(astPackage);
              Set<SysMLTypeSymbol> newSymbols = getSymbolsFromRexportedImports(astPackage, alreadyVisitedPackages);

              //if it is a star import add all symbols of this package. => CoCos are checked elsewhere so we do not
              // have to add further checking here.
              for (SysMLTypeSymbol possiblePackage : newSymbols) {
                if (importUnitStd.isStar() && possiblePackage.getAstNode() instanceof ASTPackage) {
                  //System.out.println("--- 1 ---  Found a reexported package resolved Types before " + resolvedTypes
                  // .size());
                  resolvedTypes.addAll(addSymbolsOfPackageByStarImport((ASTPackage) possiblePackage.getAstNode()));
                  //System.out.println("--- 2 ---- Found a reexported package resolved Types after " + resolvedTypes
                  // .size());
                }
                else { // Not a star import, just add the symbol
                  resolvedTypes.add(possiblePackage);
                }
              }
            }
          }
        }
      }
    }
    return resolvedTypes;
  }

  private List<SysMLTypeSymbol> addSymbolsOfPackageByStarImport(ASTPackage astPackage) {
    List<SysMLTypeSymbol> symbolsInPackage = new ArrayList<>();
    LinkedListMultimap<String, SysMLTypeSymbol> imports =
        astPackage.getPackageBody().getSpannedScope().getSysMLTypeSymbols();
    for (SysMLTypeSymbol importSymbol : imports.values()) {
      if ((importSymbol.getAccessModifier() instanceof SysMLAccessModifierPrivate)) { // Do not import private symbols
        //System.out.println("Did not import symbol " + importSymbol.getName() + ", because it is private." );
      }
      else {
        //System.out.println("Adding symbol " + importSymbol.getName() + " to transitive imports");
        symbolsInPackage.add(importSymbol);
      }
    }
    return symbolsInPackage;
  }
}
