package de.monticore.lang.sysmlrequirementdiagrams._ast;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLParameter;
import de.monticore.lang.sysmlcommons._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementSubjectSymbol;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ASTRequirementDef extends ASTRequirementDefTOP {

  /**
   * Validate requirement parameters for a requirement definition using the following rules:
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
   * will come last in the order). Then, the requirement can optionally declare new parameters.
   * <p>
   * 4. Whenever redefinitons are done, type compatibility must hold.
   */
  public void validateRequirementParameters() {
    if(this.isPresentSysMLSpecialization()) {
      // Validate that redefining parameters are compatible with the redefined parameters.
      for (ASTMCObjectType superDef : this.getSysMLSpecialization().getSuperDefList()) {
        RequirementDefSymbol reqDefSym = this.getEnclosingScope().
            resolveRequirementDef(((ASTMCQualifiedType) superDef).getMCQualifiedName().getQName()).get();
        if(this.isPresentParameterList()) {
          this.validateParameterTypes(reqDefSym.getAstNode());
        }
      }
    }
  }

  /**
   * Inherit those parameters from the generalized requirements that weren't redefined
   * by the current requirement definition.
   */
  public void inheritNonRedefinedRequirementDefinitionParameters() {
    if(this.isPresentSysMLSpecialization() && this.getSysMLSpecialization().sizeSuperDef() == 1) {
    /*
    Since the current requirement def. only generalizes single requirement def.,
    it is allowed that less than the total number of super requirement parameters
    are redefined here.
    The parameters that aren't redefined will be inherited instead.
     */
      ASTSysMLSpecialization specialization = this.getSysMLSpecialization();
      RequirementDefSymbol sym = this.getEnclosingScope().
          resolveRequirementDef(
              ((ASTMCQualifiedType) specialization.getSuperDef(0)).getMCQualifiedName().getQName()).get();

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
      inheritNonRedefinedRequirementDefinitionParameters();
    }
    return this.inheritedParameters;
  }

  /**
   * Extract all inherited subjects from the super types.
   *
   * @return ArrayList<RequirementSubjectSymbol>
   */
  protected ArrayList<RequirementSubjectSymbol> getInheritedSubjects() {
    ArrayList<RequirementSubjectSymbol> subjects = new ArrayList<>();
    if(this.isPresentSysMLSpecialization()) {
      ASTSysMLSpecialization specialization = this.getSysMLSpecialization();
      for (ASTMCObjectType superDef : specialization.getSuperDefList()) {
        Optional<RequirementDefSymbol> superDefSym = ((SysMLv2Scope) specialization.getEnclosingScope())
            .resolveRequirementDef(((ASTMCQualifiedType) superDef).getMCQualifiedName().getQName());
        if(!superDefSym.isPresent()) {
          Log.error("Super RequirementDefinition '" + ((ASTMCQualifiedType) superDef).getMCQualifiedName().getQName()
              + "' could not be resolved.");
        }
        else {
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
    }
    return subjects;
  }

  /**
   * Find the most constrained (in terms of type) inherited subject and return it.
   *
   * @return Optional<RequirementSubjectSymbol>
   */
  public Optional<RequirementSubjectSymbol> getInheritedSubject() {
    Optional<RequirementSubjectSymbol> inheritedSubject = Optional.empty();
    ArrayList<RequirementSubjectSymbol> inheritedSubjects = getInheritedSubjects();
    if(inheritedSubjects.size() > 0) {
      inheritedSubject = getCompatibleSubject(inheritedSubjects);
    }
    return inheritedSubject;
  }

  /**
   * Extract inherited requirement subjects, then either:
   * 1. If current requirement def. defines a subject, then it is compatible to the inherited subject types.
   * 2. If it does not, then set the inherited subject as its subject.
   * If inherited subjects are not of the same type, an error is raised.
   */
  @Override
  public void setSubject() {
    if(!subjectFetched) {
      subjectFetched = true;
      if(this.isPresentSysMLSpecialization()) {
        ASTSysMLSpecialization specialization = this.getSysMLSpecialization();
        ArrayList<RequirementSubjectSymbol> subjects = new ArrayList<>();
        for (ASTMCObjectType superDef : specialization.getSuperDefList()) {
          // Super requirement must be present. Checked by CoCo before this.
          RequirementDefSymbol sym = ((SysMLv2Scope) specialization.getEnclosingScope())
              .resolveRequirementDef(((ASTMCQualifiedType) superDef).getMCQualifiedName().getQName()).get();
          Optional<RequirementSubjectSymbol> inheritedSubject = sym.getAstNode().getSubject();
          inheritedSubject.ifPresent(subjects::add);
        }
        if(subjects.size() > 0) {
          Optional<RequirementSubjectSymbol> compatibleSubjectOpt = getCompatibleSubject(subjects);
          if(compatibleSubjectOpt.isPresent()) {
            RequirementSubjectSymbol compatibleSubject = compatibleSubjectOpt.get();
            int subjectsSize = this.getSpannedScope().getRequirementSubjectSymbols().size();
            switch (subjectsSize) {
              case 0:
            /*
             If subject is not in current req. definition, then add the subject to the spanned scope.
             The enclosing scope of this subject symbol is not changed and backward traversal from here
             will bring us to its actual enclosing scope.
             */
                this.getSpannedScope().add(compatibleSubject);
                break;
              case 1:
                // Check if the current requirement subject is compatible with the inherited subjects.
                RequirementSubjectSymbol currentSubject = this.getSpannedScope().getRequirementSubjectSymbols().values().get(
                    0);
                if(!currentSubject.isCompatible(
                    compatibleSubject)) {
                  Log.error("Specialized requirement definition '" + this.getName() + "' with subject type '"
                      + currentSubject.getSubjectType().getTypeInfo().getName() + "' is not compatible with "
                      + "inherited subject type '" + compatibleSubject.getSubjectType().getTypeInfo().getName() + "'!");
                }
                break;
            }
          }
        }
      }
    }
  }

  public Optional<RequirementSubjectSymbol> getSubject() {
    Optional<RequirementSubjectSymbol> subject = Optional.empty();
    // If the subject was not already fetched, then try to find the correct subject.
    if(!subjectFetched) {
      setSubject();
    }
    if(this.getSpannedScope().getRequirementSubjectSymbols().size() > 0)
      subject = Optional.ofNullable(this.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
    return subject;
  }

}
