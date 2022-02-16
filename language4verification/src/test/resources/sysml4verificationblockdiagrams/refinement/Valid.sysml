/**
 * Beispiele für korrekt angegebenes Refinement
 */
package valid {

  part def A;

  part def B refines A;

  part def C refines A, B;

}
