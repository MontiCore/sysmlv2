requirement def MassLimitationRequirement {
  doc /* The actual mass shall be less than or equal
       * to the required mass. */

  attribute massActual : MassValue;
  attribute massReqd : MassValue;

  require constraint { massActual <= massReqd }
}