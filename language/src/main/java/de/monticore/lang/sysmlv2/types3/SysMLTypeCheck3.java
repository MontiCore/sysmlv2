package de.monticore.lang.sysmlv2.types3;

import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.streamexpressions.types3.StreamExpressionsTypeVisitor;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2._visitor.SysMLv2Traverser;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.ocl.types3.OCLSymTypeRelations;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.streams.StreamSymTypeRelations;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

/**
 * TypeCheck3 implementation for the SysMLv2 language. After calling {@link #init()}, this
 * implementation will be available through the TypeCheck3 interface.
 */
public class SysMLTypeCheck3 extends MapBasedTypeCheck3 {

  public static void init() {
    initTC3Delegate();
    StreamSymTypeRelations.init();
    SysMLWithinTypeBasicSymbolResolver.init();
    SysMLWithinScopeBasicSymbolResolver.init();
    SysMLTypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    MCCollectionSymTypeRelations.init();
    SysMLSymTypeRelations.init();
  }

  public static void reset() {
    TypeCheck3.resetDelegate();
    StreamSymTypeRelations.reset();
    SysMLWithinTypeBasicSymbolResolver.reset();
    SysMLWithinScopeBasicSymbolResolver.reset();
    SysMLTypeVisitorOperatorCalculator.reset();
    CommonExpressionsLValueRelations.reset();
    MCCollectionSymTypeRelations.reset();
    OCLSymTypeRelations.reset();
  }

  protected static void initTC3Delegate() {
    Log.trace("init SysMLTypeCheck3", "TypeCheck setup");

    SysMLv2Traverser typeTraverser = SysMLv2Mill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();

    // Expressions
    // TODO cleanup implementation
    // TODO See if FDr changes in WithinResolver help with integration
    var forBasis = new ExpressionBasisTypeVisitor();
    forBasis.setType4Ast(type4Ast);
    typeTraverser.add4ExpressionsBasis(forBasis);

    var forLiterals = new MCCommonLiteralsTypeVisitor();
    forLiterals.setType4Ast(type4Ast);
    typeTraverser.add4MCCommonLiterals(forLiterals);

    var forCommon = new SysMLCommonExpressionsTypeVisitor();
    forCommon.setType4Ast(type4Ast);
    typeTraverser.add4CommonExpressions(forCommon);
    typeTraverser.setCommonExpressionsHandler(forCommon);
    typeTraverser.add4SysMLExpressions(forCommon);
    typeTraverser.setSysMLExpressionsHandler(forCommon);

    var forOcl = new SysMLOCLExpressionsTypeVisitor();
    forOcl.setType4Ast(type4Ast);
    typeTraverser.add4OCLExpressions(forOcl);
    typeTraverser.add4SysMLExpressions(forOcl);

    var forStreams = new StreamExpressionsTypeVisitor();
    forStreams.setType4Ast(type4Ast);
    typeTraverser.add4StreamExpressions(forStreams);

    var forSets = new SysMLSetExpressionsTypeVisitor();
    forSets.setType4Ast(type4Ast);
    typeTraverser.add4SetExpressions(forSets);
    typeTraverser.add4SysMLExpressions(forSets);

    // MCTypes

    var forBasicTypes = new MCBasicTypesTypeVisitor();
    forBasicTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCBasicTypes(forBasicTypes);

    // TODO are MCSimpleGenerics required?
    var forCollectionTypes = new MCCollectionTypesTypeVisitor();
    forCollectionTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCCollectionTypes(forCollectionTypes);

    // create delegate
    SysMLTypeCheck3 sysmlTC3 = new SysMLTypeCheck3(typeTraverser, type4Ast);
    sysmlTC3.setThisAsDelegate();
  }

  public SysMLTypeCheck3(
      ITraverser typeTraverser, Type4Ast type4Ast) {
    super(typeTraverser, type4Ast);
  }
}
