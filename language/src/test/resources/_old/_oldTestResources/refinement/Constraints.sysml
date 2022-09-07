/**
 * Einzelne Constraints untereinander verfeinern
 */
package constraintRefinement {

  constraint def Rough(b: boolean) {
    b == true || b == false
  }

  // die Paramter müssen gleich sein
  constraint def Refined(b: boolean) refines Rough {
    b == true
  }

  // mehrfach
  constraint def Multiple(b: boolean) refines Rough, Refined {
    b == true
  }

  // Was ist hiermit?
  /*constraint def Rough2 {
    attribute b : boolean;
    b == true || b == false
  }

  constraint def Refines2 refines Rough2 {
    redefines b = true;
    b == true || b == false
  }*/

  // Was wollen wir erlauben?
  //constraint TrueRough : Rough(true);

  // Funky: Wie läuft das zwischen Definition und Usage?
  //constraint RoughInstanz : Refined(true) refines TrueRough;

  // Oder so?
  //constraint RoughInstanz2 : Refined(true) refines Rough(true);

}
