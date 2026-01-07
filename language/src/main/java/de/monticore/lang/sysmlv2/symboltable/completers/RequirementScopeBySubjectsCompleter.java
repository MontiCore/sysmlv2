package de.monticore.lang.sysmlv2.symboltable.completers;

import de.monticore.lang.sysmlconstraints._ast.ASTRequirementUsage;
import de.monticore.lang.sysmlconstraints._visitor.SysMLConstraintsVisitor2;
import de.monticore.lang.sysmlparts._symboltable.PartDefSymbol;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._symboltable.ISysMLv2Scope;
import de.monticore.lang.sysmlv2._symboltable.SysMLv2Scope;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class RequirementScopeBySubjectsCompleter implements SysMLConstraintsVisitor2 {

  @Override
  public void endVisit(ASTRequirementUsage node) {
    if(!node.isPresentRequirementSubject()) {
      return;
    }

    // retrieve requirement subjects data
    var reqSubject = node.getRequirementSubject();
    var reqSubjectName = reqSubject.getName();

    // retrieve the subject within the enclosing scope of our RequirementUsage
    ISysMLv2Scope usageEnclosingScope = ((ISysMLv2Scope) node.getEnclosingScope());
    Optional<PartDefSymbol> subjectSym = usageEnclosingScope.resolvePartDef(reqSubjectName);

    if (!subjectSym.isPresent()) {
      // that should better be called in explicit CoCo later, and we just return
      // here
      Log.error("0x10113 Subject not found.", node.get_SourcePositionStart());
    }

    var subjectScope = ((SysMLv2Scope) subjectSym.get().getSpannedScope());
    var reqScope = ((SysMLv2Scope) node.getSymbol().getSpannedScope());

    // we copy the ports from the subject to the requirement
    subjectScope.getLocalPortUsageSymbols().forEach(portUsage -> {
      var portUsageBuilder = SysMLv2Mill.portUsageSymbolBuilder()
          .setTypesList(portUsage.getTypesList())
          .setConjugatedTypesList(portUsage.getConjugatedTypesList())
          .setStrong(portUsage.isStrong())
          .setName(portUsage.getName())
          .setFullName(portUsage.getFullName())
          .setPackageName(portUsage.getPackageName())
          .setAccessModifier(portUsage.getAccessModifier())
          .setStereoinfo(portUsage.getStereoinfo())
          .setEnclosingScope(reqScope)
          .setSpannedScope(SysMLv2Mill.scope());
      reqScope.add(portUsageBuilder.build());
    });
  }

}
