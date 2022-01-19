## SysML v2 Visualization

Project for the SysML v2. Requires models that are compatible with the general standard.

### Usage

The application is to be used as a standard jar from command line. Additional information about the CLI is available through using the `--help` option.
Furthermore, for the visualization of non-trivial SysML components GraphViz Dot is needed. Visit https://www.graphviz.org/download/ and download the appropriate package for your environment.

### Setup for developers

Project uses dependencies published to GitHub thus requiring a GitHub personal access token.
Please create a personal `gradle.properties` file in your local `GRADLE_USER_HOME` (usually situated at `<<User>>/.gradle`)
and add the following:
```
githubUser = GITHUB_USERNAME
githubToken = YOUR_PAN
```
