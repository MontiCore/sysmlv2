<!-- (c) https://github.com/MontiCore/monticore -->
# For Developers

## Building the Tool from the Sources

As alternative to a download, it is possible to build an executable JAR of the
tool from the source files located in GitHub. In order to build an executable
Jar of the tool with Bash from the source files available in GitHub, execute the
following commands.

First, clone the repository:
```bash
git clone https://github.com/MontiCore/sysmlv2.git
```

Change the directory to the root directory of the cloned sources:
```bash
cd sysmlv2
```

Then build the source files with gradle. To this effect, execute the following
two command:

```bash
./gradlew :language:shadowJar
```
Please ensure that you have Java 21 JRE installed and gradle is set up to use it.

Congratulations! The executable JAR file `language-7.*.*-SNAPSHOT-mc-tool.jar` is
now in the directory `language/target/libs`.
