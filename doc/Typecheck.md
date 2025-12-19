<!-- (c) https://github.com/MontiCore/monticore -->
# Erfahrungen zum Typechecker

In diesem Dokument sammeln wir Erfahrungen, die wir beim Programmieren des Typecheckers für SysMLv2 gemacht haben.

## Grundwissen

Es folgt eine Auflistung an Grundwissen zum Implementieren eines Typecheckers.

* [ ] Visitor-Infrastruktur verstehen (Kapitel 8)
  * [ ] insbesondere Komposition von Visitoren und Handlern verschiedener Sprachen (Kapitel 8.2)
* [ ] Symbol Management Infrastruktur (Kapitel 9)
  * [ ] Symbole (Kapitel 9.1, 9.2)
  * [ ] Scopes, insbesondere Global & Artifact Scope (Kapitel 9.1, 9.3)
  * [ ] referenzierte Symbol auflösen (Kapitel 9.8)
  * [ ] nach adaptierten Symbolen auflösen (Kapitel 9.10.4)
* [ ] Konkrete Symbole und Types
  * [ ] Symbole (Kapitel 18.3)
  * [ ] Types (Kapitel 18.4)
* [ ] Das Typecheck Kapitel aus dem MontiCore Handbuch lesen und verstehen (Kapitel 18.6)
  * [ ] `SymTypeExpression` Klassen-Hierarchie und Unterschied zu sprach-spezifischen Typen (Kapitel 18.6.1)
  * [ ] Funktion der Klassen `TypeCheck` und `TypeCheckResult` sowie der Interfaces `IDerive` und `ISynthesize`. (Kapitel 18.6.2-18.6.3)
        Im Gegensatz zur Darstellung im MontiCore Handbuch implementiert die `TypeCheck`-Klasse nur die statischen Methoden.
        Die Interfaces `IDerive` und `ISynthesize` werden jeweils von abstrakten Klassen implementiert und in `TypeCalculator` verwendet.

## Komponenten

Eine Auflistung der verschiedenen Komponenten eines Typecheckers.

### DeriveSymTypeOf

Die `DeriveSymTypeOfL`-Klasse leitet einen `SymType` für die Konstrukte der Sprache `L` ab.
Dazu implementiert sie das Visitor- und/ oder das Handler-Interface der entsprechenden Sprache.
Wenn sie Expressions behandelt kann sie zudem die Klasse `AbstractDeriveFromExpression`.
In jedem Fall beinhaltet sie ein `TypeCheckResult`-Attribut sowie dessen Access-Methoden.
Darüber werden Ergebnisse des Typechecks mit anderen `DeriveSymTypeOf`- und `Deriver`-Klassen ausgetauscht.

### Deriver

Die Klasse `LDerive` dient als Einstiegspunkt um einen Typecheck auf Konstrukten der Klasse `L` durchzuführen.
Dies sind üblicherweise entweder `ASTExpressions` oder `ASTLiterals`.
Dazu erweitert er die Klasse `AbstractDerive` und konfiguriert dessen Traverser mit `DeriveSymTypOf`-Klassen.
Es bietet sich an für jede Subsprache zu überprüfen, ob eine solche `DeriveSymTypOf`-Klasse bereits existiert.

```java
public class SysMLExpressionsDeriver extends AbstractDerive {

  public SysMLExpressionsDeriver() {
    super(SysMLExpressionsMill.traverser());

    DeriveSymTypeOfExpression forBasisExpr = new DeriveSymTypeOfExpression();
    forBasisExpr.setTypeCheckResult(typeCheckResult);
    getTraverser().add4ExpressionsBasis(forBasisExpr);
    getTraverser().setExpressionsBasisHandler(forBasisExpr);

    // more DeriveSymTypeOf... follow
  }

}
```

Einhängen einer `DeriveSymTypeOf`-Instanz zum Behandeln von Basis-Expressions in den Expression-Deriver der SysMLv2-Sprache.
In der vierten Zeile wird der Traverser der Zielsprache gesetzt.
In Zeile sieben wird eine Referenz zur `TypeCheckResult`-Instanz der `Derive`-Klasse übergeben, sodass `DeriveSymTypeOfExpression` sein Ergebnis setzen kann.
In den folgenden Zeilen werden Visitor und Handler in den Traverser eingehangen.

```java
TypeCheckResult type = new SysMLExpressionsDeriver().deriveType(someASTExpression);
```

