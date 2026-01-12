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

![showcase](doc/showcase.png)

[download]: https://github-registry-files.githubusercontent.com/758456656/1b847700-819d-11f0-8dad-cb32a1bfe312?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250916%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250916T120634Z&X-Amz-Expires=300&X-Amz-Signature=8856e041f06680f48963357fe24467e82f549ab877f3902d9bbe5dc381c649c8&X-Amz-SignedHeaders=host&response-content-disposition=filename%3Dlanguage-server-7.6.1-10-vscode.vsix&response-content-type=application%2Foctet-stream

## Contributing

Find [known issues][issues] and more explanations on [how to develop for this
project **here**][devs].

## Known Issues

| Method                                                                 | does it work?         | notes                                                                                                                                                    |
|------------------------------------------------------------------------|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Installing the plugin and running it alone                             | yes                   | Works as intended                                                                                                                                        |
| Running the plugin alone without installing it                         | yes                   | Works as intended                                                                                                                                        |
| Installing the plugin and running the server seperately                | no                    | Even if set to connect to an already running server the client will always try to start its own server.                                                  |
| Running the plugin and server seperately without installing the plugin | only with workarounds | The client can be forced to connect to an already running server with an environment variable                                                            |
| Executing the gradle task 'runSysMLv2VscodePluginAttached'             | no                    | Current theory is that the client is started before the server is ready and then fails to connect properly. (only a guess. further insight is required.) |

The client always tries to start its own server even when a server is already
running, making it impossible to run the server seperately. The plugin provides
a setting option that should make the client try to connect to an existing
server however this does not work. What does work is setting the environment
variable 'SYSMLV2_LSP_PORT' to the port on which the server is running. This
forces the client to connect however it doesnt provide the flexibility the
plugin should normally possess.

## For Developers

### Language Server - Running with only the Plug-in

#### Prerequisites

1. Install [NPM](https://www.npmjs.com/) and add it to `$PATH`.
2. Install the SysMLv2 VSCode plugin
1. Execute the Gradle-Task 'buildSysmlv2VscodePlugin' in the `other` category. (the gradle Task 'generateSysmlv2VscodePlugin' in the `mc-lsp` category will not work)
2. Execute the Gradle-Task 'packageSysmlv2VscodePlugin' in the `other` category.
   ![](doc/generatePlugIn.png)
3. In the Folder "sysmlv2\language-server\target\generated-sources\SysMLv2\plugins\sysmlv2-vscode-plugin" you should now find a .vsix file.
4. Install it by opening VS Code, navigating to the extension tab, `Views and further actions` (three dots), and selecting the entry to load the VSIX.
   ![](doc/install_vsix.png)

#### Run

1. Close VSCode.
2. Make sure no language server is currently running (to do this in windows open the Task-Manager and terminate all task called `Java(TM) Platform SE` should there be any running).
3. Now Start VSCode and open a sysml file. After a few seconds the editor should display syntax highlighting.

### Language Server - Running the server and plug-in seperately

#### Prerequisites

1. Install [NPM](https://www.npmjs.com/) and add it to `$PATH`.
2. Generate the VSCode plugin by executing the Gradle-Task 'buildSysmlv2VscodePlugin' in the `other` category. (the gradle Task 'generateSysmlv2VscodePlugin' in the `mc-lsp` category will not work)
3. (This step is only necessary due to a bug.) In the file `sysmlv2\language-server\target\generated-sources\SysMLv2\plugins\sysmlv2-vscode-plugin\.vscode\launch.json` add the line `"env": {"SYSMLV2_LSP_PORT": "3000"}`
   ![](doc/set_ENV.png)

#### Run/Debug

1. Start the [`LanguageServerCLI`](src/main/java/de/monticore/lang/sysmlv2/_lsp/LanguageServerCLI.java) with parameters `--socket -port 3000`.
* Adapting the run configuration using IntelliJ Idea:
  * Navigate to the `LanguageServerCLI`, right-click on the main method > "Run.../Debug..."
  * Stop the running process again
  * Edit the run configuration ("Run" > "Edit Configurations") that was just created
  * Add `--socket -port 3000` to the CLI arguments textbox
  * Apply and run/debug from IntelliJ's main window
2. Open VSCode and open the folder `sysmlv2\language-server\target\generated-sources\SysMLv2\plugins\sysmlv2-vscode-plugin\`
3. Run/Debug the Plug-in in VSCode and open a sysml file in the new window opening up.
4. After a few seconds the editor should display syntax highlighting.
* If the language server functionality fails, restart and/or reopen the sysml files and check the debugging information of the language server.

#### Errors

1. When running the Server fails and java throws the exception `Address already in use: NET_Bind` then there is likely already a server running. You can kill it in the task-manager by ending all tasks named `Java(TM) Platform SE`.
