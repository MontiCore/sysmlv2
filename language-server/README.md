<!-- (c) https://github.com/MontiCore/monticore -->
# SysML v2 Syntax Support

Editor-agnostic, state-of-the-art syntax support for SysML v2 textual notation.
Provides contextual syntax highlighting of keywords, auto-completion, navigation
between usages and definitions, error reporting, and hints.

This tool build's on [Microsoft's Language Server Protocol (LSP)][lsp], a
protocol that's used between an editor or IDE and a language server that
provides language features like auto complete, go to definition, find all
references etc. Our language server is automatically generated from our [SysML
v2 language implementation][language] via the MontiCore Language Server
Generator (MCLSG).

Compared to the [pilot implementation's syntax support][sst], our implementation
* **is editor-agnostic**, i.e., any editor supporting the Language Server
  Protocol can connect to our server to provide rich syntax support.
* **provides a single implementation** usable across different editor, no need
  for re-implementation for each editor/IDE.
* **enabled the most advanced analytics**, contextual highlighting,
  expression support, and is fully extendable. No need to define non-contextual
  keyword-lists or give up go-to-definition and contextual hints.

[lsp]: https://microsoft.github.io/language-server-protocol/
[language]: ../language/README.md
[sst]: https://github.com/Systems-Modeling/SysML-v2-Pilot-Implementation/tree/master/tool-support/syntax-highlighting

## Download and Usage

You can use our language server with any LSP-compatible editor or IDE. Try out
installing our integrated **VSCode Plugin** to get going the fastest:

1. [**Download** the latest version][download]
2. **Install** by simply dragging the downloaded VSIX-file into the extensions
   view in VSCode
3. **Done**! Now create or open a text file with the ending `.sysml`:

![showcase](../doc/showcase.png)

[download]: https://github-registry-files.githubusercontent.com/758456656/1b847700-819d-11f0-8dad-cb32a1bfe312?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250916%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250916T120634Z&X-Amz-Expires=300&X-Amz-Signature=8856e041f06680f48963357fe24467e82f549ab877f3902d9bbe5dc381c649c8&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dlanguage-server-7.6.1-10-vscode.vsix&response-content-type=application%2Foctet-stream

## Contributing

Find [known issues][issues] and more explanations on [how to develop for this
project **here**][devs].

[issues]: ../doc/KNOWN_ISSUES.md
[devs]: ../doc/FOR_DEVELOPERS.md