Der Typecheck kann dann auf eine beliebige `ASTExpression` oder ein beliebiges `ASTLiteral` aufgerufen werden.

### TypeCheck

Die `TypeCheck`-Klasse und deren erweiternden `LTypeCheck`-Klassen sind stateless und bieten statische Hilfsmethoden.

### Scope: Adaptieren von Symbolen

Die `LScope`-Klasse ermöglicht das Adaptieren von Symbolen zwischen einer Startsprache `L` (z.B. SysMLv2) und einer Zielsprache (z.B. BasicSymbols).
Dadurch können ähnliche Typsysteme bestehender MontiCore-Sprachen wiederverwendet werden.
Dies kann ersparen `DeriveSymTypeOf`-Klassen für entsprechende Sprachen zu implementieren.
In der `LScope`-Klasse werden per Top-Mechanismus die `resolveAdaptedSLocallyMany`-Methoden überschrieben, wobei `S` ein Symbol der Zielsprache ist.
In ihr wird anhand der Parameter `boolean foundSymbols, String name, AccessModifier modifier, Predicate<TypeSymbol> predicate` nach Symbolen der Startsprache resolved.
Diese werden dann zu Symbolen der Zielsprache umgebaut und in eine Liste, eingefügt die zurückgegeben wird.
Von dort an, übernehmen die Typecheker-Implementierungen der Zielsprache.
Die `resolveAdapted...`-Methoden werden durch die `resolve`-Methoden der Zielsprache aufgerufen, sofern diese noch keine entsprechenden Symbole gefunden haben.

#### resolveAdapted eingrenzen

Die `LScope`-Klasse kann mehrere Hundert `resolve`-Methoden; für die SysMLv2-Sprache sind es beispielsweise über 600.
Daher ist es wichtig die Kandidaten zum Überschreiben effektiv eingrenzen zu können.
Wir interessieren uns für die Adapter-Methoden und können damit die Suche auf Methoden mit `resolveAdapted...` im Namen eingrenzen.
Im nächsten Schritt müssen wir uns bewusst werden zu welchen Symbol-Typ wir adaptieren wollen.
Dafür können wir die Auswahl auf die Typen unserer Zielsprache eingrenzen.
Im Falle der SysMLv2 war dies `BasicSymbols` mit den Symbolen `DiagramSymbol`, `TypeSymbol`, `TypeVarSymbol` (äquivalent zu Typparametern in Java), `VariableSymbol`, `FunctionSymbol`.
Nun müssen wir schauen welcher Symbol-Typ am besten zu dem Nichtterminal unserer Startsprache passt.
Im Falle der SysMLv2 ließen sich alle (bisherigen) Symbole entweder auf Variablen- oder Typsymbole aus BasicSymbols zurückführen.

#### Symbol der Startsprache resolven

Als nächsten Schritt resolven wir das Symbol der Startsprache und adaptieren es.
Dabei kann sich die Strategie abhängig von der entsprechenden Adapter-Methode unterscheiden.
Daher beschreiben wir das Vorgehen für die Adapter-Methoden der SysMLv2, wobei dies für andere Start/- Zielsprachen durchaus anders sein kann.

##### resolveAdaptedVariableLocallyMany

Wir resolven nach Symbolen die die gleiche Funktion wie Variablen übernehmen.
Diese werden eher in einem lokalen Scope definiert und können, wenn dies der Fall ist, mit `resolve...LocallyMany(...)`-Methoden resolved werden (siehe Listing, Zeile 8).
Die Parameter dieser Methoden belegen wir, so dass Symbole ausschließlich anhand ihres Namens resolved werden.
Es werden also keine Symbole ausgeschlossen, weil Symbole bereits gefunden wurden, Modifier oder ein Prädikat nicht übereinstimmen.
Dies ergibt folgende Variablen-Belegung `foundSymbols = false`, `modifier = AccessModifier.ALL_INCLUSION`, `predicate = x -> true`.

Danach iterieren wir über die gefundenen Symbole und adaptieren sie (Zeile 10 ff.).
So weit wie möglich setzen wir die Attribute des `VariableSymbolBuilder`s.
Dabei nimmt das `Type`-Attribut eine besondere Rolle ein:
Anhand dessen können in folgenden Resolve-Schritten Typdefinitionen gefunden und ggf. adaptiert werden.
Der Type wird als Instanz einer `SymTypeExpression` gesetzt.
Wir müssen also zwei Sachen machen:
Erstens, die konkrete Klasse für die SymTypeExpression bestimmen.
Zweitens, den Typ unseres zu adaptierenden Symbols bestimmen und in die SymTypeExpression einsetzen.

