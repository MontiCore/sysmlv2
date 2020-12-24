<!-- (c) https://github.com/MontiCore/monticore -->
# SysML v2
The MontiCore language family for SysML v2  contains the grammars 
  and symbol management infrastructure for parsing and processing SysML models.
  Examples for two of the languages:

```
package 'Vehicles' {        // a SysML v2 block diagram
  private import ScalarValues::*; 
  block Vehicle; 
  block Truck is Vehicle; 
  value type Torque is ISQ::TorqueValue; 
}
```
```
package 'Coffee' {          // a SysML v2 activity diagram
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

* The language family includes reused
 and 33 new grammars. Some of the finalizing grammars are:
- [`AD`][ADGrammar]: Language definition for SysML Activity Diagrams
- [`BDD`][BDDGrammar]: Language definition for SysML Block Definition Diagrams
- [`IBD`][IBDGrammar]: Language definition for SysML Internal Block Diagrams
- [`PackageDiagram`][PackageDiagramGrammar]: Language definition for SysML Package Diagrams
- [`ParametricDiagram`][ParametricDiagramGrammar]: Language definition for SysML Parametric Diagrams
- [`RequirementDiagram`][RequirementDiagramGrammar]: Language definition for SysML Requirement Diagrams
- [`STM`][STMGrammar]: Language definition for SysML State Machine Diagrams
- [`SysMLBasics`][SysMLBasicsGrammar]: Language definition building a basis for the SysML v2 language family
- [`SysMLCommon`][SysMLCommon]: Language definition for inheriting common concepts of SysML, such as Generalization
- [`SysML`][SysML]: Language definition for composing the resulting SysML v2 language
- The official version does not yet support sequence and use case diagrams yet.

* The main purpose of this language is parsing general artifacts in SysML v2 
  format that adhere to the upcoming standard.
  Caution: As long as the upcoming standard changes, we will adapt the 
  textual language definitions accordingly (until there will be a finalization).
* The SysML v2 grammars enables parsing arbitrary SysML v2 artifacts for further 
  processing. 
  Actually these grammars represents a slight superset to the official SysML 2 
  standard. It is intended for parsing SysML 2-compliant models. 
  
## Renamed NTs from the official implementation
 
 (Xtext->MC)
- Import -> ImportUnit
- All NTs which are now interfaces in MC must be implemented. For the standard implementation these NTs
  get suffix "std".
- Name -> SysMLName  

## Helpful methods
If searching for the implementation of an interface `x` often it is helpful to search
for "`x =`" in the grammar directory. If an NT does only implement one interface it will always have
the form "MyNT implements `x =`".

## Further Information
* [MontiCore documentation](http://www.monticore.de/)


[ADGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/AD.mc4
[BDDGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/BDD.mc4
[IBDGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/IBD.mc4
[PackageDiagramGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/PackageDiagram.mc4
[ParametricDiagramGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/ParametricDiagram.mc4
[RequirementDiagramGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/RequirementDiagram.mc4
[STMGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/SMD.mc4
[SysMLBasicsGrammar]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/basics/sysmldefault/SysMLBasics.mc4
[SysMLCommon]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/common/SysMLCommon.mc4
[SysML]: https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/SysML.mc4

