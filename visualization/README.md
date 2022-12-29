<!-- (c) https://github.com/MontiCore/monticore -->
## SysML v2 Visualization

Project for the SysML v2. Requires models that are compatible with the general standard.

### Usage

The application is to be used as a standard jar from command line. Additional information about the CLI is available through using the `--help` option.
Furthermore, for the visualization of non-trivial SysML components GraphViz Dot is needed. Visit https://www.graphviz.org/download/ and download the appropriate package for your environment.

### Setup for developers

This project uses the SysML v2's official implementation on GitHub. It thus needs a GitHub personal access token
(PAT). Create this PAT on GitHub by navigating to Settings > Developer Settings. The token needs `read_registry`
rights. Then add it and your GitHub user name to `$GRADLE_USER_HOME/gradle.properties` as follows:
```
githubUser = GITHUB_USERNAME
githubToken = GITHUB_PAT
```
