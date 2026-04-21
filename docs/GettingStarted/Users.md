<!-- (c) https://github.com/MontiCore/monticore -->
# For Users

## Tool Download and Use

* [**Download SysML v2 Tool**](https://www.monticore.de/download/MCSysMLv2.jar)

The SysML v2 tool offers options for processing SysML v2 models. It provides through the CLI as follows:

`java -jar MCSysMLv2.jar [-h] -i <fileName> [-path <p>] [-pp [<file>]] [-s [<file>]]`

where the arguments are:

| Option                   | Explanation                                                                                |
|--------------------------|--------------------------------------------------------------------------------------------|
| -ex,--extended           | Runs additional checks assuring models are fit for semantic analysis using [MontiBelle](https://www.se-rwth.de/projects/#MontiBelle) |
| -h,--help                | Prints this help dialog                                                                    |
| -i,--input <file>        | Reads the source file (mandatory) and parses the contents                                  |
| -path <arg>              | Sets the artifact path for imported symbols, space separated.                              |
| -pp,--prettyprint <file> | Prints the AST to stdout or the specified file (optional)                                  |
| -r,--report <dir>        | Prints reports of the artifact to the specified directory.                                 |
| -s,--symboltable <file>  | Serialized the Symbol table of the given artifact.                                         |
| -v,--version             | Prints version information                                                                 |

exemplary usage:

```bash
java -jar MCSysMLv2.jar -h
java -jar MCSysMLv2.jar -i Car.sysml -pp
```

##### Prerequisites

To run the tool, it is required to install a Java 21 JRE.
