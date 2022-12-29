<!-- (c) https://github.com/MontiCore/monticore -->
## TODO

### Anpassungen, die wir gemacht haben

### Zusammengereimte Dokumentation

### Grundtypen

___TODO: einmal alle Grundtypen auf denen der TypeChecker operiert zusammenfassen___
* ggf auch welche Operation möglich sind, z.B. replacement of TypeParameter ist nicht in jedem Typen möglich

* SymTypeVariable
* `SymTypeExpression.createTypeExpression` macht nicht immer das gleiche je nach Variante

Strategie um einige Fehler zu finden/ zu vermeiden:
1. Vergewissern das sprach-spezifische Typen konzeptuell richtig auf den Grundtypen zugewiesen werden
2. Vergewissern das dieses Mapping auch im Code (zur Laufzeit) existiert
   * Beim Verwenden von Methoden wie `SymTypeExpression.createTypeExpression` sicherstellen, das der richtige Typ zurückgegeben wird
   * Verwenden von Konstruktoren kann etwas mehr Sicherheit bieten

### Scope

___TODO: Beitrag zum Typechek___
_adapter Methoden_

### SymboltableCompleter

___TODO: Beitrag zum Typechek___

### Mill

___TODO: Beitrag zum Typechek___

### Generics

___Erklären wie man Generics implementieren kann___
