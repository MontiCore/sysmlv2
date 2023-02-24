<!-- (c) https://github.com/MontiCore/monticore -->
# Language Server

A language server generated with the MCLSG (MontiCore Language Server Generator).

## Prerequisites

1. Install [NPM](https://www.npmjs.com/) and add it to `$PATH`.
2. Install the SysMLv2 VSCode plugin
   1. Download the plugin from the [paketregistry](https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/packages/5793).
   2. Install it by opening VS Code, navigating to the extension tab, `Views and further actions` (three dots), and selecting the entry to load the VSIX.
   ![](doc/install_vsix.png)

## Run/Debug

1. Start the [`LanguageServerCLI`](src/main/java/de/monticore/lang/sysmlv2/_lsp/LanguageServerCLI.java) with parameters `--socket -port 3000`.
   * Adapting the run configuration using IntelliJ Idea:
      * Navigate to the `LanguageServerCLI`, right-click on the main method > "Run.../Debug..."
      * Stop the running process again
      * Edit the run configuration ("Run" > "Edit Configurations") that was just created
      * Add `--socket -port 3000` to the CLI arguments textbox
      * Apply and run/debug from IntelliJ's main window
2. Restart VS Code and select a `sysml`-file.
3. After a few seconds the editor should display syntax highlighting.
   * If the language server functionality fails, restart and/or reopen the sysml files and check the debugging information of the language server.
