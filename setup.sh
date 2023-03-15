mkdir spoon && cd spoon && git clone https://github.com/PDG4J/spoon .
mvn clean install -DskipTests -Dcheckstyle.skip
cd ../ && mkdir pdg4j && cd pdg4j && git clone https://github.com/PDG4J/PDG4J .
./gradlew build