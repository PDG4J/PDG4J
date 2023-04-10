FROM ubuntu
RUN apt update && apt install -y openjdk-17-jdk openjdk-17-jre git maven
RUN git clone https://github.com/PDG4J/spoon
RUN cd spoon/spoon-control-flow && mvn -DskipTests -Dcheckstyle.skip -am install
RUN git clone https://github.com/PDG4J/PDG4J
RUN cd PDG4J && chmod +x gradlew && ./gradlew build
RUN rm PDG4J/build/libs/pdg4j-*-slim.jar
RUN for f in PDG4J/build/libs/pdg4j-*.jar; do mv -i "$f" "pdg4j.jar"; done
RUN rm -r spoon && rm -r PDG4J
ENTRYPOINT java -jar pdg4j.jar