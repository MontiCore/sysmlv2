<!-- (c) https://github.com/MontiCore/monticore -->
# For Users

## Tool Download and Use

* [**Download SysML v2 Tool**](https://www.monticore.de/download/MCSysMLv2.jar)

The SysML v2 tool offers options for processing SysML v2 models. It provides through the CLI as follows:

`java -jar MCSysMLv2.jar [-h] -i &lt;fileName&gt; [-path &lt;p&gt;] [-pp [&lt;file&gt;]] [-s [&lt;file&gt;]]`

where the arguments are:

| Option                   | Explanation                                                                        |
|--------------------------|------------------------------------------------------------------------------------|
| -ex,--extended           | Runs additional checks assuring the semantic soundness of models according to Spes |
| -h,--help                | Prints this help dialog                                                            |
| -i,--input &lt;file&gt;        | Reads the source file (mandatory) and parses the contents                    |
| -path &lt;arg&gt;              | Sets the artifact path for imported symbols, space separated.                |
| -pp,--prettyprint &lt;file&gt; | Prints the AST to stdout or the specified file (optional)                    |
| -r,--report &lt;dir&gt;        | Prints reports of the artifact to the specified directory.                   |
| -s,--symboltable &lt;file&gt;  | Serialized the Symbol table of the given artifact.                           |
| -v,--version             | Prints version information                                                         |

exemplary usage:

```bash
java -jar MCSysMLv2.jar -h
java -jar MCSysMLv2.jar -i Car.sysml -pp
```

## Prerequisites

To run the tool, it is required to install a Java 21 JRE.
