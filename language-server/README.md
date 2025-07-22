<!-- (c) https://github.com/MontiCore/monticore -->
# Language Server

A language server generated with the MCLSG (MontiCore Language Server Generator).

The last known stable version of the language server was in 7.6.1-7. That version of the plugin can be found [here](https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/packages/9886). (Requires access to the private gitlab project.)

## what should work and what does work

| Method                                                                 | does it work?         | notes                                                                                                                                                    |
|------------------------------------------------------------------------|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Installing the plugin and running it alone                             | yes                   | Works as intended                                                                                                                                        |
| Running the plugin alone without installing it                         | yes                   | Works as intended                                                                                                                                        |
| Installing the plugin and running the server seperately                | no                    | Even if set to connect to an already running server the client will always try to start its own server.                                                  |
| Running the plugin and server seperately without installing the plugin | only with workarounds | The client can be forced to connect to an already running server with an environment variable                                                            |
| Executing the gradle task 'runSysMLv2VscodePluginAttached'             | no                    | Current theory is that the client is started before the server is ready and then fails to connect properly. (only a guess. further insight is required.) |

## Known Issue

The client always tries to start its own server even when a server is already running, making it impossible to run the server seperately. The plugin provides a setting option that should make the client try to connect to an existing server however this does not work.
What does work is setting the environment variable 'SYSMLV2_LSP_PORT' to the port on which the server is running. This forces the client to connect however it doesnt provide the flexibility the plugin should normally possess.

# Language Server - Running with only the Plug-in

## Prerequisites

1. Install [NPM](https://www.npmjs.com/) and add it to `$PATH`.
2. Install the SysMLv2 VSCode plugin
   1. Execute the Gradle-Task 'buildSysmlv2VscodePlugin' in the `other` category. (the gradle Task 'generateSysmlv2VscodePlugin' in the `mc-lsp` category will not work)
   2. Execute the Gradle-Task 'packageSysmlv2VscodePlugin' in the `other` category.
   ![](doc/generatePlugIn.png)
   3. In the Folder "sysmlv2\language-server\target\generated-sources\SysMLv2\plugins\sysmlv2-vscode-plugin" you should now find a .vsix file.
   4. Install it by opening VS Code, navigating to the extension tab, `Views and further actions` (three dots), and selecting the entry to load the VSIX.
   ![](doc/install_vsix.png)

## Run

1. Close VSCode.
2. Make sure no language server is currently running (to do this in windows open the Task-Manager and terminate all task called `Java(TM) Platform SE` should there be any running).
3. Now Start VSCode and open a sysml file. After a few seconds the editor should display syntax highlighting.

# Language Server - Running the server and plug-in seperately

## Prerequisites

1. Install [NPM](https://www.npmjs.com/) and add it to `$PATH`.
2. Generate the VSCode plugin by executing the Gradle-Task 'buildSysmlv2VscodePlugin' in the `other` category. (the gradle Task 'generateSysmlv2VscodePlugin' in the `mc-lsp` category will not work)
3. (This step is only necessary due to a bug.) In the file `sysmlv2\language-server\target\generated-sources\SysMLv2\plugins\sysmlv2-vscode-plugin\.vscode\launch.json` add the line `"env": {"SYSMLV2_LSP_PORT": "3000"}`
![](doc/set_ENV.png)

## Run/Debug

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

## Errors

1. When running the Server fails and java throws the exception `Address already in use: NET_Bind` then there is likely already a server running. You can kill it in the task-manager by ending all tasks named `Java(TM) Platform SE`.