Für den ersten Schritt haben wir überlicherweise die Auswahl zwischen `Array`, `Obscure`, `OfFunction`, `OfGenerics`, `OfNull`, `OfObject`, `OfWildcard`, `Primitive`, `Variable`, `Void` (Kapitel 18.6.1).
Dabei dürften `Array`, `OfObject`, `OfGenerics`, und `Primitive` die interessanteren Kandidaten sein.

```java
public List<VariableSymbol> resolveAdaptedVariableLocallyMany(
    boolean foundSymbols, // ignore
    String name, // resolve for
    AccessModifier modifier, // ignore
    Predicate<VariableSymbol> predicate // ignore
) {
  var adapted = new ArrayList<VariableSymbol>(); // stores adapted variables
  var usages = resolvePortUsageLocallyMany(false, name, AccessModifier.ALL_INCLUSION, x -> true);

  for(PortUsageSymbol portUsage : usages) {
    var type = // ... resolve exactly one type for the port usage

    var variable = BasicSymbolsMill.variableSymbolBuilder()
        .setName(portUsage.getName())
        .setEnclosingScope(portUsage.getEnclosingScope())
        .setFullName(portUsage.getFullName())
        .setPackageName(portUsage.getPackageName())
        .setAccessModifier(portUsage.getAccessModifier())
        .setType(new SymTypeOfObject(type))
        .build();

    adapted.add(variable);
  }

  return adapted;
}
```

##### resolveAdaptedTypeLocallyMany

Ähnlich zum Resolven von adaptierten Variablen, müssen wir zuerst Symbole der Zielsprache resolven.
Dazu verwenden wir die `resolve(String name)`-Methoden, da sie nicht nur lokal nach Typ-Definitionen sucht.
Die beschriebene Parameter-Belegung von `foundSymbol`, `modifier`, und `predicate` wird dadurch überladene Methoden automatisch gesetzt.

Abgesehen von dem verwendeten `BasicSymbolsMill.typeSymbolBuilder()`, ist das Vorgehen nun äquivalent.

```java
BasicSymbolsMill.typeSymbolBuilder()
  // Namen setzen
  .setName(symbol.getName())
  .setPackageName(symbol.getPackageName())
  .setFullName(symbol.getFullName())

  // Modifier setzen
  .setAccessModifier(symbol.getAccessModifier())

  // Scopes setzen
  .setSpannedScope(symbol.get().getSpannedScope())
  .setEnclosingScope(symbol.getEnclosingScope());
```

#### Adaptieren Checkliste

* [ ] Zielsprache festlegen, z.B. `BasicSymbols`
* [ ] Symbol-Typ der Zielsprache auswählen, z.B. `VariableSymbol` oder `TypeSymbol`
* [ ] `resolveAdapted...LocallyMany`-Methode der `de.monticore.lang.l._symboltable.LScope`-Klasse der Startsprache `L` überschreiben
* [ ] Symbol-Typ `S` der Startsprache auswählen der adaptiert werden soll
* [ ] Nach Symbol der Startsprache resolven, zum Beispiel mit `resolveLocallyMany(...)` oder `resolve(String)`
* [ ] Adaptiertes Symbol bauen und zurückgeben

Nun kann beispielsweise nach Variable resolved werden (`resolveVariable(String name)`) und es werden die adaptieren Symbole gefunden.

### Mill

Die Mill kann dazu verwendet werden vordefinierte Typen, die nicht Teil der Modelle sind, in das `GlobalScope` zu laden.
Dies wird beispielsweise bei den vordefinierten Stream-Typ der SysMLv2 gemacht.
Dabei kann das Static Delegator Pattern ähnlich zum generierten Teil der Mill verwendet werden.

```java
// überschreiben der Mill per TOP-Mechanismus
public class SysMLv2Mill extends SysMLv2MillTOP {

  // statische Methode zum Initialisieren der Mill
  public static void addStreamType() {
    getMill()._addStreamType();
  }

  // Methoden zum Überschreiben wie die Mill gesetzt werden kann
  protected void _addStreamType() {
    // definieren des Stream-Typ und hinzufügen zum dem GlobalScope
  }
}
```

