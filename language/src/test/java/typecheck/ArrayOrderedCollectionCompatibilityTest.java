package typecheck;

import de.monticore.lang.sysmlv2.SysMLv2Mill;
import de.monticore.lang.sysmlv2.SysMLv2Tool;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayOrderedCollectionCompatibilityTest {

  @BeforeEach
  public void setUp() {
    LogStub.init();
    new SysMLv2Tool().init();
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testArrayIsCompatibleWithoutBeingSubtype(boolean collectionArray) {
    var integer = SymTypeExpressionFactory.createPrimitive("int");
    var array = arrayOf(integer, collectionArray);
    var collection = orderedCollectionOf(integer);

    assertThat(SymTypeRelations.isCompatible(collection, array)).isTrue();
    assertThat(SymTypeRelations.isSubTypeOf(array, collection)).isFalse();
    assertThat(SymTypeRelations.isCompatible(array, collection)).isFalse();
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  public void testArrayWithDifferentElementTypeIsRejected(boolean collectionArray) {
    var integer = SymTypeExpressionFactory.createPrimitive("int");
    var bool = SymTypeExpressionFactory.createPrimitive("boolean");
    var array = arrayOf(integer, collectionArray);

    assertThat(SymTypeRelations.isCompatible(orderedCollectionOf(bool), array)).isFalse();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void testMultidimensionalArrayPreservesInnerArrays() {
    var integer = SymTypeExpressionFactory.createPrimitive("int");
    var matrix = SymTypeExpressionFactory.createTypeArray(integer, 2);
    var row = SymTypeExpressionFactory.createTypeArray(integer, 1);

    assertThat(SymTypeRelations.isCompatible(orderedCollectionOf(row), matrix)).isTrue();
    assertThat(SymTypeRelations.isCompatible(orderedCollectionOf(integer), matrix)).isFalse();
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {"Stream", "EventStream", "SyncStream", "ToptStream", "UntimedStream"})
  public void testStreamsRemainSeparate(String streamName) {
    var integer = SymTypeExpressionFactory.createPrimitive("int");
    var stream = SymTypeExpressionFactory.createGenerics(
        SysMLv2Mill.globalScope().resolveType(streamName).orElseThrow(), integer);

    assertThat(SymTypeRelations.isCompatible(orderedCollectionOf(integer), stream)).isFalse();
    assertThat(Log.getFindings()).isEmpty();
  }

  private SymTypeExpression arrayOf(SymTypeExpression elementType, boolean collectionArray) {
    if (collectionArray) {
      var arraySymbol = SysMLv2Mill.globalScope().resolveType("Collections.Array").orElseThrow();
      // Die Regel muss auch bei generierten Symbolen ohne Obertypen funktionieren.
      arraySymbol.getSuperTypesList().clear();
      return SymTypeExpressionFactory.createGenerics(arraySymbol, elementType);
    }
    return SymTypeExpressionFactory.createTypeArray(elementType, 1);
  }

  private SymTypeExpression orderedCollectionOf(SymTypeExpression elementType) {
    return SymTypeExpressionFactory.createGenerics(
        SysMLv2Mill.globalScope().resolveType("Collections.OrderedCollection").orElseThrow(), elementType);
  }
}
