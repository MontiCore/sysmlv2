package de.monticore.lang.sysmlrequirementdiagrams._visitor;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLSubsetting;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementSubjectSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementUsageSymbol;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RequirementsPostProcessor finds and sets:
 * 1. the correct subject in the requirements,
 * 2. the correct requirement parameters in the requirements.
 */
public class RequirementsPostProcessor implements SysMLRequirementDiagramsVisitor2 {

  /**
   * Extracts all inherited subjects from the super types.
   *
   * @param node ASTRequirementDef
   * @return ArrayList<RequirementSubjectSymbol>
   */
  protected ArrayList<RequirementSubjectSymbol> getInheritedSubjects(ASTRequirementDef node) {
    ArrayList<RequirementSubjectSymbol> subjects = new ArrayList<>();
    if(node.isPresentSysMLSpecialization()) {
      getInheritedSubjects(node.getSysMLSpecialization(), subjects);
    }
    return subjects;
  }

  /**
   * Extracts all inherited subjects from the immediate super types.
   *
   * @param specialization ASTSysMLSpecialization
   * @param subjects       ArrayList<RequirementSubjectSymbol> subjects
   */
  protected void getInheritedSubjects(ASTSysMLSpecialization specialization,
                                      ArrayList<RequirementSubjectSymbol> subjects) {
    // We only look for subjects from the immediate super types, because the super types
    // themselves should already have their valid subjects set by this point.
    for (ASTMCObjectType superDef : specialization.getSuperDefList()) {
      Optional<RequirementDefSymbol> superDefSym = ((SysMLv2Scope) specialization.getEnclosingScope())
          .resolveRequirementDef(((ASTMCQualifiedType) superDef).getMCQualifiedName().toString());

      List<RequirementSubjectSymbol> requirementSubjects = superDefSym.get()
          .getAstNode()
          .getSpannedScope()
          .getRequirementSubjectSymbols()
          .values();
      /*
      If exactly one subject is present, then add it in the list.
      If there are no subjects, then we don't care.
      There can't be more than one subject.
       */
      if(requirementSubjects.size() == 1) {
        subjects.add(requirementSubjects.get(0));
      }
    }
  }