### Generische Typen

Für generische Typen gibt es (mindestens) drei Fälle zu beachten:
1. resolven nach generischen Typen aus Modellen
2. erstellen von generischen Typen per Hand
3. belegen der Typvariablen (bzw. Typparameter) mit konkreten Typen

Im Typechecker für SysMLv2 fanden bisher nur die letzten beiden Varianten anwendung und werden daher beschrieben.

#### Erstellen generische Typen

In dem Abschnitt zur Mill haben wir bereits beschrieben wie vordefinierte Typen in das Global Scope hinzugefügt werden können.
In diesem Abschnitt beschreiben wir wie generische Typen mit Funktionen gebaut werden können.
Dies kann dann in der `_addStreamType()`- oder einer äquivalenten Methoden geschehen.

##### Typvariable

Erstellen einer Typvariable mit dem Namen `E`:

```java
BasicSymbolsMill.typeVarSymbolBuilder().setName("E").build();
```

Typvariablen werden als `TypeVarSymbol` definiert.

##### Funktion

Erstellen einer Funktion `get(int n): E`:

```java
protected FunctionSymbol buildGetFunction(TypeVarSymbol typeVar) {
  var parameterList = new BasicSymbolsScope();

  // primitiver Parametertyp
  SymTypePrimitive intType = SymTypeExpressionFactory.createPrimitive(SysMLv2Mill.globalScope().resolveType("int").get());

  // Parameter
  VariableSymbol parameter = SysMLv2Mill.variableSymbolBuilder().setName("n").setType(intType).build();
  parameterList.add(parameter);

  // Rückgabetyp
  SymTypeVariable returnType = SymTypeExpressionFactory.createTypeVariable(typeVar);

  return BasicSymbolsMill.functionSymbolBuilder()
      .setName("get")
      .setSpannedScope(parameterList)
      .setType(returnType)
      .build();
}
```

Funktionen werden als `FunctionSymbol` definiert.
Ihr Builder der `BasicSymbols`-Grammatik erhählt den Funktionsname als `String`, die Parameterlist als `BasicSymbolsScope`, und einen Rückgabewert als `SymTypeExpression`.
Parameter selbst werden als `VariableSymbol` mit entsprechenden Typ als `SymTypeExpression` erstellt.
Der Rückgabetyp selbst ist eine `SymTypeVariable`.

##### Generischer Typ

Erstellen eines generischen Typens `Stream<E>` mit der Funktion `get(int n): E`:

```java
protected OOTypeSymbol buildStreamType() {
  var typeVar = buildTypeVar();

  var spannedScope = new OOSymbolsScope();
  spannedScope.add(typeVar);
  spannedScope.add(buildGetFunction(typeVar));

  return OOSymbolsMill.oOTypeSymbolBuilder()
      .setName("Stream")
      .setSpannedScope(spannedScope)
      .build();
}
```

Generische Typen werden als `OOTypeSymbol` erstellt.
Sie spannen ein Scope auf, welches die Typvariable(n) sowie die Funktionen des generischen Typs enthält.

#### Belegen der Typevariable

Bisher haben wir den generischen Stream-Typen `Stream<E>` erstellt und im Global Scope abgelegt.
In Zeile 1 resolven wir diesen aus dem Global Scope.
In Zeile 2 erstellen bzw. resolven wir den konkreten Typen für Typvariable `E`.
In der letzten Zeile bauen wir einen generischen Typen der sowohl `Stream<E>` als auch den `concreteType` speichert.
Dieser generische Typ kann nun im Typechecker verwendet werden, beispielsweise als Typ einer adaptierten Variable.

```java
Optional<TypeSymbol> streamType = SysMLv2Mill.globalScope().resolveType("Stream");
SymTypeExpression concreteType = //...
SymTypeOfGenerics concreteStream = SymTypeExpressionFactory.createGenerics(streamType.get(), concreteType);
```

Die Klasse `SymTypeExpression` bietet die Methode `replaceTypeVariables(Map<TypeVarSymbol, SymTypeExpression> replaceMap)` die durch den Typechecker mit der Map `E -> concreteType` aufgerufen wird.
Die Implementierung in `SymTypeExpression` selbst ist leer.
Jedoch bietet `SymTypeOfGenerics` eine Implementierung die den Ersetzungschritt durchführt.
Dieser wird einmal entlang der aufgespannten Scopes des generischen (Stream) Objektes durchgeführt.
Wenn eine Typvariable `E` beispielsweise in der Funktion `get(int n): E` durch den konkreten Typen ersetzt wird, kann dies u.a. an falschen Typ-Symbolen liegen die den generische Typen und dessen Funktionen bilden.




