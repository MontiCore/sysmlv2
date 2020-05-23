<!-- (c) https://github.com/MontiCore/monticore -->
# SysML2
* The MontiCore language family for the SysML 2  contains the grammars 
  and symbol management infrastructure for parsing and processing SysML models
  An example:

```
package 'Vehicles' { 
  private import ScalarValues::*; 
  block Vehicle; 
  block Truck is Vehicle; 
  value type Torque is ISQ::TorqueValue; 
}
```
* The language family comprises the following grammars:
- [`AD`][ADGrammar]: Language definition for SysML Activity Diagrams
- [`BDD`][BDDGrammar]: Language definition for SysML Block Definition Diagrams
- [`IBD`][IBDGrammar]: Language definition for SysML Internal Block Diagrams
- [`PackageDiagram`][PackageDiagramGrammar]: Language definition for SysML Package Diagrams
- [`ParametricDiagram`][ParametricDiagramGrammar]: Language definition for SysML Parametric Diagrams
- [`RequirementDiagram`][RequirementDiagramGrammar]: Language definition for SysML Requirement Diagrams
- [`SD`][SDGrammar]: Language definition for SysML Sequence Diagrams
- [`SMD`][SMDGrammar]: Language definition for SysML State Machine Diagrams
- [`SysMLBasics`][SysMLBasicsGrammar]: Language definition for a common basis of all SysML diagrams
- [`UseCaseDiagram`][UseCaseDiagramGrammar]: Language definition for SysML Use Case Diagrams


  

[ADGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/AD.mc4
[BDDGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/BDD.mc4
[IBDGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/IBD.mc4
[PackageDiagramGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/PackageDiagram.mc4
[ParametricDiagramGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/ParametricDiagram.mc4
[RequirementDiagramGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/RequirementDiagram.mc4
[SDGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/SD.mc4
[SMDGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/SMD.mc4
[SysMLBasicsGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/SysMLBasics.mc4
[UseCaseDiagramGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/UseCaseDiagram.mc4
[SysML2Grammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/tree/master/src/main/grammars/de/monticore/lang/sysml/legacy

