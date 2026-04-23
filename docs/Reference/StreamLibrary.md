# Stream Library & Writing Stream Specifications

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

The default timing is `event`.

Stream specifications contain properties written over the input and output of components' ports.

## Components
Components use standard syntax and must declare its ports (packages beind optional):
```
part def A {
  port input:   PortType1;
  port output: ~PortType1;

  ...
}
```
## Ports
Port types must exist and feature at least one attribute:
```
port def PortType1 {
  attribute value: int;
}
```
Attribute types have to be standardized java types (int, double, float, short, long, boolean, byte,
 char, String), nat or user defined:
```
attribute def UserDefinedType1 {
  attribute field1: int;
}
```

```
enum def UserDefinedEnum1 {
  enum E1;
  enum E2;
}
```

## Stream Specifications
Stream specifications can be expressed in SysML v2 as asserted constraints if there are no assumptions about the environment.
```
assert constraint c1 {
  input.value == <1,2>
}
```
The stream of messages over a port can be recalled by field accesses over the port usage attribute. When accessed in specifications these field accesses have an
implicit stream type. For example `input.value` in component `A` has the type `EventStream<int>`.
In the case of port definitions with singular attributes we provide syntactic sugar by writing `output` instead of `output.value`
as it is unambiguous that a stream access is.
This is especially useful when chaining methods over streams.

More complex specifications can be written as assumption/guarantee pairs through requirements. Multiple assumptions and multiple guarantees are allowed.
Each requirement corresponds to a property.
```
satisfy requirement r1 {
  assume constraint c2 {
    input.value == <1>
  }
  require constraint c3 {
    output.value == input.value
  }
}
```

## Parameters
Stream specifications can also be parametrized with the parameter being set at initialization of the part.
```
part A {
  ...

  final attribute maxValue: int;

  assert constraint c4 {
    input.value.first() >= maxValue;
  }
}
```

## Methods
The stream method library and all stream expressions can be used inside specifications:
```
output.value.length() == (input.value()^^<2>).length()
```

## OCLExpressions
It is usual to use first-order logic expressions in specifications. These can be embedded through OCLExpressions (which also include SetExpressions):
```
forall t in nat:
  !input.value.nth(t).isEmpty() || input.values.nth(t) isIn {0}
```
