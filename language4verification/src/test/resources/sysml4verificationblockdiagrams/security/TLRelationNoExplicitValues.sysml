/**
 * Beispiele für korrekt angegebene TrustLevelRelations
 */
package harderVerification {

  part def SecurePart {
    trustLevelRelation riskyPartUsage < securePartUsage;

    part riskyPartUsage : SecurityRiskyPart;
    part securePartUsage : EvenSecurerPart;
  }

  part def ProblematicPart {
    trustLevelRelation riskyPartUsage > securePartUsage;

    part riskyPartUsage : SecurityRiskyPart;
    part securePartUsage : EvenSecurerPart;
  }

  part def SecurityRiskyPart;

  part def EvenSecurerPart;
}
