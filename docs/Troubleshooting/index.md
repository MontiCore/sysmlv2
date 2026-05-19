# Troubleshooting

## Support

If you encounter any issues or have questions, feel free to reach out.

- **GitHub Issues:** [Report a bug or request a feature](https://github.com/MontiCore/sysmlv2/issues)
- **Email:** [marin@se-rwth.de](mailto:marin@se-rwth.de)

## Error Codes

Below is a list of error codes you might encounter while modeling using the CLI, along with explanations and suggestions on how to resolve them in your SysML models.

### 0x10... Model Structure & Resolution
**0x10005**,**0x10032**: A cardinality declaration is incomplete or empty.

**Fix:** Ensure your cardinalities conform to the format `[n..m]`,or `[m]` where `n` is non-negative and `m` is non-negative with `n <= m` or `*` .

**0x10030**, **0x10031**, **0x10033**: We deem cardinality ranges like `[*]`, `[n..*]`, or `[n..m]` problematic. We recommend using exact scalar cardinalities (e.g., `[1]`).

**0x10009**, **0x10010**, **0x10012**, **0x10AA3**, **0x10AA4**: Unresolved part usage, part definition, or port usage in a decomposed part def.
**Example**:
```sysml
...
part def A {
  connect somePart1.somePort to ...
}

```
**Fix:** Check the names used in your `connect` statements to ensure the parts and ports exist and are spelled correctly. `somePart1` has to be declared as a part usage with a type,
it has to have a part def and `somePort` has to be declared in the definition of the part usage.

**0x10017**, **0x10020**: Unresolved Action definition or usage.
**Example**:
```sysml
action def A1: A {
  ...
}
action a: A2;
```
**Fix:** Verify the spelling of the action and ensure it is defined in the current scope or imported. `A` and `A2` here were not defined anywhere in scope.
Please note that `A` must be an action def and `A2` can be both an action def and a usage.

**0x10018**: Specializations of port usages must be port usages or definitions.

**Fix:** Ensure the type assigned to your port is a valid port definition (see **0x10017**).

**0x10021**, **0x10022**: Unresolved Interface definition or usage.

**Fix:** Verify the spelling and existence of the referenced interface (see **0x10017**).

**0x10023**, **0x10024**: Unresolved Item definition or usage.

**Fix:** Verify the spelling and existence of the referenced item (see **0x10017**).

**0x10025**, **0x10026**, **0x10027**, **0x10028**, **0x10AA1**: Unresolved Part or Port definition/usage.

**Fix:** Ensure that the parts and ports you are trying to specialize or type actually exist in the model (see **0x10017**).

**0x10029**, **0x10030**: Source or target state in a transition is not defined.
**Example**:
```sysml
state S;
transition first S then T;
```
**Fix:** Verify that the `first` and `then` states `S` and `T` are declared in scope. In this case `S` is declared but `T` cannot be resolved.

**0x10034**, **0x10035**: Unresolved State definition.

**Fix:** Check the spelling of state definitions referenced in your models (see **0x10017**).

**0x10AA2**: Part def used in `refines` specializations does not exist (SpesML specific CoCo).

**Fix:** Check the spelling of the part definition you are trying to refine (see **0x10017**).

**0x10AA5**, **0x10AA6**: Illegal connection for subcomponent outputs.
```sysml
part def A {
  port i: SomePort;

  connect i to sub.o;
}
```
**Fix:** Ensure outputs of subcomponents only connect to inputs of other subcomponents or to outputs of the parent component.


**0x10AA6**: Illegal connection for parent component inputs. **Fix:** Ensure inputs of parent components only connect to inputs of subcomponents or outputs of the parent component.
**0x10AA7**: Subcomponent name is not unique. **Fix:** Give each part usage within a block a unique name.

### 0x80... and 0x81... Expressions & Type Checking
* **0x80001**, **0x80004**: Failed to derive a type for an expression (e.g., in constraints or transition guards). **Fix:** Check your expression for syntax errors or unresolved symbols.
* **0x80002**, **0x80005**: Expression type should be boolean. **Fix:** Ensure your constraint or transition guard evaluates to a true/false condition.
* **0x81001**: Type should not be a Stream. **Fix:** You are using a stream where a scalar value is expected. Check your stream instantiations.
* **0x81002** - **0x81009**: Set expression typing errors (LHS/RHS calculation failures, incompatible elements). **Fix:** When using subset or element-of operators, ensure both sides are evaluable, the right side is a Set, and the elements are of compatible types.
* **0x81010**: Stream not defined in global scope. **Fix:** Ensure the standard library is loaded properly.

### 0x90... Refinements
* **0x90011**, **0x90021**: Refinement connection topology mismatch. **Fix:** When a component refines another, their internal connection structure (number of connections) should remain similar to ensure structural consistency.
* **0x90020**, **0x90022**: Missing requirement or composition refinement. **Fix:** Ensure your high-level and low-level requirements and compositions form a continuous refinement chain.
* **0x90030**: Cyclic refinement detected. **Fix:** Refinements must be acyclic. Remove circular `refines` relationships (e.g., A refines B, and B refines A).
* **0x90031**: Trivial self-refinement. **Fix:** A component cannot refine itself. Remove the self-referencing `refines` clause.
* **0x9004**: Incompatible refinement interface. **Fix:** Ensure the ports and their types match exactly between the refined component and the abstract component.

### 0xA... Internal & Evaluation Errors
* **0xA0001**, **0xA0002**: KerML cardinality cannot produce a value. **Fix:** Ensure cardinalities use simple integer literals.
* **0xA0171**: Logical NOT (`not` or `!`) operator applied to a non-boolean. **Fix:** Only apply `not` to expressions that resolve to a boolean value.
* **0xA0321**: Attempted to access methods/attributes on a type variable. **Fix:** Do not use dot notation on generic type variables.
* **0xA1236**: Ambiguous symbol resolution. **Fix:** A name used in an expression resolves to multiple symbols. Use fully qualified names to resolve the ambiguity.
* **0xA1303**, **0xA1306**: Type or variable is not accessible. **Fix:** Check the visibility (e.g., `public`, `private`) of the referenced type or variable.
* **0xA1317**: Cannot find symbol. **Fix:** Ensure the variable, property, or method is declared and imported into the current scope.

### 0xD... Serialization Errors
* **0xD0001**, **0xD0100**, **0xD0101**: Symbol table serialization errors. **Fix:** This is an internal tool error usually caused by unsupported constructs like mixed causalities or unknown timings in the AST. Ensure your model conforms strictly to supported SysML constructs.

### 0xPA... Packaging & Environment Errors
* **0xPA090**, **0xPA091**, **0xPA092**: Missing standard library definitions. **Fix:** The tool failed to find or load the `Stream.symtabdefinition` from its resources. Ensure that the tool is installed correctly and all JAR files are intact.

### 0xFF... Verification & Modeling Guidelines
* **0xFF001**, **0xFF002**: Action definition named `done`. **Fix:** `done` is a reserved keyword in some contexts. Please rename your action.
* **0xFF003**, **0xFF004**: `do` actions are not supported. **Fix:** The verification engine does not support `do` actions in states. Use `entry`, `exit`, or external transitions instead.
* **0xFF005**: Name is not Isabelle compatible. **Fix:** Rename the element using only letters, numbers, and underscores. Ensure it does not start with a number.
* **0xFF006**: Channels (Ports) need a type. **Fix:** Ensure every attribute within a port definition has a type defined via `:` or `defined by`.
* **0xFF007**: `exit` actions are not supported. **Fix:** The verification engine does not currently support `exit` actions. Model this behavior using standard transitions instead.
* **0xFF0001**: Discouraged use of `flow` connections. **Fix:** Use standard `connect` or `connection` statements instead of `flow`.
* **0xFF0002**: Part must use at least one port. **Fix:** A well-modeled part should communicate with its environment. Add a port to your part definition.
* **0xFF0003**: Part has no explicit behavior. **Fix:** Add a constraint, a state machine, or subparts to define the behavior of the part.
* **0xFF0004**: Part has conflicting behaviors. **Fix:** Ensure a part uses exactly one type of behavior specification (constraint, state machine, or composition), not a mixture of them.

All other error codes describe internal errors and should be reported as a [bug](https://github.com/MontiCore/sysmlv2/issues).
