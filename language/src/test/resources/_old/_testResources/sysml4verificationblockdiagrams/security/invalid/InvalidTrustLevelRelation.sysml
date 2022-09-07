/**
 * Beispiel für TrustLevelRelation ueber eine Partproperty die nicht existiert
 */
package invalidTLR {

  part def InvalidPart {
    trustLevelRelation nonExistingPartProp < existingPartProp;

    trustLevelRelation existingPartProp < portA;

    port portA : A;
    part existingPartProp : ExistingPart;
  }

  part def ExistingPart {}

  port def A;
}
