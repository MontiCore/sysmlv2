package schrott.types.check;

import de.monticore.lang.sysml.sysml4verification.SysML4VerificationMill;
import de.monticore.lang.sysml.sysml4verification._visitor.SysML4VerificationTraverser;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.check.SynthesizeSymTypeFromMCCollectionTypes;
import de.monticore.types.check.SynthesizeSymTypeFromMCSimpleGenericTypes;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;

import java.util.Optional;


/**
 * For a more detailed explanation on MontiCore TypeChecks see:
 * https://github.com/MontiCore/monticore/blob/6.7.0/monticore-grammar/src/main/java/de/monticore/types/check/TypeCheck.md
 *
 * This class is the TypesSynthesizer class for the SysML4Verification language.
 *
 * (Explanation and example might need improvment, when better understood.)
 * It synthesizes the Type of a TypeExpression e.g. 'List<List<Boolean>'
 *
 * It delegates to all already implemented TypesSynthesizers of the extended TYPE-grammars by creating a SysML4Vericiation
 * traverser and setting the respective TypesCalculator of the extended TYPE-grammar, which is a Visitor (and possibly
 * handler) for the extended grammar.
 *
 */

public class SynthesizeTypeSysML4VerificationDelegator implements ISynthesize {

  protected TypeCheckResult result;

  protected SysML4VerificationTraverser traverser;

  public SynthesizeTypeSysML4VerificationDelegator(){
    init();
  }

  @Override
  public Optional<SymTypeExpression> getResult() {
    if(result.isPresentCurrentResult()){
      return Optional.of(result.getCurrentResult());
    }else{
      return Optional.empty();
    }
  }

  @Override
  public void init() {
    this.traverser = SysML4VerificationMill.traverser();
    this.result = new TypeCheckResult();

    SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromMCBasicTypes.setTypeCheckResult(result);
    traverser.add4MCBasicTypes(symTypeFromMCBasicTypes);
    traverser.setMCBasicTypesHandler(symTypeFromMCBasicTypes);

    SynthesizeSymTypeFromMCCollectionTypes symTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    symTypeFromMCCollectionTypes.setTypeCheckResult(result);
    traverser.add4MCCollectionTypes(symTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(symTypeFromMCCollectionTypes);

    SynthesizeSymTypeFromMCSimpleGenericTypes symTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    symTypeFromMCSimpleGenericTypes.setTypeCheckResult(result);
    traverser.add4MCSimpleGenericTypes(symTypeFromMCSimpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(symTypeFromMCSimpleGenericTypes);
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

}
