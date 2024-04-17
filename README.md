<!-- (c) https://github.com/MontiCore/monticore -->

# SysML v2 Parser defined using MontiCore

[SysML v2](https://www.omgsysml.org/SysML-2.htm) is about to finish its
standardization process. Compared to its previous version, it has a
lot of new capabilities to describe behavior, structure, interactions,
and other relevant aspects of systems.
[Here](https://www.omgsysml.org/index.htm) a detailed description of
the SysML language and capabilities can be found.

One of the interesting new capabilities is the exchange of models
between tools using a really human readable textual form of the SysML
language in the spirit of a modern programming language (even though it
has a number of special constructs that resemble modelling concepts).

```
standard library package 'Vehicles' {
  import ISQ::TorqueValue;
  import ScalarValues::*;
  part def Automobile;
  alias Car for Automobile;
  alias Torque for ISQ::TorqueValue;
}
```

This textual form will play a major role in the exchange of models
between tools thus allowing to build toolchains, as well as in the
versioning of models, e.g., in Github, and also in the efficient
definition of models by people who prepare textual notations.

It is therefore highly relevant to have consistent parsing mechanisms
available. The [SysML v2
Pilot-Implementation](https://github.com/Systems-Modeling/SysML-v2-Pilot-Implementation)
contains a parser for this textual notation.

We know from the definition of programming languages, that it is,
however, helpful to provide a second source parser, such that parsing
results can be compared and therefore compilers, linters, checkers of
context conditions and other advanced tooling, receive the level of
quality desired for industrial use.

## Capabilities of this Parser for SysML v2

Because MontiCore provides modern parsing technology we have therefore
implemented a SysML v2 parser based on MontiCore. It is available in
this repository under the [MontiCore relaxed
licensing}(https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
(Level-2 is based on BSD-3-Clause).

This parser provides a number of advantages

1. it uses modular language composition (i.e. it is itself defined using
   a number of pre-existing language components, whose quality and functionality
   already has been hardened).
1. the parser comes with lots of infrastructure for defining
   and exchanging symbols on model level, because SysML introduces
   quite a number of new kinds of symbols, e.g. for states, actions, parts,
   etc.
1. more infrastructure exists to manage well-formedness conditions,
   even complex ones, to detect errors, incompleteness, inconsistencies, etc.

Especially the symbol management infrastructure, which was carried over
by the MontiCore development team from compiler technologies to the new
kinds of symbols a modern modelling language typically has, provides
helpful advantages. This allows to decouple the symbol management in
the models from the mapping of these symbols to code, which might be
different, dependent on the technology setting. For example a state
`Off` might become an enum constant `MyAutomaton.Off`, an integer value
`int Off=7`, a subclass `StateOff` in the state pattern, or map to a
method API like `isOff()`, `setOff()`. MontiCore's symbol management
takes care about `Off` as state and checks consistency already on model
level.

## Future

This project follows several purposes: One main purpose is to provide a
second SysML v2 parser
for comparison and potential quality check with the
parser given in the [SysML v2
Pilot-Implementation](https://github.com/Systems-Modeling/SysML-v2-Pilot-Implementation).

Furthermore, we are interested to extend and adapt

* the frontend, i.e., the language capabilities,
* the analytical capabilities, i.e., advanced context conditions,
  consistency and completeness checks, up to high-level verification
  capabilities using frormal methods, and
* the generative backend, i.e., the capabilities to produce
  simulations, and real code.

For this purposes the MontiCore infrastructure is very helpful, as it
enables modularity in several dimensions: Modular enhancement of the
SysML v2 language using conservative extension. Conservative extension
as defined in the [MontiCore
handbook](https://monticore.de/handbook.pdf) allows to extend the
capabilities of a language without invalidating (1) existing models and
(2) generative and an analytical functionality that has only been
defined for the older language. This is why context conditions and
generation functionalities can also be defined in a modular way
(using various design patterns) being rather easily
extendable.

## Project Structure

This project currently provides

* the parser,
* a pretty printing facility, and
* produces the symbol table of the given artifact.

The context conditions are definitely not yet complete, because to some
extent the context conditions have still to be refined. Especially the
correct use of expressions and their typing, which is one of the larger
parts of context conditions in programming languages, will be
something, we will further explore.

* [**language**](language) contains the sources to the language
  implementation, including the parser
* [**language-server**](language-server) A [language server][3]
  implementation for SysML v2 in MontiCore, generated via the MontiCore Language
  Server Generator (MCLSG). Enables the following editor features:
  - Syntax highlighting

  ![](doc/highlighting.png)
  - In-place error reporting

  ![](doc/errors.png)
  - In-place suggestions for auto-completion and error-correction

  ![](doc/completion.png)
* [**visualization**](visualization) The [official pilot implementation for
  visualization][2] wrapped as a gradle project
* [**visualization-plugin**](visualization-plugin) The official pilot implementation for
  visualization wrapped in a VSCode plugin

Tests for the parser contain exemplary SysML v2 models from the [SysML
Submission Team (SST)](https://github.com/Systems-Modeling):
* [Systems Library](language/src/main/resources/Systems%20Library)
* [Domain Libraries](language/src/main/resources/Domain%20Libraries)
* [Example, Training, and Validation Models](language/src/test/resources/official)

All of these examples can be parsed by both parsers. We welcome the submission
of further examples for quality checks.

## Tool Download and Use

* [**Download SysML v2 Tool**](http://www.monticore.de/download/MCSysMLv2.jar)

The [SysML v2 tool](bin/MCSysMLv2.jar) offers options for processing SysML v2
models. It provides through the CLI as follows:

`java -jar MCSysMLv2.jar [-h] -i <fileName> [-path <p>] [-pp [<file>]] [-s [<file>]]`

where the arguments are:

| Option                   | Explanation                                                                                |
|--------------------------|--------------------------------------------------------------------------------------------|
| -ex,--extended           | Runs additional checks assuring models are fit for semantic analysis using [MontiBelle][1] |
| -h,--help                | Prints this help dialog                                                                    |
| -i,--input <file>        | Reads the source file (mandatory) and parses the contents                                  |
| -path <arg>              | Sets the artifact path for imported symbols, space separated.                              |
| -pp,--prettyprint <file> | Prints the AST to stdout or the specified file (optional)                                  |
| -r,--report <dir>        | Prints reports of the artifact to the specified directory.                                 |
| -s,--symboltable <file>  | Serialized the Symbol table of the given artifact.                                         |
| -v,--version             | Prints version information                                                                 |

exemplary usage:

```
java -jar MCSysMLv2.jar -h
java -jar MCSysMLv2.jar -i Car.sysml -pp
```

A `code generation` and `advanced consistency checks` are currently in
work, but the not yet available.

An update of the embedding of the complete parser into the [SPES
Systems Engineering Methodology](https://spesml.github.io/index.html/),
which acts as a plug-in for Cameo / MagicDraw is currently also
planned.

##### Prerequisites

To run the tool, it is required to install a Java 11 JRE.

## Building the Tool from the Sources (if desired)

As alternative to a download, it is possible to build an executable JAR of the
tool from the source files located in GitHub. In order to build an executable
Jar of the tool with Bash from the source files available in GitHub, execute the
following commands.

First, clone the repository:
```
git clone https://github.com/MontiCore/sysmlv2.git
```

Change the directory to the root directory of the cloned sources:
```
cd sysmlv2
```

Then build the source files with gradle. To this effect, execute the following
two command:

```
./gradlew build
```

Congratulations! The executable JAR file MCCD.jar is now in the directory
language/target/libs.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of MontiCore languages
  **](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library
  **](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [MontiCore' Language Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

[1]: https://www.se-rwth.de/projects/#MontiBelle
[2]: https://github.com/Systems-Modeling/SysML-v2-Pilot-Implementation/tree/master/org.omg.sysml.interactive
[3]: https://microsoft.github.io/language-server-protocol/