### Type Check von StreamConstructorExpression

Eine StreamConstructorExpression ist eine spitze Klammernotation, die
eine oder mehrere Expressions enthält – zum Beispiel `<x>`.
Dabei ist `x` eine innere Expression mit dem Typ `T`.
Der gesamte Expression `<x>` hat dann den Typ `Stream<T>`.

Zum Beispiel `<true>` ist ein Stream mit genau ein Element
,nämlich `true`, es hat den Typ `stream<boolean>`.

Um StreamConstructorExpression richtig type checken zu können, wird
die Methode `endVisit(ASTStreamConstructorExpression expr)` im
Visitor für Stream Expression überschrieben :

```java
public class SysMLv2DeriveSymTypeOfStreamConstructorExpression extends AbstractDeriveFromExpression implements StreamExpressionsVisitor2, StreamExpressionsHandler{
  @Override
  public void endVisit(ASTStreamConstructorExpression node) {
    SymTypeExpression type = getTypeCheckResult().getResult();
    calculateCorrectType(type);
  }

  protected void calculateCorrectType(SymTypeExpression type) {
    var streamType = SysMLv2Mill.globalScope().resolveType("Stream");
    if(streamType.isEmpty()) {
     Log.error("0x81010 Stream not defined in global scope. Initialize it with 'SysMLv2Mill.addStreamType()'!");
    }
    type = SymTypeExpressionFactory.createGenerics(streamType.get(), type);
    getTypeCheckResult().setResult(type);
  }
}
```
Der Typ des inneren Expressions `T` wird in einen generischen
Stream-Typ `Stream<T>` verpackt.

Am Ende registrieren wir diesen Visitor
`SysMLv2DeriveSymTypeOfStreamConstructorExpression`-Klasse
beim SysMLDeriver.

#### Behandeln mehrere Elemente im StreamConstructorExpression:
Im StreamConstructorExpression können mehrere Elemente vorkommen.
Diese Elemente müssen alle denselben Typ haben.
Unterschiedliche Typen in einem StreamConstructorExpression sind nicht erlaubt.


```java
@Override
public void traverse(ASTStreamConstructorExpression node) {
  getTypeCheckResult().reset();
  var first = node.getExpressionList().get(0);
  first.accept(getTraverser());
  TypeCheckResult fValue = getTypeCheckResult().copy();
  List<ASTExpression> eList = node.getExpressionList();
  for (ASTExpression e : eList) {
    e.accept(getTraverser());
    TypeCheckResult eValue = getTypeCheckResult().copy();
    if (!fValue.getResult().deepEquals(eValue.getResult())) {
      var start = node.get_SourcePositionStart();
      var end = node.get_SourcePositionEnd();
      Log.error("Stream Expressions cannot contain multiple types", start, end);
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
    }
  }
}

@Override
public void endVisit(ASTStreamConstructorExpression expr) {
  TypeCheckResult inner = getTypeCheckResult().copy();
  getTypeCheckResult().setResult(StreamSymTypeFactory.createStream(inner.getResult()));
}
```
In der Methode `traverse(ASTStreamConstructorExpression node)` wird
zunächst der Typ des ersten Elements-`fValue` in einem Stream bestimmt.
Anschließend wird überprüft, ob alle übrigen Elemente im Stream
denselben Typ wie das erste Element haben.

Falls ein Fehler auftritt, wird ein `Obscure` Type zurückgegeben.

### Die`times` Funktion für StreamConstructorExpression

Die `times` Funktion dient dazu, einen Wert, zum Beispiel `true`, mehrfach in
einem Stream zu wiederholen, zum Beispiel `k`-mal.

Mögliche Syntaxformen dafür sind `<true>^k` oder `<true>.times(k)`, wobei `k` eine natürliche Zahl ist.

Zum Beispiel ergibt der Expression `<true>.times(2)` und `<true>^2` den Stream `<true, true>`.

Die gewünschte Syntax: `<true>^k` kann aber nicht unterstützt werden,
da der Operator `^` bereits in der  Grammatik `SysMLExpressions.mc4` verwendet wird:

