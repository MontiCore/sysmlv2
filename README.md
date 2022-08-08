## SysML v2 - [`language`](language)

MontiCore Implementation of the official SysML v2 specification according to the [SysML Submission Team (SST)](https://github.com/Systems-Modeling),
specifically three core documents:
* [1-Kernel_Modeling_Language](https://github.com/Systems-Modeling/SysML-v2-Release/blob/master/doc/1-Kernel_Modeling_Language.pdf)
* [2-OMG_Systems_Modeling_Language](https://github.com/Systems-Modeling/SysML-v2-Release/blob/master/doc/2-OMG_Systems_Modeling_Language.pdf)
* [3-Systems_Modeling_API_and_Services](https://github.com/Systems-Modeling/SysML-v2-Release/blob/master/doc/3-Systems_Modeling_API_and_Services.pdf)

With additional material for quick reference and examples:
* [Intro to the SysML v2 Language-Graphical Notation](https://github.com/Systems-Modeling/SysML-v2-Release/blob/master/doc/Intro%20to%20the%20SysML%20v2%20Language-Graphical%20Notation.pdf)
* [Intro to the SysML v2 Language-Textual Notation](https://github.com/Systems-Modeling/SysML-v2-Release/blob/master/doc/Intro%20to%20the%20SysML%20v2%20Language-Textual%20Notation.pdf)

## Visualization - [`visualization`](visualization)

The official pilot implementation for visualization wrapped as a gradle project (requires GitHub [setup](visualization/README.md)):
* [SysML-v2-Pilot-Implementation/Interactive](https://github.com/Systems-Modeling/SysML-v2-Pilot-Implementation/tree/master/org.omg.sysml.interactive)

## Profile for Verification - [`language4verification`](language4verification)

A derivative for formal verification. Based on findings from the MontiBelle project:
* [[KMP+21] Model-Based Development and Logical AI for Secure and Safe Avionics Systems: A Verification Framework for
  SysML Behavior Specifications](https://www.se-rwth.de/publications/Model-Based-Development-and-Logical-AI-for-Secure-and-Safe-Avionics-Systems-A-Verification-Framework-for-SysML-Behavior-Specifications.pdf)

  ![](doc/meta_cd.png)
* [[KPRR21] Model-Based Design of Correct Safety-Critical Systems using Dataflow Languages on the Example of SysML
  Architecture and Behavior Diagrams](https://www.se-rwth.de/publications/Model-Based-Design-of-Correct-Safety-Critical-Systems-using-Dataflow-Languages-on-the-Example-of-SysML-Architecture-and-Behavior-Diagrams.pdf)

  ![](doc/event_transition.png)

## Editor Support - [`language-server`](language-server)

A [language server](https://microsoft.github.io/language-server-protocol/) implementation for SysML v2 in MontiCore,
generated via the [MontiCore Language Server Generator (MCLSG)](https://git.rwth-aachen.de/monticore/tools/lsp-generator)
based on the profile for verification. Enables the following editor features:

* Syntax highlighting

  ![](doc/highlighting.png)
* In-place suggestions for auto-completion

  ![](doc/completion.png)
* In-place error reporting

  ![](doc/errors.png)
* In-place suggestions for error-correction

## -- T4V Doc --
### Einleitung
Im Projekt [T4V](https://git.rwth-aachen.de/montibelle/frontend/language/text4verification) wurde eine Language Aggregation von einer Grammatik, die semi-strukturierten natürlich-sprachlichen Text darstellt, mit SysML erstellt. Wir entschieden uns zwar im Verlauf des Projektes gegen die Erstellung einer Supergrammatik beider Grammatiken, jedoch kann dies für spätere Projekte sinnvoll sein. So kann, um Language Embedding in z.B. `requirement def` zu ermöglichen, eine Grammatik samt Language Server, welcher die notwendigen Schnittstellen bietet, mit dem später vorgestellten Approach entwickelt werden.

### Ausgangssituation  
Als Ausgang besaßen wir zwei Sprachen: Die T4V, welche natürlich-sprachliche Texte durch CoreNLP zu einem CoreDoc parsed, welches dann nach Vereinfachungen in einen AST überführt wurde mit dem man Referenzen innerhalb eines Dokumentes erkennen und verfolgen konnte sowie die SysMLv2, auf welche wir diese Referenzen zu erweitern versuchten.

### Herangehensweise A: Aggregierte Sprache
Unsere erste - und auch die naheliegendste - Idee war es, eine Grammatik für die beiden Sprachen zu erstellen, also eine minimale Monticore-Sprache, die beide Grammatiken erweitert. Dies würde dazu führen, dass die Symbole, die wir zu verknüpften versuchen, durch das Vorhandensein in der gemeinsamen Grammatik, bekannt sind, was uns das Resolven erleichtert hätte. Die Grammatik sah dabei wie folgt aus

```
// Namen der Sprachen dienen nur der Veranschaulichung
grammar MyCompGrammar extends de.monticore.lang.t4v, de.monticore.lang.sysmlv2 {

}
```

Dieser Ansatz hat allerdings einige Nachteile: Der gravierndste und für uns ausschlaggebende Nachteil, der dazu geführt hat, diese Idee nicht weiter zu verfolgen, war der, dass die bereits vorhandenen Language-Server beider Sprachen nicht wiederverwendet werden konnten. Dies hätte zur Folge, dass das gesamte Vorgehen nicht wiederverwendbar ist, für jede Komposition müsste also in Kleinarbeit erst eine Sprache, dann der Language-Server und schlussendlich die restliche Logik implementiert werden. Es gab außerdem einige technische Probleme, die wir der Dokumentation wegen auch nennen wollen. Zum einen war durch Inkompatibilitäten der Gradle-Versionen die Umstellung knifflig. Wir mussten sowohl Java wie Gradle downgraden und auf den Eclipse-Compiler umstellen, um die Sprache zu kompilieren. Weiter waren die Einbindung in Gradle durch Dependencies mit Repositories eine Sysisphus-Aufgabe, denn die Grammars hatten unterschiedliche Versionen verschiedener Sprachen als Dependencies, welches manuelle Intervention forderte und den Prozess müßig laufen lies. Nichtsdestotrotz kann diese Herangehensweise, auch wenn sie statisch ist, bei der Einbindung in die eingangs erwähnten Teile von SysML2 funktionieren. Wenn zum Beispiel die `requirement def` statt normalem Text ein T4V-Element enthält, kann durch das Aufstellen des ASTs dort die Interaktion klappen. Wir hingegen hielten diesen Ansatz für nicht zielführend, weshalb wir uns schlussendlich für eine andere Herangehensweise entschieden.

### Herangehensweise B: Aggregierte Language-Server
Statt die Sprachen zu aggregieren, entschieden wir uns, die Language-Server zu aggregieren. Dies hatte den Vorteil, dass durch die Existenz der Language-Server kein weiterer Aufwand (neben der Aggregation selbst) entstand, wie etwa das Erstellen einer neuen Sprache. Weiter ist der Ansatz deutlich flexibler, da es so möglich ist, je zwei beliebige Language-Server zu verbinden. Es muss jedoch immer die Resolve-Logik implementiert werden, was allerdings im Vergleich zu dem vorherigen Ansatz deutlich weniger Aufwand ist. Ein Beispiel zu der Aggregation der Language-Server kann [hier](https://git.rwth-aachen.de/monticore/tools/lsp-generator/-/tree/master/03.Examples/LanguageAggregation) gefunden werden. Das T4V-Projekt kann [hier](https://git.rwth-aachen.de/montibelle/frontend/language/text4verification) gefunden werden. Dokumentation zu den verschiedenen Teilbereichen und Gedankengängen vor, während und nach dem Projekt sind [hier](https://git.rwth-aachen.de/montibelle/frontend/language/text4verification/-/tree/master/doc) gesammelt.