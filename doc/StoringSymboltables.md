<!-- (c) https://github.com/MontiCore/monticore -->
# (De)Serialisierung von Symboltabellen mit identischen ArtifactScope-Namen

## Problem

Beim Serialisieren von zwei Modellen mit identischen Paketnamen, z.B.package
MyPackage (explizit in SysML erlaubt),kann dem Symbolspeichermechanismus ein
Pfad zur Ziel-.sym-Datei bereitgestellt werden.Beim erneuten Laden sucht der
Auflösungsmechanismus jedoch nach MyPackage.sym im Symbolpfad,wenn kein
passendes Symbol gefunden wird (z.B. MyPackage.someSymbol).Da Symbole auf
beliebige Dateinamen gespeichert werden können, führt dies im Standardfall,in
dem die Datei MyPackage.sym heißt, zu einem Fehler.

## Naive Lösungen

Einführung eines Namensschemas für die Speicherung von ArtifactScopes mit
identischen Namen, zB. MyPackage$index.sym. Beim Laden kann dann teilweise – STs
werden geladen, bis das Symbol aufgelöst ist – oder vollständig – alle STs mit
dem angegebenen Paketpräfix werden geladen – vorgegangen werden.

Verwendung einer Ordnerstruktur, bei der alle Unterelemente mit einem Index
benannt werden, und Anwendung eines ähnlichen Ladeverfahrens, falls
Sonderzeichen in MontiCore problematisch sind.
