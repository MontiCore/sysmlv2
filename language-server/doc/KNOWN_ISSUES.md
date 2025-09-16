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
