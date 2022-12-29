<!-- (c) https://github.com/MontiCore/monticore -->
# Language Server

A language server generated with the MCLSG (MontiCore Language Server Generator).

## Prerequisites

Install [NPM](https://www.npmjs.com/) and add it to `$PATH`

## Run/Debug

Start the [`LanguageServerCLI`](src/main/java/de/monticore/lang/sysml4verification/_lsp/LanguageServerCLI.java) with
parameters `--socket -port 3000`, then connect to the running server using the generated
[VSCode extension](../vscode-plugin/README).

### IntelliJ

* Navigate to the `LanguageServerCLI`, right click on the main method > "Run.../Debug..."
* Stop the running process again
* Edit the run configuration ("Run" > "Edit Configurations") that was just created
* Add `--socket -port 3000` to the CLI arguments textbox
* Apply and run/debug from IntelliJ's main window
