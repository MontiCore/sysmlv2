package de.monticore.lang.sysmlv2.types;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.lang.sysmlparts.symboltable.adapters.PortUsage2VariableSymbolAdapter;
import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.ocl.oclexpressions._ast.ASTOCLArrayQualification;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypePrimitive.unbox;

/**
 * <p>In SysMLv2, the expression in StateUsage is not type of Stream.
 * Rewrite method {@link #calculateFieldAccess(ASTFieldAccessExpression, boolean)} for this purpose.</p>
 */
public class SysMLv2DeriveSymTypeOfCommonExpressions extends DeriveSymTypeOfCommonExpressions {
  /**
   * @see SysMLDeriver#isStream
   */
  protected boolean isStream;

  public SysMLv2DeriveSymTypeOfCommonExpressions(boolean isStream) {
    super();
    this.isStream = isStream;
  }

  /**
   * Übernommen von TypeCheck.isInt
   */
  protected boolean isNat(SymTypeExpression type) {
    return "nat".equals(unbox(type.print()));
  }

  /**
   * Nat hinzufügen
   */
  @Override
  public boolean isIntegralType(SymTypeExpression type) {
    return (TypeCheck.isLong(type) || TypeCheck.isInt(type) ||
        TypeCheck.isChar(type) || TypeCheck.isShort(type) ||
        TypeCheck.isByte(type)) || isNat(type);
  }

  /**
   *  Calculate type of FieldAccessExpression according to value of isStream,
   *  if FieldAccessExpression's left side is a PortUsageSymbol.
   * @param type is calculated in {@link #calculateFieldAccess(ASTFieldAccessExpression, boolean)}
   */
  protected void calculateFieldAccessAboutPortUsage(SymTypeExpression type) {
    if (this.isStream) {
      //case for isStream == true
      if (type.getTypeInfo().getName().contains("Stream")) {
        //type is already Stream, set TypeCheckResult
        getTypeCheckResult().setResult(type);
      } else {
        //but 'type' is not Stream, we create type of Stream based on 'type'
        // resolve the globally defined generic Stream-type
        var streamType = SysMLv2Mill.globalScope().resolveType("Stream");
        if(streamType.isEmpty()) {
          Log.error("Stream not defined in global scope. Initialize it with 'SysMLv2Mill.addStreamType()'!");
        }
        if (type instanceof SymTypeArray) {
          //for example, type like boolean[], int[]...
          //type = Stream<type>[], not Stream<type[]>
          var innerType = ((SymTypeArray) type).getArgument();
          //create Stream<innerType> first
          var StreamInnerType = SymTypeExpressionFactory.createGenerics(streamType.get(), innerType);
          //then Stream<innerType>[]
          type = SymTypeExpressionFactory.createTypeArray(StreamInnerType.getTypeInfo(), 1,
              StreamInnerType);
        } else {
          //type is like boolean, int...
          //type = Stream<type>, create SymTypeOfGenerics Stream<type>
          type = SymTypeExpressionFactory.createGenerics(streamType.get(), type);
        }
        //check whether type is a Stream
        if (type.getTypeInfo().getName().contains("Stream")) {
          getTypeCheckResult().setResult(type);
        } else {
          Log.error("type should be Stream");
        }
      }
    } else {
      //case for isStream == false
      if (!type.getTypeInfo().getName().contains("Stream")) {
        //type is also not Stream, set TypeCheckResult
        getTypeCheckResult().setResult(type);
      } else {
        //but type is Stream
        Log.error("type should not be Stream");
      }
    }
  }

