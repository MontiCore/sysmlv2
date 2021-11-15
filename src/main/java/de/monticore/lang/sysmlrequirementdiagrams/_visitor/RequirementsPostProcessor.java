package de.monticore.lang.sysmlrequirementdiagrams._visitor;

import de.monticore.lang.sysmlcommons._ast.ASTSysMLSpecialization;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementDef;
import de.monticore.lang.sysmlrequirementdiagrams._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementDefSymbol;
import de.monticore.lang.sysmlrequirementdiagrams._symboltable.RequirementSubjectSymbol;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.lang.sysmlrequirementdiagrams.RequirementDiagramsHelper.getSubjectType;
import static de.monticore.lang.sysmlrequirementdiagrams.RequirementDiagramsHelper.isCompatible;

/**
 * RequirementsPostProcessor finds and sets the correct subject in the requirements.
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
    if (node.isPresentSysMLSpecialization()) {
      getInheritedSubjects(node.getSysMLSpecialization(), subjects);
    }
    return subjects;
  }

  protected void getInheritedSubjects(ASTSysMLSpecialization specialization, ArrayList<RequirementSubjectSymbol> subjects) {
    // We only look for subjects from the immediate super types, because the super types
    // themselves should already have their valid subjects set by this point.
    for (ASTMCObjectType superDef : specialization.getSuperDefList()) {
      Optional<RequirementDefSymbol> superDefSym = ((SysMLv2Scope) specialization.getEnclosingScope())
              .resolveRequirementDef(((ASTMCQualifiedType) superDef).getMCQualifiedName().toString());

      if (!superDefSym.isPresent()) {
        Log.error("Super requirement not found!");
      }

      List<RequirementSubjectSymbol> requirementSubjects = superDefSym.get()
              .getAstNode()
              .getSpannedScope()
              .getRequirementSubjectSymbols()
              .values();

      switch (requirementSubjects.size()) {
        case 0:
          // If no subject is present, then do nothing.
          break;
        case 1:
          // If exactly one subject is present, then add it in the list.
          subjects.add(requirementSubjects.get(0));
          break;
        default:
          // Otherwise, log an error that multiple subjects were found.
          Log.error("Multiple subjects found. A requirement can only have one subject.");
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
    if (node.getEnclosingScope().getAstNode() instanceof ASTRequirementDef) {
      ASTRequirementDef reqDef = ((ASTRequirementDef) node.getEnclosingScope().getAstNode());
      if (reqDef.getSpannedScope().getRequirementSubjectSymbols().values().size() > 0) {
        subject = Optional.ofNullable(reqDef.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
      }
    } else if (node.getEnclosingScope().getAstNode() instanceof ASTRequirementUsage) {
      ASTRequirementUsage reqUsage = ((ASTRequirementUsage) node.getEnclosingScope().getAstNode());
      if (reqUsage.getSpannedScope().getRequirementSubjectSymbols().values().size() > 0) {
        subject = Optional.ofNullable(reqUsage.getSpannedScope().getRequirementSubjectSymbols().values().get(0));
      }
    }
    return subject;
  }

  /**
   * Extracts and returns the requirement subject of a requirement usage defined by a requirement definition.
   *
   * @param node ASTRequirementUsage
   * @return Optional<RequirementSubjectSymbol>
   */
  protected Optional<RequirementSubjectSymbol> getRequirementDefinitionSubject(ASTRequirementUsage node) {
    Optional<RequirementSubjectSymbol> subject = Optional.empty();
    if (node.isPresentMCType()) {
      ASTMCQualifiedType usageType = (ASTMCQualifiedType) node.getMCType();
      Optional<RequirementDefSymbol> requirementDefSymbol = node.getEnclosingScope()
              .resolveRequirementDef(usageType.getMCQualifiedName().toString());

      // If requirement definition symbol is not found, raise an error.
      if (!requirementDefSymbol.isPresent()) {
        Log.error("MCType " + usageType.getMCQualifiedName().toString() + " could not be resolved.");
      }
      // Otherwise, get subject from requirement definition.
      List<RequirementSubjectSymbol> subjectSymbols = requirementDefSymbol.get().getAstNode().getSpannedScope()
              .getRequirementSubjectSymbols().values();
      if (subjectSymbols.size() > 0) {
        subject = Optional.ofNullable(subjectSymbols.get(0));
      }
    }
    return subject;
  }

  /**
   * Validates that types of the subjects are compatible with one another.
   * NOTE: By compatibility, only equality of type is implemented as of now!
   *
   * @param subjects ArrayList<RequirementSubjectSymbol>
   */
  protected void validateSubjectsTypeCompatibility(ArrayList<RequirementSubjectSymbol> subjects) {
    RequirementSubjectSymbol current = subjects.get(0);
    for (RequirementSubjectSymbol subject : subjects) {
      // What happens if an inherited subject has a bound value?
      if (!isCompatible(subject, current)) {
        Log.error("Inherited requirements do not have the same subject type!");
      }
    }
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
    if (node.getSpannedScope().getRequirementSubjectSymbols().size() > 1) {
      Log.error("Multiple subjects found. A requirement can only have one subject.");
    }

    // We only need to set and check subject type for compatibility if there are inherited subjects.
    ArrayList<RequirementSubjectSymbol> inheritedSubjects = getInheritedSubjects(node);
    if (inheritedSubjects.size() > 0) {
      validateSubjectsTypeCompatibility(inheritedSubjects);
      int subjectsSize = node.getSpannedScope().getRequirementSubjectSymbols().size();
      switch (subjectsSize) {
        case 0:
            /*
             If subject is not in current req. definition, then add the subject to the spanned scope.
             The enclosing scope of this subject symbol is not changed and backward traversal from here
             will bring us to its actual enclosing scope.
             */
          node.getSpannedScope().add(inheritedSubjects.get(0));
          break;
        case 1:
          // Check if the current requirement subject is compatible with the inherited subjects.
          if (!isCompatible(inheritedSubjects.get(0), node.getSpannedScope().getRequirementSubjectSymbols().values().get(0))) {
            Log.error("Specialized requirement does not have the same subject type as inherited ones!");
          }
          break;
      }
    }
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

    if (subjectDefinedInUsage) {
      reqUsageSubject = node.getSpannedScope().getRequirementSubjectSymbols().values().get(0);
      // If subject was redefined with an expression, resolve its type.
      if (reqUsageSubject.getAstNode().isPresentBinding()) {
        if (getSubjectType(reqUsageSubject) == null) {
          Log.error("Unable to resolve bound subject. Subject not found!");
        }
      }
    }

    // *** Check compatibility with requirement definition ***
    Optional<RequirementSubjectSymbol> reqDefSubject = getRequirementDefinitionSubject(node);
    // Check subject type compatibility only if both subjects are present.
    if (reqDefSubject.isPresent() && reqUsageSubject != null) {
      if (!isCompatible(reqDefSubject.get(), reqUsageSubject)) {
        Log.error("Subject of requirement usage is not compatible with the subject of the corresponding" +
                " requirement definition!");
      }
    }
    // If subject was inherited and not redefined, then set it as req. usage subject.
    else if (reqDefSubject.isPresent()) {
      reqUsageSubject = reqDefSubject.get();
    }

    // *** Check compatibility with enclosing requirement ***
    // If this is a nested requirement and enclosing req. def/usage defines a subject, then:
    //    1. if req. usage now has a subject, then it should be compatible with enclosing req. subject
    //       (only in case subject redefinition was not done via expression),
    //    2. if req. usage does not have a subject, assign enclosing req. subject as its subject.
    Optional<RequirementSubjectSymbol> enclosingReqSubject = getEnclosingRequirementSubject(node);
    if (enclosingReqSubject.isPresent()) {
      if (reqUsageSubject != null && !reqUsageSubject.getAstNode().isPresentBinding()) {
        if (!isCompatible(reqUsageSubject, enclosingReqSubject.get())) {
          Log.error("Subject of requirement usage is not compatible with the subject of the " +
                  "corresponding enclosing requirement!");
        }
      }
      // If no subject is yet set, then set enclosing req. subject as current req. subject.
      else if (reqUsageSubject == null) {
        reqUsageSubject = enclosingReqSubject.get();
      }
    }

    // If req. usage subject was found, then add it in the spanned scope.
    if (!subjectDefinedInUsage && reqUsageSubject != null) {
      node.getSpannedScope().add(reqUsageSubject);
    }
  }

  @Override
  public void visit(ASTRequirementDef node) {
    this.setSubjectInRequirementDefinition(node);
  }

  @Override
  public void visit(ASTRequirementUsage node) {
    this.setSubjectInRequirementUsage(node);
  }
}
