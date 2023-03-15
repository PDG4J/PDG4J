# PDG4J

PoC Analyser for Java source code utilising Program Dependence Graph

## Build

This project uses unpublished modules from [https://github.com/INRIA/spoon](Spoon).  
Our team has made a [https://github.com/pdg4j/spoon](fork) of it to bump up maven plugins & make these modules publishable.

First of all, we need to install these packages locally:
```shell
mkdir spoon && cd spoon && git clone https://github.com/PDG4J/spoon .
mvn clean install -DskipTests -Dcheckstyle.skip
```

Then we can build PDG4J itself:
```shell
cd ../ && mkdir pdg4j && cd pdg4j && git clone https://github.com/PDG4J/PDG4J .
./gradlew build
```