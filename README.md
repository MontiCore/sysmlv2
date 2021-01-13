<!-- (c) https://github.com/MontiCore/monticore -->
## SysML v2 Official

Project for the SysML v2 language family in MontiCore. It is compatible with the general standard.
The language is a strict compatible superset of the SysML v2 syntax. 

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

``mvn clean install `` : Builds and tests everything.

``mvn install -DskipTests`` : Only builds the project.

``mvn install -Pdev-skip-slow-tests``:  Activates profile which skips slow tests, but should be used carefully, since
 this command skips the SysMLToolTest, which combines all software components. 
 This testing is especially useful when developing the parser.


 ## SysMLTool
 
 We provide the class `SysMLTool` to use our implementation of SysML v2. The main method takes the following parameters:
 - **Model Path** (_required_): The first argument should be a path to the directory containing the models.
 - **Deactivate CoCos** (_optional_): The argument `-cocosOff` disables CoCos.
 - **Library Directories** (_optional_): We can add directories containing models, which serve as a library, with the command `-lib=` followed by the directory path. It is possible to add multiple library directories.
  
 We parse all models in the given directories and build the symbol table. 
 Then we check the context conditions on the models in the model directory. 
 If we want to check a library for well-formedness, we pass the library as the first directory.
  Because SysML models provide a basis for code generation or complex analysis, the `SysMLTool` offers the method
   `mainForJava`, which returns a list of the parsed models in the model directory. 
   Further tools can process and transform the returned models. This method takes the same arguments as the main method.
   
 ## Architecture 
 ![Package Structure](architecturedoc/ArcPackageStructure.png)
 
 ## Grammars - SysMLBasics Package
 ![SysMLCommon](architecturedoc/basicsDefault.png)
   
 ## Grammars - SysMLCommon Package
 ![SysMLCommon](architecturedoc/common.png)
 
 ## Grammars - Composing SysML v2
 ![SysMLLangage](architecturedoc/SysMLAndAdvanced.png) 
 
 ## Context Conditions - Warnings instead of Errors
 We fully support the official SysML  v2 models but currently do not support importing KerML models and some of the
  implemented CoCos restrict the official syntax. 
 Thus, CoCos for resolving importing models (possible KerML models) and CoCos, which restrict the official syntax 
  only emit warnings and not errors. 
 This project builds on the 2020-03 
  [official pilot implementation](http://openmbee.org/sysml-v2-release/2020-03)
  of SysML v2, but is realized with a new architecture,
  an improved abstract syntax and new functionality, such as the additional CoCos. 
  Since SysML v2 is currently in development, this MontiCore
   project is not stable and will be updated in the future according to the future releases.
  
 ## Further Information
 
 * [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
 * [MontiCore documentation](http://www.monticore.de/)
 * [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
 * [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
 * [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
 * [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
 * [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
