# Troubleshooting

## Support

If you encounter any issues or have questions, feel free to reach out.

- **GitHub Issues:** [Report a bug or request a feature](https://github.com/MontiCore/sysmlv2/issues)
- **Email:** [marin@se-rwth.de](mailto:marin@se-rwth.de)

## Error Codes

Below is a list of error codes you might encounter while modeling using the CLI, along with explanations and suggestions on how to resolve them in your SysML models.

### 0x10... Model Structure & Resolution
#### **0x10005**,**0x10032**
##### Explanation
A cardinality declaration is incomplete or empty.

##### Fix {: .skip-from-toc }
Ensure your cardinalities conform to the format `[n..m]`,or `[m]` where `n` is non-negative and `m` is non-negative with `n <= m` or `*` .

#### **0x10030**, **0x10031**, **0x10033**
##### Explanation
We deem cardinality ranges like `[*]`, `[n..*]`, or `[n..m]` problematic.

##### Fix {: .skip-from-toc }
We recommend using exact scalar cardinalities (e.g., `[1]`).

#### **0x10009**, **0x10010**, **0x10012**, **0x10AA3**, **0x10AA4**
##### Explanation
Unresolved part usage, part definition, or port usage in a decomposed part def.
##### Example
```sysml
...
part def A {
  connect somePart1.somePort to ...
}

```
##### Fix {: .skip-from-toc }
Check the names used in your `connect` statements to ensure the parts and ports exist and are spelled correctly. `somePart1` has to be declared as a part usage with a type,
it has to have a part def and `somePort` has to be declared in the definition of the part usage.

#### **0x10017**, **0x10020**
##### Explanation
Unresolved Action definition or usage.
##### Example
```sysml
action def A1: A {
  ...
}
action a: A2;
```
##### Fix {: .skip-from-toc }
Verify the spelling of the action and ensure it is defined in the current scope or imported. `A` and `A2` here were not defined anywhere in scope.
Please note that `A` must be an action def and `A2` can be both an action def and a usage.

#### **0x10018**
##### Explanation
Specializations of port usages must be port usages or definitions.

##### Fix {: .skip-from-toc }
Ensure the type assigned to your port is a valid port definition (see **0x10017**).

#### **0x10021**, **0x10022**
##### Explanation
Unresolved Interface definition or usage.

##### Fix {: .skip-from-toc }
Verify the spelling and existence of the referenced interface (see **0x10017**).

#### **0x10023**, **0x10024**
##### Explanation
Unresolved Item definition or usage.

##### Fix {: .skip-from-toc }
Verify the spelling and existence of the referenced item (see **0x10017**).

#### **0x10025**, **0x10026**, **0x10027**, **0x10028**, **0x10AA1**
##### Explanation
Unresolved Part or Port definition/usage.

##### Fix {: .skip-from-toc }
Ensure that the parts and ports you are trying to specialize or type actually exist in the model (see **0x10017**).

#### **0x10029**, **0x10030**
##### Explanation
Source or target state in a transition is not defined.
##### Example
```sysml
state S;
transition first S then T;
```
##### Fix {: .skip-from-toc }
Verify that the `first` and `then` states `S` and `T` are declared in scope. In this case `S` is declared but `T` cannot be resolved.

#### **0x10034**, **0x10035**
##### Explanation
Unresolved State definition.

##### Fix {: .skip-from-toc }
Check the spelling of state definitions referenced in your models (see **0x10017**).

#### **0x10AA2**
##### Explanation
Part def used in `refines` specializations does not exist (SpesML specific CoCo).

##### Fix {: .skip-from-toc }
Check the spelling of the part definition you are trying to refine (see **0x10017**).

#### **0x10AA5**, **0x10AA6**
##### Explanation
Illegal connection of (subcomponent) ports.
##### Example
```sysml
port def SomePort {
  in attribute b: Boolean;
}
part def SomePart {
  port i:  SomePort;
  port o: ~SomePort;
}
part def A {
  port i1:  SomePort;
  port o1: ~SomePort;

  part sub:  SomePart;
  part sub1: SomePart;

  connect i1 to sub.i;  // 1
  connect sub.o to o1;  // 2
  connect i1 to sub.o1; // 3

  connect sub.o -> sub1.i; // 4
  connect sub.i -> sub1.o; // 5
}
```
##### Fix {: .skip-from-toc }
We consider valid two types of simple connections: connecting subcomponent IO to other subcomponent IO of the opposite direction;
connecting IO of subcomponents to the IO of the parent component with the same directionm, aka forwarding.
Connections 1 and 2 are legal forwards. 3 is not. Connection 4 is valid, while 5 is not.

#### **0x10AA7**
##### Explanation
Subcomponent name is not unique.
##### Fix {: .skip-from-toc }
Give each part usage within a block a unique name.

### 0x80... and 0x81... Expressions & Type Checking
#### **0x80001**, **0x80002**
##### Explanation
Failed to derive a type for an expression in constraints.
##### Example
```sysml
port def SomePort {
  in attribute b: Boolean;
}
part def A {
  port i:  SomeBooleanIn;
  port o: ~SomeBooleanIn;

  constraint c {
    i.b > o.b
  }
}
```

##### Fix {: .skip-from-toc }
Check your expression for syntax errors or unresolved symbols. Here, the expressions on the left and right are not of type integer, which `>` requires.

#### **0x80004**, **0x80005**
##### Explanation
Expression type should be boolean.
##### Example
```sysml
port def SomePort {
  in attribute b: Integer;
}
part def A {
  port i:  SomeBooleanIn;

  exhibit state s {
    transition
    first S
    if i.b
    then S;
  }
}
```

##### Fix {: .skip-from-toc }
Ensure your constraint or transition guard evaluates to a true/false condition. Here the value of the guard is of type Integer.

#### **0x81001**
##### Explanation
You are using a stream where a scalar value is expected.
##### Example
```sysml
port def SomePort {
  in attribute b: Boolean;
}
part def A {
  port i:  SomeBooleanIn;
  port o: ~SomeBooleanIn;

  constraint c {
    i.b.first();
  }

  exhibit state s {
    transition
    first S
    if i.b.first()
    then S;
  }
}
```

##### Fix {: .skip-from-toc }
in constraints access of a port evaluates stream based, while in transitions it evaluates to the explicit type.
The expression in `c` is valid as `i.b` evaluates to `EventStream<Boolean>`, while in `s` to `Boolean`,
making the guard invalid. See [SpesML](../SpesML/index.md).

### 0x90... Specializations & Refinements
#### **0x90030**, **0x90031**
##### Explanation
Cyclic specialization detected (e.g., `part def A specializes B;`, and `part def B specializes A;`).

##### Fix {: .skip-from-toc }
Specialization must be acyclic. Remove circular specialization relationships. This includes trivial specializations.

### 0xD... Serialization Errors
#### **0xD0001**, **0xD0100**, **0xD0101**
##### Explanation
Symbol table serialization errors.

##### Fix {: .skip-from-toc }
This is an internal tool error usually caused by trying to serialize a construct that should not be serialized
or of unknown timing. Ensure your model conforms strictly to supported SysML constructs.

### 0xPA... Packaging Internal Errors
#### **0xPA090**, **0xPA091**, **0xPA092**
##### Explanation
Missing MontiCore specific standard library definitions. The tool failed to find or
load the `Stream.symtabdefinition` from its resources.

##### Fix {: .skip-from-toc }
 Ensure all MontiCore Gradle dependencies are loaded.

### 0xFF... Modeling Guidelines Errors
#### **0xFF003**, **0xFF004**
##### Explanation
We do not recommend do actions in states.

##### Fix {: .skip-from-toc }
Use `entry`, `exit`, or external transitions instead.

#### **0xFF005**
##### Explanation
Name contains not recommended characters.

##### Fix {: .skip-from-toc }
For use in validation and verification tools, we recommend simple names.
Rename the element using only letters, numbers, and underscores. Ensure it does not start with a number.

#### **0xFF006**
##### Explanation
Port attribute does not have a type.

##### Fix {: .skip-from-toc }
Ensure every attribute within a port definition has at least one type defined via `:` or `defined by`.

#### **0xFF0001**
##### Explanation
Discouraged use of flow connections.

##### Fix {: .skip-from-toc }
Use standard `connect` or `connection` statements instead of `flow`.

#### **0xFF0002**
##### Explanation
Part should use at least one port.

##### Fix {: .skip-from-toc }
A well-modeled part should communicate with its environment. Add a port to your part definition.

#### **0xFF0003**
##### Explanation
Part has no explicit behavior.

##### Fix {: .skip-from-toc }
Add a constraint, a state machine, or subparts to define the behavior of the part.

#### **0xFF0004**
##### Explanation
Part has conflicting behaviors.

##### Fix {: .skip-from-toc }
Ensure a part uses exactly one type of behavior specification (constraint, state machine, or composition), not a mixture of them.

!!! info
    All other error codes describe internal errors and should be described through [issues](https://github.com/MontiCore/sysmlv2/issues) for assistance.

