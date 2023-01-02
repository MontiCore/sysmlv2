<!-- (c) https://github.com/MontiCore/monticore -->
## Not complete coverage of the entire language

We temporarily gave up on covering the entire language.

## No names with spaces

The following is currently not supported:

```
package 'Package Name with Spaces;

constraint { some.'Name With Spaces'.x > 5 }
```

The reason is, that we were unable to override the `Name` token in such a way, that it covered these names with
quotation marks. We tried the following:

```
  @Override
  token Name = CharSequence | ( 'a'..'z' | 'A'..'Z' | '_' | '$' )
                              ( 'a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '$' )*;

  /**
    A sequence of characters in single quotation marks. Covers a similar
    purpose as the String token provided by MontiCore.
  */
  fragment token CharSequence
    = '\'' (SingleCharacter|EscapeSequence)+ '\''
    : {setText(getText().substring(1, getText() .length() - 1));}
  ;
```

The error for names as above a token recognition error after 2 characters. So for `this name`, it would fail at `h`.
Additionally, we would get an error from the ANTLR subsystem about `CharSequence` having an operation that would
never be executed. We tried to reduce `CharSequence` to only the use of `SingleCharacter`, which at least got rid of
the second error. We then tried to directly include the right-hand side of SingleCharacter (`~ ('\'')`, see
MontiCore) in the token `Name`. We error didnt go away. Debugging the Lexer for ~5 minutes didnt bring much light to
the situation. Try for yourself by placing breakpoints in `Log.error` and tracing the steps in the callstack!