  /**
   * <p>This method is copied from the superclass. </p>
   * <p>TypeCheckResult is finally set in this methode, some modifications have been made here,
   * we set TypeCheckResult according to the value of {@code isStream}.</p>
   *
   * @see de.monticore.types.check.DeriveSymTypeOfBSCommonExpressions#traverse(ASTFieldAccessExpression)
   * @see de.monticore.types.check.DeriveSymTypeOfBSCommonExpressions#calculateNamingChainFieldAccess(ASTFieldAccessExpression, List)
   * @see de.monticore.types.check.DeriveSymTypeOfBSCommonExpressions#calculateFieldAccess(ASTFieldAccessExpression, boolean)
   */
  @Override
  protected void calculateFieldAccess(ASTFieldAccessExpression expr,
                                      boolean quiet) {
    TypeCheckResult fieldOwner = getTypeCheckResult().copy();
    SymTypeExpression fieldOwnerExpr = fieldOwner.getResult();
    TypeSymbol fieldOwnerSymbol = fieldOwnerExpr.getTypeInfo();
    if (fieldOwnerSymbol instanceof TypeVarSymbol && !quiet) {
      Log.error("0xA0321 The type " + fieldOwnerSymbol.getName() + " is a type variable and cannot have methods and attributes");
    }
    //search for a method, field or type in the scope of the type of the inner expression
    List<VariableSymbol> fieldSymbols = getCorrectFieldsFromInnerType(fieldOwnerExpr, expr);
    Optional<TypeSymbol> typeSymbolOpt = fieldOwnerSymbol.getSpannedScope().resolveType(expr.getName());
    Optional<TypeVarSymbol> typeVarOpt = fieldOwnerSymbol.getSpannedScope().resolveTypeVar(expr.getName());

    if (!fieldSymbols.isEmpty()) {
      //cannot be a method, test variable first
      //durch AST-Umbau kann ASTFieldAccessExpression keine Methode sein
      //if the last result is a type then filter for static field symbols
      if (fieldOwner.isType()) {
        fieldSymbols = filterModifiersVariables(fieldSymbols);
      }
      if (fieldSymbols.size() != 1) {
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1236", expr.get_SourcePositionStart());
        }
      }
      if (!fieldSymbols.isEmpty()) {
        VariableSymbol var = fieldSymbols.get(0);
        expr.setDefiningSymbol(var);
        SymTypeExpression type = var.getType();
        getTypeCheckResult().setField();
        //++++++++++ modify start here ++++++++++
        //in superclass is: getTypeCheckResult().setResult(type);
        if (expr.getExpression() instanceof ASTNameExpression) {
          //case for expression like f.a,f.a[1]
          if (((ASTNameExpression) expr.getExpression()).getDefiningSymbol().get() instanceof PortUsage2VariableSymbolAdapter) {
            calculateFieldAccessAboutPortUsage(type);
          } else {
            //else-case for SuperClass
            getTypeCheckResult().setResult(type);
          }
        } else if (expr.getExpression() instanceof ASTOCLArrayQualification) {
          //case for expression like f[1].a, f[1].a[1]
          var oclExpr = expr.getExpression();
          if (((ASTOCLArrayQualification) oclExpr).getExpression() instanceof ASTNameExpression) {
            var nameExpr = ((ASTOCLArrayQualification) oclExpr).getExpression();
            if (((ASTNameExpression) nameExpr).getDefiningSymbol().get() instanceof PortUsage2VariableSymbolAdapter) {
              calculateFieldAccessAboutPortUsage(type);
            } else {
              //else-case for SuperClass
              getTypeCheckResult().setResult(type);
            }
          } else {
            //else-case for SuperClass
            getTypeCheckResult().setResult(type);
          }
        } else {
          //else-case for SuperClass
          getTypeCheckResult().setResult(type);
        }
        //++++++++++ modify end here ++++++++++
      }
    } else if (typeVarOpt.isPresent()) {
      //test for type var first
      TypeVarSymbol typeVar = typeVarOpt.get();
      if(checkModifierType(typeVar)){
        SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeVariable(typeVar);
        expr.setDefiningSymbol(typeVar);
        getTypeCheckResult().setType();
        getTypeCheckResult().setResult(wholeResult);
      } else{
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1306", expr.get_SourcePositionStart());
        }
      }
    } else if (typeSymbolOpt.isPresent()) {
      //no variable found, test type
      TypeSymbol typeSymbol = typeSymbolOpt.get();
      if (checkModifierType(typeSymbol)) {
        SymTypeExpression wholeResult = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
        expr.setDefiningSymbol(typeSymbol);
        getTypeCheckResult().setType();
        getTypeCheckResult().setResult(wholeResult);
      } else {
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        if(!quiet) {
          logError("0xA1303", expr.get_SourcePositionStart());
        }
      }
    } else {
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      if(!quiet) {
        logError("0xA1317", expr.get_SourcePositionStart());
      }
    }
  }
}
