<!-- (c) https://github.com/MontiCore/monticore -->

# SysML v2 in MontiCore

## Project Structure
* [**bin**](bin) contains the executable parser
* [**examples**](examples) contains exemplary SysML v2 models from the 
[SysML Submission Team (SST)](https://github.com/Systems-Modeling)

## Tool Download
* [**Download SysML v2 Tool**](http://www.monticore.de/download/MCSysMLv2.jar)

Alternatively, the tool can be found in the `bin`-folder.

##### Prerequisites
To run the tool, it is required to install a Java 11 JRE.

## Tool Parameters
The [SysML v2 tool](bin/MCSysMLv2.jar) offers options for processing SysML v2
models. It provides through the CLI as follows:

`java -jar MCSysMLv2.jar [-h] -i <fileName> [-path <p>] [-pp [<file>]] [-s [<file>]]`

where the arguments are:

| Option                            | Explanation |
| ------                            | ------ |
| -ex,--extended           | Runs additional checks not pertaining to the official language specification |
| -h,--help                | Prints this help dialog
| -i,--input <file>        | Reads the source file (mandatory) and parses the contents
| -path <arg>              | Sets the artifact path for imported symbols, space separated.
| -pp,--prettyprint <file> | Prints the AST to stdout or the specified file (optional)
| -r,--report <dir>        | Prints reports of the artifact to the specified directory.
| -s,--symboltable <file>  | Serialized the Symbol table of the given artifact.
| -v,--version             | Prints version information

exemplary usage:

```
  java -jar MCSysMLv2.jar -h
  java -jar MCSysMLv2.jar -i Car.sysml -pp
``` 

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
