<!-- (c) https://github.com/MontiCore/monticore -->
# SysML 2 Official

Project for the SysML 2 language familly in MontiCore. It is compatible with the general standard.
The language should a strict compatible superset of the SysML 2 Syntax. It can also parse
models which are not valid, because of the additional CoCos this project has.

This project is based on the 2020-03 pilot implementation of SysML 2.

It reuses MontiCore language.

Also a Pretty Printer should be written. 

#Useful commands Maven build

mvn clean install (to build everything)
mvn clean install -DskipTests

#Useful commands Gradle build

``
gradle build
``
``
gradle test -i --fail-fast
`` (Enables Info logging and fast fail)
