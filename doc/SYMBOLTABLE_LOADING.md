# (De)Serialization of Symboltables with Identical ArtifactScope Names

## Problem

In the case of serializing two models with identical package names, i.e. `package MyPackage`
(allowed explicitly in SysML), the symbol storing mechanism can be provided a path to the target `.sym` file. In the case
of loading these back, the resolution mechanism looks for `MyPackage.sym` in the symbol path when it does not find
a matching symbol (i.e. `MyPackage.someSymbol`). Because we can store symbols to arbitrary file names this leads to
a failure for the default case where we name the file `MyPackage.sym`.

## Naive Solutions:

Introduce a naming scheme for storing artifactscopes with identical names, such as `MyPackage$index.sym`. Then loading
can take place partially - load STs until symbol is resolved - or completely - load all STs with specified package prefix.
Use a folder structure where all children are named with an index and apply a similar loading mechanism if special characters
are deemed dangerous in Monticore.

### Downsides and Interrelation with Imports
TODO