```java
CalcDefPowerExpression implements Expression =
base:Expression "^" exponent:Expression ;
```
Deshalb kann die `times` Funktion nur in der in der `SysMLv2Mill.java`-Klasse gebaut werden:


```java
protected FunctionSymbol buildTimesFunction(TypeSymbol streamSymbol, TypeVarSymbol typeVar) {
    var parameterList = new BasicSymbolsScope();

    VariableSymbol parameter = SysMLv2Mill.variableSymbolBuilder().setName(
        "k").setType(buildNatType()).build();
    parameterList.add(typeVar);
    parameterList.add(parameter);

    var returnType = SymTypeExpressionFactory.createGenerics(streamSymbol, SymTypeExpressionFactory.createTypeVariable(typeVar));

    return SysMLv2Mill.functionSymbolBuilder()
        .setName("times")
        .setType(returnType)
        .setSpannedScope(parameterList)
        .build();
}
```
Die Funktion wird mit dem Namen `"times"` erstellt und erhält
einen Parameter `k` vom Typ `Nat`.

Der `returnType` verwendet eine Typvariable-`typeVar` (mit dem Type `T`)
und verpackt diesen in einen Stream mit dem Typ `Stream<T>`.

Mit `var parameterList = new BasicSymbolsScope();` wird
ein neuer Symbol-Scope erstellt, der die Typvariable Typvariable-`T`
und Wertparameter-`k` enthält.

Dieser Scope dann wird später mithilfe von `setSpannedScope(...)`
als lokaler Scope der Funktion-`times` verwendet.

Die Syntax `<true>.times(k)` ist jetzt erlaubt.

### Symbol von Symtabdefinition laden

Zuvor wurde das Symbol `Stream` (inklusive zugehöriger Methoden wie times)
explizit über `SysMLv2Mill.addStreamType()` zur globalen Scope hinzugefügt.

