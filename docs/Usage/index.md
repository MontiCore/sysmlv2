---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Usage

`java -jar MCSysMLv2.jar [-h] -i <fileName> [-path <p>] [-pp [<file>]] [-s [<file>]]`

| Option                   | Explanation                                                                                |
|--------------------------|--------------------------------------------------------------------------------------------|
| -ex,--extended           | Runs additional checks assuring models are fit for semantic analysis using [MontiBelle][https://www.se-rwth.de/projects/#MontiBelle] |
| -h,--help                | Prints this help dialog                                                                    |
| -i,--input <file>        | Reads the source file (mandatory) and parses the contents                                  |
| -path <arg>              | Sets the artifact path for imported symbols, space separated.                              |
| -pp,--prettyprint <file> | Prints the AST to stdout or the specified file (optional)                                  |
| -r,--report <dir>        | Prints reports of the artifact to the specified directory.                                 |
| -s,--symboltable <file>  | Serialized the Symbol table of the given artifact.                                         |
| -v,--version             | Prints version information                                                                 |