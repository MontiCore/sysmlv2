package de.monticore.lang.kerml;

import de.monticore.lang.kerml._visitor.KerMLTraverser;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.visitor.ITraverser;

/**
 * Boilerplate, alles aus SysML kopiert
 */
public class KerMLTypeCheck3 extends MapBasedTypeCheck3 {

  public KerMLTypeCheck3(ITraverser typeTraverser, Type4Ast type4Ast) {
    super(typeTraverser, type4Ast, new InferenceContext4Ast());
  }

  public static void init() {
    initTC3Delegate();
  }

  private static void initTC3Delegate() {
    KerMLTraverser typeTraverser = KerMLMill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();

    // MCTypes
    var forBasicTypes = new MCBasicTypesTypeVisitor();
    forBasicTypes.setType4Ast(type4Ast);
    typeTraverser.add4MCBasicTypes(forBasicTypes);

    // create delegate
    KerMLTypeCheck3 sysmlTC3 = new KerMLTypeCheck3(typeTraverser, type4Ast);
    sysmlTC3.setThisAsDelegate();
  }
}
