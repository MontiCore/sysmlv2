/* (c) https://github.com/MontiCore/monticore */
package 'Wi:th\\Bu dy' {
  //private import 'Wi::th\\ImpossibleName'::X::Y;
  //part def H :> 'Wi::th\\ImpossibleName'::X::Y;
}

package 'Wi::th\\ImpossibleName' {

  //public import WithNestation::A::**;
  //part def J :> WithNestation::A {
  //  private import WithNestation::**;
  //  part u :> b;
  //}

  //part def K {
  //    private import WithNestation::**;
  //    part u :> b;
  //}

  //part def K  {
  //  private import WithNestation::**;
  //  part u :> b;
  //}

  package C {
    //private import WithNestation::**;

    //item x = WithNestation
    //part u :> WithNestation::A::X::b.c {
    //  //part g :> b.c {
    //    part x : H;
    //  //}
    //}
  }
}

package Functional_Layer{
  action aB {
    action rAC {
      action rAF;
    }

    action def AD {
      action ad_1;
    }
  }
}

part pA {
  //private import Functional_Layer::**;
  perform Functional_Layer::aB.rAC.rAF;
  perform Functional_Layer::aB.rAC;
  // in performs kann er kombinieren
}

package S {
  part def ST {
    part def ST1;
  }
}

package FromImports {
  public import S::*;
}

package WithNestation {
  private import FromImports::*;

  part u: ST;// S::ST;

  part def A {
    part def X {
      part b {
        part c {
          part def H;
        }
      }
    }
    //calc def '..' specializes DataFunctions::'..' { in x: MeasurementUnit[1]; in y: MeasurementUnit[1]; return : MeasurementUnit[1]; }
    // '*' -> Mult (FunctionSymbol)
  }
//
  //part def Y {
  //  //private import A::*; // are imports relative? YES
  //  //part def B :> X; // works
  //  //part def U :> A::X; // equivalent
  //}
//
  //// from supertype
  //part def B :> A;
  ////part m :> B::b; // ofc resolvable but not accessible
  //part m : B;
  //part n :> m.b;

}

package Estranged {
  part def Sibling {
    part def Niece {
      part a {
        part b {
          part def C;
        }
      }
    }
  }
  part def Uncle {
    //private import Sibling::Niece::**;
    // part def NieceClone :> Niece;
    //part n :> b; // a

    // is fully valid
    //private import Sibling::Niece::**;
    //part n1 : C;

    // is fully valid
    // private import Sibling::Niece::a::b::*;
    // part n1 : C;
  }
}