Jetzt erfolgt die Definition dieser Typinformationen nicht mehr
manuell, sondern automatisch durch das Laden aus einer
`Stream.symtabdefinition`([Symtabdefinition](
https://github.com/MontiCore/monticore/blob/dev/monticore-libraries/stream-symbols/src/main/symtabdefinition/Stream.symtabdefinition)).


Das Import der stream-symbols dependency von Monticore in `build.gralde`
von `language` module ist nötig:

```java
implementation "de.monticore:stream-symbols:$mc_version"
```

In der `SysMLv2Tool.java`-Klasse:

```java
public void loadStreamSymbolsFromJar()  {
  URL streamDefUrl = SysMLv2Tool.class.getClassLoader().getResource("Stream.symtabdefinitionsym");
  if (streamDefUrl == null) {
    Log.error("0xPA090 Failed to find Stream.symtabdefinitionsym on the classpath.");
    return;
  }
  if (!"jar".equals(streamDefUrl.getProtocol())) {
    Log.error("0xPA091 Expected Stream.symtabdefinitionsym to be loaded from a JAR");
    return;
  }
```
Die Methode versucht zunächst, die `Stream.symtabdefinitionsym`
über den Classloader zu finden.

Dann es wird geprüft, ob die Ressource tatsächlich aus einem JAR geladen
wird.

```java
  JarURLConnection conn = null;
  JarFile jar = null;

  try {
    conn = (JarURLConnection) streamDefUrl.openConnection();
    jar = conn.getJarFile();
  }
  catch (IOException e) {
    Log.error("0xPA092 Failed to open symbol definition from JAR URL");
  }
```
Mittels JarURLConnection wird versucht, die tatsächliche JAR-Datei
zu öffnen. Bei Fehlern beim Öffnen wird ein Fehler geloggt.

```java
  Path jarPath = Path.of(jar.getName());

  SysMLv2Mill.globalScope().getSymbolPath().addEntry(jarPath);

  SysMLv2Mill.globalScope().putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
  SysMLv2Mill.globalScope().putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new MethodSymbolDeSer());
}
```
Der Pfad zur JAR-Datei wird dem Symbolpfad der globalen Scope hinzugefügt.

Zum Schluss werden Deserialisierer für bestimmte Symbolarten registriert.


##### Stream wird zu EventStream
Nach dem Wechsel zu der Benutzung der `.symtabdeifnition`-Dateien,
gibt es nun nicht mehr nur den allgemeinen Typ `Stream`,
sondern mehrere spezialisierte Stream-Typen wie `EventStream`,
`UntimedStream` und weitere.

Ursprünglich wurde in der
`SysMLv2DeriveSymTypeOfCommonExpressions.java` versucht, direkt `Stream` zu resolven:

```java
var streamType = SysMLv2Mill.globalScope().resolveType("Stream");
```
Das funktioniert technisch weiterhin, weil `Stream` nach wie vor
im Symboltable definiert ist. In der neuen Version wird aber explizit
`EventStream` verwendet:


```java
var streamType = SysMLv2Mill.globalScope().resolveType("EventStream");
```

Obwohl `Stream` weiterhin verfügbar und auflösbar ist,
es wird in konkreten Anwendungsfällen Methoden  wie
`nth`, `times` erwatet, die ausschließlich in
`EventStream<T>` definiert sind.

Das ist ein `EventStream.symtabdefinition`
([EventStream](https://github.com/MontiCore/monticore/blob/dev/monticore-libraries/stream-symbols/src/main/symtabdefinition/EventStream.symtabdefinition)).
:

```java
symtabdefinition EventStream {

class EventStream<T> implements Stream<T> {
   + UntimedStream<T> nth(long n);
   + EventStream<T> times(long n);
...
}

}
```
`EventStream<T>` implementiert das Interface `Stream<T>`.
Die Methoden wie `nth` und `times` sind an `EventStream<T>` gebunden.

Die Methode `nth` hat ein Argument `n` vom
Typ `long` und gibt einen Wert vom Typ `UntimedStream<T>` zurück.

##### Effekt
Die Umstellung von `Stream` auf `EventStream` hat zur Folge, dass
Methoden wie `times` nun konkreter auf `EventStream<T>`
statt auf das allgemeinere Interface `Stream<T>` zurückgeführt werden.

Beispiel:

* [ ] Die Expresison `<true>.times(5)` hat jetzt den Typ `EventStream<boolean>`
  statt wie zuvor `Stream<boolean>`.
* [ ] Die Expresison `<true>.nth(5)` hat jetzt den Typ `UntimedStream<boolean>`
  statt wie zuvor `Stream<boolean>`.

##### Anpassungen in `SysMLGlobalScope.java`

Da es erfolgt nicht mehr durch mill klasse
sondern über .symtabdefinition-Dateien geladen werden, musste die
SysMLGlobalScope entsprechend angepasst werden. In zuvor implemetierung:
```java
  public  void loadFileForModelName (String modelName) {
  java.util.Optional<java.net.URL> location = getSymbolPath().find(modelName, getFileExt());

  try {
    if(location.isPresent()) {
      var potArtScopeName = Files.getNameWithoutExtension(Paths.get(location.get().toURI()).getFileName().toString());
      ...
```
Diese Vorgehensweise funktionierte solange die Dateien direkt im
Dateisystem lagen.  In diesem Fall akzeptiert `Paths.get(...)` den URI problemlos.

###### Problem bei Ressourcen aus JAR-Dateien
Nach dem Umstieg auf veröffentlichte Symboltabellen
im JAR-Format, enthält `location` Werte wie:
```java
jar:file:///C... stream-symbols-7.8.0-SNAPSHOT.jar!/Stream.symtabdefinition
```
Der Aufruf von `Paths.get(location.get().toURI())` schlägt hier
jedoch fehl.
`Paths.get(...)` funktioniert nur mit echten Dateisystempfaden,
nicht mit Ressourcen innerhalb eines JAR-Archivs.

Da sich die `.symtabdefinition`-Dateien nun innerhalb
von JAR-Dateien befinden, führt der Versuch, den Pfad auf
diese Weise zu extrahieren, zu einer
`FileSystemNotFoundException`.

Deshalb wurde die Pfadverarbeitung angepasst, sodass
```java
location.get()).toString())
```
verwendet wird, ohne `Paths.get()` zu benutzen.

#### Entsprechende Tests sind erstellt.
* [1]  [StreamConstructorExpression](https://github.com/MontiCore/sysmlv2/commit/e7e85161b3ea1f7f1a15f53c5154d1e38aef4eb3#diff-835f1b721457a93eaffdf87f0e4f6296e9acc8c1e074ab820f9838c531b2a13aR16)
* [2] [times Function](https://github.com/MontiCore/sysmlv2/commit/ff62447b2d3008cfbccafbfa000f959548ad3461)




