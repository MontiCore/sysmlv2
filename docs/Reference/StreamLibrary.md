# Stream Library

The Stream Library provides standard components and definitions for working with streams in SpesML.
Here you can find [stream expressions](https://monticore.github.io/monticore/monticore-grammar/src/main/grammars/de/monticore/Grammars/#streamexpressionsmc4-stable)
and the [stream method library](https://github.com/MontiCore/monticore/tree/dev/monticore-libraries/stream-symbols/src/main/symtabdefinition).
The modeling with SpesML v2 works seamlessly with the MontiCore parser.

## Specifying Timing
Timing can be specified in multiple ways using user defined keywords:

1. At the interface level over ports:

```#event port x;```

2. On the behavioral level:

```#sync satisfy requirement {...}```

!!! warning
    Note that 1. & 2. are **mutually** exclusive.
