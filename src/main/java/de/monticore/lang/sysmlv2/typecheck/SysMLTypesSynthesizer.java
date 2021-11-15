package de.monticore.lang.sysmlv2.typecheck;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

import java.util.Optional;

public class SysMLTypesSynthesizer implements ISynthesize {
  protected TypeCheckResult result;

  protected SysMLv2Traverser traverser;

  @Override
  public Optional<SymTypeExpression> getResult() {
    if (result.isPresentCurrentResult()) {
      return Optional.of(result.getCurrentResult());
    } else {
      return Optional.empty();
    }
  }

  @Override
  public void init() {
    this.traverser = SysMLv2Mill.traverser();
    this.result = new TypeCheckResult();

    SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromMCBasicTypes.setTypeCheckResult(result);
    traverser.add4MCBasicTypes(symTypeFromMCBasicTypes);
    traverser.setMCBasicTypesHandler(symTypeFromMCBasicTypes);

    DeriveSymTypeofSysMLBasis symTypeFromSysMLBasis = new DeriveSymTypeofSysMLBasis();
    symTypeFromSysMLBasis.setTypeCheckResult(result);
    traverser.add4SysMLBasis(symTypeFromSysMLBasis);
    traverser.setSysMLBasisHandler(symTypeFromSysMLBasis);
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }
}
