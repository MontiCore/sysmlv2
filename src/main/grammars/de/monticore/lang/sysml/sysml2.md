<!-- (c) https://github.com/MontiCore/monticore -->
# SysML2
* The MontiCore language family for the SysML 2  contains the grammars 
  and symbol management infrastructure for parsing and processing SysML models.
  Examples for two of the languages:

```
package 'Vehicles' {                        // a SysML block diagram
  private import ScalarValues::*; 
  block Vehicle; 
  block Truck is Vehicle; 
  value type Torque is ISQ::TorqueValue; 
}
```
```
package 'Coffee' {                         // a SysML activity diagram
  activity BrewCoffee (in beans : CoffeeBeans, in, water : Water, out coffee : Coffee) { 
    bind grind::beans = beans;
    action grind : Grind (in beans, out powder);
    flow grind::powder to brew::powder;
    bind brew::water = water;
    action brew : Brew (in powder, in water, out coffee); 
    bind brew::coffee = coffee;
  }
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

* The main purpose of this language is parsing general artifacts in SysML 2 
  format that adhere to the upcoming standard.
  Caution: As long as the upcoming standard changes, we will adapt the 
  textual language definitions accordingly (until there will be a finalization).
* The SysML 2 grammars enables parsing arbitrary SysML 2 artifacts for further 
  processing. 
  Actually these grammars represents a slight superset to the official SysML 2 
  standard. It is intended for parsing SysML 2-compliant models. Therefore, 
  well-formedness checks are kept to a minimum, because we assume to parse 
  correctly produced SysML 2 models only. 

## Symboltable
* The SysML 2  artifacts provide symbols of different, yet to be 
  precisely kinds. 
* Symbol management:
  * SysML 2 artifacts provide a hierarchy of scopes along the objects they 
    define.
  * Symbols are often *externally visible* and *exported*. 
  * Details to be defined.

### Symbol kinds used by SysML 2 (importable):
* TBD: Currently none. However this may change when applying future use cases.

### Symbol kinds defined by SysML 2:
* TBD: Symbol kinds are currently explored.

### Symbols exported by SysML 2:
* TBD: Symbols defined by SysML2 models are often *externally visible* and 
  *exported*. Details will follow.

## Functionality: CoCos
* currently none; it is assumed that the SysML 2 models were produced correctly.

## Further Information
* [MontiCore documentation](http://www.monticore.de/)


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

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