  /**
   * Extracts and returns the requirement subject of a requirement usage defined by its enclosing
   * requirement definition/usage.
   *
   * @param node ASTRequirementUsage
   * @return Optional<RequirementSubjectSymbol>
   */
  protected Optional<RequirementSubjectSymbol> getEnclosingRequirementSubject(ASTRequirementUsage node) {
    Optional<RequirementSubjectSymbol> subject = Optional.empty();
    if(node.getEnclosingScope().getAstNode() instanceof ASTRequirementDef) {
      ASTRequirementDef reqDef = ((ASTRequirementDef) node.getEnclosingScope().getAstNode());
      if(reqDef.getSpannedScope().getRequirementSubjectSymbols().values().size() > 0) {
        subject = Optional.ofNullable(reqDef.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
      }
    }
    else if(node.getEnclosingScope().getAstNode() instanceof ASTRequirementUsage) {
      ASTRequirementUsage reqUsage = ((ASTRequirementUsage) node.getEnclosingScope().getAstNode());
      if(reqUsage.getSpannedScope().getRequirementSubjectSymbols().values().size() > 0) {
        subject = Optional.ofNullable(reqUsage.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
      }
    }
    return subject;
  }

  /**
   * Checks whether the given requirement usage is nested in another requirement definition or usage.
   *
   * @param node ASTRequirementUsage
   * @return Boolean
   */
  protected boolean isNestedRequirement(ASTRequirementUsage node) {
    return node.getEnclosingScope().getAstNode() instanceof ASTRequirementDef
        || node.getEnclosingScope().getAstNode() instanceof ASTRequirementUsage;
  }

  /**
   * Extracts and returns the requirement subject of a requirement usage defined by a requirement definition.
   *
   * @param node ASTRequirementUsage
   * @return Optional<RequirementSubjectSymbol>
   */
  protected Optional<RequirementSubjectSymbol> getRequirementDefinitionSubject(ASTRequirementUsage node) {
    Optional<RequirementSubjectSymbol> subject = Optional.empty();
    if(node.isPresentMCType()) {
      ASTMCQualifiedType usageType = (ASTMCQualifiedType) node.getMCType();
      Optional<RequirementDefSymbol> requirementDefSymbol = node.getEnclosingScope()
          .resolveRequirementDef(usageType.getMCQualifiedName().toString());

      // If requirement definition symbol is not found, raise an error.
      if(!requirementDefSymbol.isPresent()) {
        Log.error("MCType " + usageType.getMCQualifiedName().toString() + " could not be resolved.");
      }
      // Otherwise, get subject from requirement definition.
      List<RequirementSubjectSymbol> subjectSymbols = requirementDefSymbol.get().getAstNode().getSpannedScope()
          .getRequirementSubjectSymbols().values();
      if(subjectSymbols.size() > 0) {
        subject = Optional.ofNullable(subjectSymbols.get(0));
      }
    }
    return subject;
  }

  /**
   * Extracts inherited requirement subjects, then either:
   * 1. If current requirement def. defines a subject, then it is compatible to the inherited subject types.
   * 2. If it does not, then set the inherited subject as its subject.
   * If inherited subjects are not of the same type, an error is raised.
   *
   * @param node ASTRequirementDef
   */
  protected void setSubjectInRequirementDefinition(ASTRequirementDef node) {
    // We only need to set and check subject type for compatibility if there are inherited subjects.
    ArrayList<RequirementSubjectSymbol> inheritedSubjects = getInheritedSubjects(node);
    if(inheritedSubjects.size() > 0) {
      // Validate all inherited subjects are compatible with each other.
      RequirementSubjectSymbol compatibleSubject = getCompatibleSubject(inheritedSubjects);
      int subjectsSize = node.getSpannedScope().getRequirementSubjectSymbols().size();
      if(compatibleSubject != null) {
        switch (subjectsSize) {
          case 0:
            /*
             If subject is not in current req. definition, then add the subject to the spanned scope.
             The enclosing scope of this subject symbol is not changed and backward traversal from here
             will bring us to its actual enclosing scope.
             */
            node.getSpannedScope().add(compatibleSubject);
            break;
          case 1:
            // Check if the current requirement subject is compatible with the inherited subjects.
            RequirementSubjectSymbol currentSubject = node.getSpannedScope().getRequirementSubjectSymbols().values().get(
                0);
            if(!currentSubject.isCompatible(
                compatibleSubject)) {
              Log.error("Specialized requirement definition '" + node.getName() + "' with subject type '"
                  + currentSubject.getSubjectType().getTypeInfo().getName() + "' is not compatible with "
                  + "inherited subject type '" + compatibleSubject.getSubjectType().getTypeInfo().getName() + "'!");
            }
            break;
        }
      }
    }
  }

  /**
   * Extracts all subjects from a given requirement usage's feature typings.
   *
   * @param node ASTRequirementUsage
   * @return List<RequirementSubjectSymbol>
   */
  protected List<RequirementSubjectSymbol> getSubjectsFromTypings(ASTRequirementUsage node) {
    List<RequirementSubjectSymbol> subjects = new ArrayList<>();
    if(node.isPresentMCType()) {
      ASTMCQualifiedType usageType = (ASTMCQualifiedType) node.getMCType();
      Optional<RequirementDefSymbol> symbol = node.getEnclosingScope()
          .resolveRequirementDef(usageType.getMCQualifiedName().toString());

      /*
       * Since feature typing does exist, it's symbol must be resolvable. No need to check here.
       * This is verified by CoCo 'FeatureTypingsInRequirementsMustExist'.
       */

      if(symbol.isPresent()) {
        List<RequirementSubjectSymbol> subjectSymbols = symbol.get().getAstNode().getSpannedScope()
            .getRequirementSubjectSymbols().values();
        if(subjectSymbols.size() > 0) {
          subjects.add(subjectSymbols.get(0));
        }
      }
    }
    return subjects;
  }

  /**
   * Extracts all subjects from a given requirement usage's subsettings.
   *
   * @param node ASTRequirementUsage
   * @return List<RequirementSubjectSymbol>
   */
  protected List<RequirementSubjectSymbol> getSubjectsFromSubsettings(ASTRequirementUsage node) {
    List<RequirementSubjectSymbol> subjects = new ArrayList<>();
    if(node.isPresentSysMLSubsetting()) {
      // Extract subject from each subsetting
      for (ASTMCQualifiedName field : node.getSysMLSubsetting().getFieldsList()) {
        Optional<RequirementUsageSymbol> subsettingUsage = node.getEnclosingScope().resolveRequirementUsage(
            field.getQName());

        /*
         * If subsetting usage exists, it's symbol must be resolvable. No need to check here.
         * This is verified by CoCo 'SubsettedRequirementsMustExist'.
         */

        if(subsettingUsage.isPresent()
            && subsettingUsage.get().getSpannedScope().getRequirementSubjectSymbols().size() > 0) {
          subjects.add(subsettingUsage.get().getSpannedScope().getRequirementSubjectSymbols().values().get(0));
        }
      }
    }
    return subjects;
  }

  protected boolean isSuperType(RequirementSubjectSymbol subject, RequirementSubjectSymbol other) {
    return other.isCompatible(subject);
  }

  protected boolean isSubType(RequirementSubjectSymbol subject, RequirementSubjectSymbol other) {
    return subject.isCompatible(other);
  }

  /**
   * Validates the the provided subjects are compatible as long as they fulfill any of the two criteria:
   * 1. They are all of same types
   * 2. All the subjects lie in the same inheritance chain, i.e. a subject is either a supertype
   * of another subject or its subtype.
   * Returns the most specialized subject as the compatible subject.
   *
   * @param subjects List<RequirementSubjectSymbol>
   * @return RequirementSubjectSymbol
   */
  private RequirementSubjectSymbol getCompatibleSubject(List<RequirementSubjectSymbol> subjects) {
    RequirementSubjectSymbol subject = null;
    if(subjects.size() > 1) {
      RequirementSubjectSymbol superType = subjects.get(0);
      RequirementSubjectSymbol subType = subjects.get(0);

      for (RequirementSubjectSymbol subj : subjects) {
        if(superType.getSubjectType().getTypeInfo().getName().equals(subType.getSubjectType().getTypeInfo().getName())
            &&
            superType.getSubjectType().getTypeInfo().getName().equals(subj.getSubjectType().getTypeInfo().getName())) {
          continue;
        }

        // First, we extract all relations of the current subject with the current super and sub types.
        boolean isSuperTypeOfSuperType = isSuperType(subj, superType);
        boolean isSubTypeOfSuperType = isSubType(subj, superType);
        boolean isSuperTypeOfSubType = isSuperType(subj, subType);
        boolean isSubTypeOfSubType = isSubType(subj, subType);

        // If current subject is supertype of the current supertype, we assign
        // current subject as the new supertype.
        if(isSuperTypeOfSuperType) {
          superType = subj;
        }
        // If current subject is subtype of the current subtype, we assign
        // current subject as the new subtype.
        else if(isSubTypeOfSubType) {
          subType = subj;
        }
        /*
        If current subject is the subtype of the current supertype, then it must
        also be the supertype of the current subtype.
        Conversely, if current subject is the supertype of the current subtype, then it must
        also be the subtype of the current supertype.
         */
        else if(!(isSuperTypeOfSubType && isSubTypeOfSuperType)) {
          Log.error("Subjects found to be incompatible with one another!");
        }
      }
    }
    else if(subjects.size() == 1) {
      subject = subjects.get(0);
    }
    return subject;
  }

  /**
   * Validates compatibility of current subject with the enclosing requirement subject.
   * If current subject does not exist, then sets the enclosing requirement subject as its subject.
   *
   * @param node           ASTRequirementUsage
   * @param currentSubject RequirementSubjectSymbol
   * @return RequirementSubjectSymbol
   */
  private RequirementSubjectSymbol validateSubjectWithEnclosingRequirementSubject(
      ASTRequirementUsage node,
      RequirementSubjectSymbol currentSubject) {
    Optional<RequirementSubjectSymbol> enclosingReqSubject = getEnclosingRequirementSubject(node);
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
    else if(currentSubject != null && isNestedRequirement(node)) {
      Log.error("Subject of nested requirement '" + currentSubject.getSubjectTypeName()
          + "' is not compatible with the subject of the " +
          "corresponding enclosing requirement! Enclosing requirement doesn't define a subject.");
    }
    return currentSubject;
  }

  /**
   * 1. Finds a compatible inherited requirement subject by processing all requirement subjects inherited
   * from feature typings and subsettings of a requirement usage.
   * 2. Validates the compatibility the 'currentSubject' subject, if it exists.
   * 3. Sets the extracted compatible subject as the 'currentSubject' subject, if it does not exist.
   *
   * @param node           ASTRequirementUsage
   * @param currentSubject RequirementSubjectSymbol
   * @return RequirementSubjectSymbol
   */
  private RequirementSubjectSymbol validateSubjectWithInheritedSubjects(
      ASTRequirementUsage node,
      RequirementSubjectSymbol currentSubject) {
    boolean subjectDefinedInUsage = node.getSpannedScope().getRequirementSubjectSymbols().size() > 0;
    // 1. Get subject from feature typings.
    RequirementSubjectSymbol subjectFromTypes = getCompatibleSubject(getSubjectsFromTypings(node));
    // 2. Get subject from subsettings.
    RequirementSubjectSymbol subjectFromSubsettings = getCompatibleSubject(getSubjectsFromSubsettings(node));
    // 3. Get more specialized subject from the above two.
    RequirementSubjectSymbol inheritedSubject = getInheritedSubject(subjectFromTypes, subjectFromSubsettings);
    if(inheritedSubject != null) {
      // If a compatible inherited subject was found, then check compatibility with subject in current usage.
      if(subjectDefinedInUsage && !currentSubject.isCompatible(inheritedSubject)) {
        Log.error("Subject type of of requirement usage '" + currentSubject.getSubjectTypeName()
            + "' is not compatible with the inherited subject type '" + inheritedSubject.getSubjectTypeName()
            + "' of typed/subsetted requirements!");
      }
      // If req. usage subject didn't already exist, then assign inherited subject to it.
      else if(!subjectDefinedInUsage) {
        currentSubject = inheritedSubject;
      }
    }
    return currentSubject;
  }

  /**
   * Validates compatibility between inherited subjects and returns the more specialized subject
   * as the final 'inherited' subject.
   *
   * @param subjectFromTypes       RequirementSubjectSymbol
   * @param subjectFromSubsettings RequirementSubjectSymbol
   * @return RequirementSubjectSymbol
   */
  private RequirementSubjectSymbol getInheritedSubject(
      RequirementSubjectSymbol subjectFromTypes, RequirementSubjectSymbol subjectFromSubsettings) {
    RequirementSubjectSymbol inheritedSubject = null;

    // If subject was found from both feature typings and subsettings, then find the more specialized one, if any.
    if(subjectFromTypes != null && subjectFromSubsettings != null) {
      if(subjectFromSubsettings.isCompatible(subjectFromTypes)) {
        inheritedSubject = subjectFromSubsettings;
      }
      else if(subjectFromTypes.isCompatible(subjectFromSubsettings)) {
        inheritedSubject = subjectFromTypes;
      }
      else {
        Log.error("Typed/subsetted requirements do not have compatible subject type!");
      }
    }
    // Otherwise, if subject from not both but either source was found, then that is the final inherited subject.
    else if(subjectFromTypes != null) {
      inheritedSubject = subjectFromTypes;
    }
    else if(subjectFromSubsettings != null) {
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
   *
   * @param node ASTRequirementUsage
   */
  protected void setSubjectInRequirementUsage(ASTRequirementUsage node) {
    boolean subjectDefinedInUsage = node.getSpannedScope().getRequirementSubjectSymbols().size() > 0;
    RequirementSubjectSymbol reqUsageSubject = null;

    // If there exists a subject in current usage, then extract that.
    if(subjectDefinedInUsage) {
      reqUsageSubject = node.getSpannedScope().getRequirementSubjectSymbols().values().get(0);
    }

    // *** 2. Check compatibility with typed and subsetted requirements ***
    reqUsageSubject = validateSubjectWithInheritedSubjects(node, reqUsageSubject);

    // *** 3. Check compatibility with enclosing requirement ***
    reqUsageSubject = validateSubjectWithEnclosingRequirementSubject(node, reqUsageSubject);

    // If req. usage subject was not already present, then add it in the spanned scope.
    if(!subjectDefinedInUsage && reqUsageSubject != null) {
      node.getSpannedScope().add(reqUsageSubject);
    }
  }

  /**
   * Set requirement parameters for a requirement definition.
   * All inherited requirements are also added and adapted accordingly, if redefined
   * in the current requirement.
   *
   * @param node ASTRequirementDef
   */
  protected void setRequirementParameters(ASTRequirementDef node) {
    // 1. Set inherited requirement parameters.
    if(node.isPresentSysMLSpecialization()) {
      ASTSysMLSpecialization specialization = node.getSysMLSpecialization();
      for (ASTMCObjectType superDef : specialization.getSuperDefList()) {
        /*
        For each general requirement, add its parameters in the specialized requirement.
        If there are duplicate parameters between inherited requirements, then they must have the same type.
         */
        RequirementDefSymbol reqDefSym = node.getEnclosingScope().
            resolveRequirementDef(((ASTMCQualifiedType) superDef).getMCQualifiedName().getQName()).get();
        node.addInheritedParameters(reqDefSym.getAstNode().getParameters());
      }
    }
    /*
    2. Set parameters of the current requirement. We set these params. after the inherited ones because
    we only check for duplicates in the current parameter list itself, not in the already added params.
    Reason being that the current requirement assigns values to the inherited params; hence, they will
    likely be present in the current parameter list itself.
     */
    node.transferParameters();
  }

  /**
   * Set requirement parameters for a requirement usage.
   * All inherited requirements are also added and adapted accordingly, if redefined
   * in the current requirement.
   *
   * @param node ASTRequirementUsage
   */
  protected void setRequirementParameters(ASTRequirementUsage node) {
    // 1. Set inherited parameters from type binding.
    if(node.isPresentMCType()) {
      RequirementDefSymbol sym = node.getEnclosingScope().
          resolveRequirementDef(((ASTMCQualifiedType) node.getMCType()).getMCQualifiedName().getQName()).get();
      node.addInheritedParameters(sym.getAstNode().getParameters());
    }

    /*
    2. If subsetting requirements are present, then add their params as well.
    If there are duplicate parameters between multiple subsetted requirements, then they must have the same type.
     */
    if(node.isPresentSysMLSubsetting()) {
      ASTSysMLSubsetting subsetting = node.getSysMLSubsetting();
      for (ASTMCQualifiedName subsettedUsage : subsetting.getFieldsList()) {
        RequirementUsageSymbol symbol = node.getEnclosingScope().
            resolveRequirementUsage(subsettedUsage.getQName()).get();
        node.addInheritedParameters(symbol.getAstNode().getParameters());
      }
    }
    /*
    3. Set parameters of the current requirement usage. We set these params after the parameters from
    type binding and subsettings are already set. This is done because we only check for duplicates
    in the current parameter list itself, not in the already added params.
    Reason being that the current requirement assigns values to the other params; hence, they will
    likely be present in the current parameter list itself.
     */
    node.transferParameters();
  }

  @Override
  public void visit(ASTRequirementDef node) {
    this.setSubjectInRequirementDefinition(node);
    this.setRequirementParameters(node);
  }

  @Override
  public void visit(ASTRequirementUsage node) {
    this.setSubjectInRequirementUsage(node);
    this.setRequirementParameters(node);
  }
}
