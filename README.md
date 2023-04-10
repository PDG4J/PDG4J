# PDG4J

PoC Analyser for Java source code utilising Program Dependence Graph

## Build

This project uses unpublished modules from [Spoon](https://github.com/INRIA/spoon).  
Our team has made a [fork](https://github.com/pdg4j/spoon) of it to bump up maven plugins & make these modules publishable.

First of all, we need to install these packages locally:
```shell
mkdir spoon && cd spoon && git clone https://github.com/PDG4J/spoon . && cd spoon-control-flow
mvn -DskipTests -Dcheckstyle.skip -am install
```

Then we can build PDG4J itself:
```shell
cd ../ && mkdir pdg4j && cd pdg4j && git clone https://github.com/PDG4J/PDG4J .
./gradlew build
```
