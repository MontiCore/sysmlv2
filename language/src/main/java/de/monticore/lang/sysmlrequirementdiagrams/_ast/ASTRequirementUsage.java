package de.monticore.lang.sysmlrequirementdiagrams._ast;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementSubjectSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementUsageSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ASTRequirementUsage extends ASTRequirementUsageTOP {

  /**
   * Validate requirement parameters for a requirement usage using the following rules:
   * 1. If there are no generalizations, then do nothing.
   * <p>
   * 2. If there is a single generalization, then it is not necessary that all parameters
   * of the generalization are redefined. If not all parameters are redefined, then the rest
   * of the parameters are inherited. (NOTE: redefined parameters are not inherited!)
   * If requirement wants to declare new parameters, it must first redefine all parameters
   * of the generalization (including the inherited parameters), in order (inherited parameters
   * will come last in the order).
   * <p>
   * 3. If there are multiple generalizations, then it is necessary that all the parameters
   * of all the generalizations are redefined (including the inherited parameters), in order (inherited parameters
   * will come last in the order).
   * Then, the requirement can optionally declare new parameters.
   * <p>
   * 4. Whenever redefinitons are done, type compatibility must hold.
   */
  public void validateRequirementParameters() {
    // Validate that redefining parameters are compatible with the redefined parameters.
    if(this.isPresentMCType()) {
      RequirementDefSymbol reqDefSym = this.getEnclosingScope().
          resolveRequirementDef(((ASTMCQualifiedType) this.getMCType()).getMCQualifiedName().getQName()).get();
      this.validateParameterTypes(reqDefSym.getAstNode());
    }

    if(this.isPresentSysMLSubsetting()) {
      ASTSysMLSubsetting subsetting = this.getSysMLSubsetting();
      for (ASTMCQualifiedName subsetted : subsetting.getFieldsList()) {
        RequirementUsageSymbol reqUsageSym = this.getEnclosingScope().
            resolveRequirementUsage(subsetted.getQName()).get();
        this.validateParameterTypes(reqUsageSym.getAstNode());
      }
    }
  }

  /**
   * Inherit those parameters from the generalized requirements that weren't redefined
   * by the current requirement usage.
   */
  public void inheritNonRedefinedRequirementUsageParameters() {
    /*
    If the requirement usage only has a single type binding or subsets a single requirement usage,
    then it is allowed that less than total defined parameters of the generalization are redefined
    here.
    The parameters that aren't redefined will be inherited instead.
     */
    if(this.isPresentMCType() && !this.isPresentSysMLSubsetting()) {
      RequirementDefSymbol sym = this.getEnclosingScope().
          resolveRequirementDef(((ASTMCQualifiedType) this.getMCType()).getMCQualifiedName().getQName()).get();
      this.inheritNonRedefinedParameters(sym.getAstNode());
    }
    else if(!this.isPresentMCType() && this.isPresentSysMLSubsetting() && this.getSysMLSubsetting().sizeFields() == 1) {
      RequirementUsageSymbol sym = this.getEnclosingScope().
          resolveRequirementUsage(this.getSysMLSubsetting().getFields(0).getQName()).get();
      this.inheritNonRedefinedParameters(sym.getAstNode());
    }
    else {
      this.inheritedParameters = new ArrayList<>();
    }
  }

  /**
   * Return inherited parameters.
   * This method recursively adds inherited parameters in the current node,
   * as well as any owned generalizations, if its inheritedParameters field is uninitialized.
   *
   * @return ArrayList<ASTSysMLParameter>
   */
  @Override
  public ArrayList<ASTSysMLParameter> getInheritedParameters() {
    if(this.inheritedParameters == null) {
      inheritNonRedefinedRequirementUsageParameters();
    }
    return this.inheritedParameters;
  }

  /**
   * Extracts and returns the requirement subject of a requirement usage defined by its enclosing
   * requirement definition/usage.
   *
   * @return Optional<RequirementSubjectSymbol>
   */
  protected Optional<RequirementSubjectSymbol> getEnclosingRequirementSubject() {
    Optional<RequirementSubjectSymbol> subject = Optional.empty();
    if(this.getEnclosingScope().getAstNode() instanceof ASTRequirementDef) {
      ASTRequirementDef reqDef = ((ASTRequirementDef) this.getEnclosingScope().getAstNode());
      subject = reqDef.getSubject();
    }
    else if(this.getEnclosingScope().getAstNode() instanceof ASTRequirementUsage) {
      ASTRequirementUsage reqUsage = ((ASTRequirementUsage) this.getEnclosingScope().getAstNode());
      subject = reqUsage.getSubject();
    }
    return subject;
  }

  /**
   * Checks whether the given requirement usage is nested in another requirement definition or usage.
   *
   * @return Boolean
   */
  protected boolean isNestedRequirement() {
    return this.getEnclosingScope().getAstNode() instanceof ASTRequirementDef
        || this.getEnclosingScope().getAstNode() instanceof ASTRequirementUsage;
  }

  /**
   * Extracts and returns the requirement subject of a requirement usage defined by a requirement definition.
   *
   * @return Optional<RequirementSubjectSymbol>
   */
  protected Optional<RequirementSubjectSymbol> getRequirementDefinitionSubject() {
    Optional<RequirementSubjectSymbol> subject = Optional.empty();
    if(this.isPresentMCType()) {
      ASTMCQualifiedType usageType = (ASTMCQualifiedType) this.getMCType();
      Optional<RequirementDefSymbol> requirementDefSymbol = this.getEnclosingScope()
          .resolveRequirementDef(usageType.getMCQualifiedName().getQName());
      // Get subject from requirement definition,
      if(requirementDefSymbol.isPresent()) {
        List<RequirementSubjectSymbol> subjectSymbols = requirementDefSymbol.get().getAstNode().getSpannedScope()
            .getRequirementSubjectSymbols().values();
        if(subjectSymbols.size() > 0) {
          subject = Optional.ofNullable(subjectSymbols.get(0));
        }
      }
      // or raise an error, if requirement definition symbol is not found.
      else {
        Log.error("RequirementUsage '" + this.getName() + "' is typed by RequirementDefinition '"
            + usageType.getMCQualifiedName().getQName()
            + "', but the latter could not be resolved.");
      }
    }
    return subject;
  }

  /**
   * Extracts all subjects from a given requirement usage's feature typings.
   *
   * @return List<RequirementSubjectSymbol>
   */
  protected List<RequirementSubjectSymbol> getSubjectsFromTypings() {
    List<RequirementSubjectSymbol> subjects = new ArrayList<>();
    if(this.isPresentMCType()) {
      ASTMCQualifiedType usageType = (ASTMCQualifiedType) this.getMCType();
      Optional<RequirementDefSymbol> symbol = this.getEnclosingScope()
          .resolveRequirementDef(usageType.getMCQualifiedName().toString());

      /*
       * Since feature typing does exist, it's symbol must be resolvable. No need to check here.
       * This is verified by CoCo 'FeatureTypingsInRequirementsMustExist'.
       */

      if(symbol.isPresent()) {
        Optional<RequirementSubjectSymbol> subjectFromTypings = symbol.get().getAstNode().getSubject();
        subjectFromTypings.ifPresent(subjects::add);
      }
    }
    return subjects;
  }

  /**
   * Extracts all subjects from a given requirement usage's subsettings.
   *
   * @return List<RequirementSubjectSymbol>
   */
  protected List<RequirementSubjectSymbol> getSubjectsFromSubsettings() {
    List<RequirementSubjectSymbol> subjects = new ArrayList<>();
    if(this.isPresentSysMLSubsetting()) {
      // Extract subject from each subsetting
      for (ASTMCQualifiedName field : this.getSysMLSubsetting().getFieldsList()) {
        Optional<RequirementUsageSymbol> subsettingUsage = this.getEnclosingScope().resolveRequirementUsage(
            field.getQName());
        /*
         * If subsetting usage exists, it's symbol must be resolvable. No need to check here.
         * This is verified by CoCo 'SubsettedRequirementsMustExist'.
         */
        if(subsettingUsage.isPresent()) {
          Optional<RequirementSubjectSymbol> subjectFromSubsettings = subsettingUsage.get().getAstNode().getSubject();
          subjectFromSubsettings.ifPresent(subjects::add);
        }
      }
    }
    return subjects;
  }

  /**
   * Validates compatibility of current subject with the enclosing requirement subject.
   * If current subject does not exist, then sets the enclosing requirement subject as its subject.
   *
   * @param currentSubject RequirementSubjectSymbol
   * @return RequirementSubjectSymbol
   */
  protected Optional<RequirementSubjectSymbol> validateSubjectWithEnclosingRequirementSubject(
      RequirementSubjectSymbol currentSubject) {
    Optional<RequirementSubjectSymbol> enclosingReqSubject = this.getEnclosingRequirementSubject();
    if(enclosingReqSubject.isPresent()) {
      // If current subject exists, it should be compatible with enclosing req. subject.
      // Check for subject compatibility only if the current subject's value is not derived from an expression.
      if(currentSubject != null && !currentSubject.getAstNode().isPresentBinding()) {
        if(!enclosingReqSubject.get().getSubjectType().deepEquals(currentSubject.getSubjectType())) {
          Log.error("Subject of requirement usage is not compatible with the subject of the " +
              "corresponding enclosing requirement!");
        }
      }
      // If no subject is yet set, then set enclosing req. subject as current req. subject.
      else if(currentSubject == null) {
        currentSubject = enclosingReqSubject.get();
      }
    }
    // Otherwise, if the enclosing requirement does not define a subject, but the current one does,
    // log compatibility error.
    else if(currentSubject != null && this.isNestedRequirement()) {
      Log.error("Subject of nested requirement '" + currentSubject.getSubjectTypeName()
          + "' is not compatible with the subject of the " +
          "corresponding enclosing requirement! Enclosing requirement doesn't define a subject.");
    }
    return Optional.ofNullable(currentSubject);
  }

  /**
   * 1. Finds a compatible inherited requirement subject by processing all requirement subjects inherited
   * from feature typings and subsettings of a requirement usage.
   * 2. Validates the compatibility the 'currentSubject' subject, if it exists.
   * 3. Sets the extracted compatible subject as the 'currentSubject' subject, if it does not exist.
   *
   * @param currentSubject RequirementSubjectSymbol
   * @return RequirementSubjectSymbol
   */
  protected Optional<RequirementSubjectSymbol> validateSubjectWithInheritedSubjects(
      RequirementSubjectSymbol currentSubject) {
    boolean subjectDefinedInUsage = this.getSpannedScope().getRequirementSubjectSymbols().size() > 0;
    // 1. Get subject from feature typings.
    Optional<RequirementSubjectSymbol> subjectFromTypes = getCompatibleSubject(this.getSubjectsFromTypings());
    // 2. Get subject from subsettings.
    Optional<RequirementSubjectSymbol> subjectFromSubsettings = getCompatibleSubject(this.getSubjectsFromSubsettings());
    // 3. Get more specialized subject from the above two.
    Optional<RequirementSubjectSymbol> inheritedSubject = getInheritedSubject(subjectFromTypes, subjectFromSubsettings);
    if(inheritedSubject.isPresent()) {
      // If a compatible inherited subject was found, then check compatibility with subject in current usage.
      if(subjectDefinedInUsage && !currentSubject.isCompatible(inheritedSubject.get())) {
        Log.error("Subject type of of requirement usage '" + currentSubject.getSubjectTypeName()
            + "' is not compatible with the inherited subject type '" + inheritedSubject.get().getSubjectTypeName()
            + "' of typed/subsetted requirements!");
      }
      // If req. usage subject didn't already exist, then assign inherited subject to it.
      else if(!subjectDefinedInUsage) {
        return inheritedSubject;
      }
    }
    return Optional.ofNullable(currentSubject);
  }

  /**
   * Validates compatibility between inherited subjects and returns the more specialized subject
   * as the final 'inherited' subject.
   *
   * @param subjectFromTypes       RequirementSubjectSymbol
   * @param subjectFromSubsettings RequirementSubjectSymbol
   * @return RequirementSubjectSymbol
   */
  protected Optional<RequirementSubjectSymbol> getInheritedSubject(
      Optional<RequirementSubjectSymbol> subjectFromTypes, Optional<RequirementSubjectSymbol> subjectFromSubsettings) {
    Optional<RequirementSubjectSymbol> inheritedSubject = Optional.empty();

    // If subject was found from both feature typings and subsettings, then find the more specialized one, if any.
    if(subjectFromTypes.isPresent() && subjectFromSubsettings.isPresent()) {
      if(subjectFromSubsettings.get().isCompatible(subjectFromTypes.get())) {
        inheritedSubject = subjectFromSubsettings;
      }
      else if(subjectFromTypes.get().isCompatible(subjectFromSubsettings.get())) {
        inheritedSubject = subjectFromTypes;
      }
      else {
        Log.error("Typed/subsetted requirements do not have compatible subject type!");
      }
    }
    // Otherwise, if subject from not both but either source was found, then that is the final inherited subject.
    else if(subjectFromTypes.isPresent()) {
      inheritedSubject = subjectFromTypes;
    }
    else if(subjectFromSubsettings.isPresent()) {
      inheritedSubject = subjectFromSubsettings;
    }
    return inheritedSubject;
  }

  /**
   * Extracts inherited as well as enclosing requirement subjects, then:
   * 1. If req. usage has a type, then its subject must be compatible with defined subject of req. usage.
   * 2. If req. usage is nested, then its subject must be compatible with enclosing req. subject.
   * 2.1. If req. usage defines a subject with an expression, then compatibility with enclosing req.
   * subject is void. In this case, we only check that the newly defined subject is resolvable
   * in the current context.
   * 2.2. TODO Needs to be same in nested mode. If req. usage redefined subject type, then we allow the constraint on redefined subject type to
   * be loosened, but further restriction on subject type is not allowed.
   * e.g. Enclosing req. has subject Car and nested req. has subject Vehicle (where Car is subtype of Vehicle),
   * then this is allowed. Vice verse is not.
   */
  @Override
  public void setSubject() {
    if(!subjectFetched) {
      subjectFetched = true;

      boolean subjectDefinedInUsage = this.getSpannedScope().getRequirementSubjectSymbols().size() > 0;
      RequirementSubjectSymbol reqUsageSubject = null;

      // If there exists a subject in current usage, then extract that.
      if(subjectDefinedInUsage) {
        reqUsageSubject = this.getSpannedScope().getRequirementSubjectSymbols().values().get(0);
      }

      // *** 2. Check compatibility with typed and subsetted requirements ***
      Optional<RequirementSubjectSymbol> subject = this.validateSubjectWithInheritedSubjects(reqUsageSubject);
      if(subject.isPresent())
        reqUsageSubject = subject.get();

      // *** 3. Check compatibility with enclosing requirement ***
      subject = this.validateSubjectWithEnclosingRequirementSubject(reqUsageSubject);
      if(subject.isPresent())
        reqUsageSubject = subject.get();

      // If req. usage subject was not already present, then add it in the spanned scope.
      if(!subjectDefinedInUsage && reqUsageSubject != null) {
        this.getSpannedScope().add(reqUsageSubject);
      }
    }
  }

}
