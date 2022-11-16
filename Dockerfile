FROM openjdk:11
WORKDIR /
ADD  /target/aggregator-0.0.1-SNAPSHOT.jar aggregator-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD java -jar aggregator-0.0.1-SNAPSHOT.jar
