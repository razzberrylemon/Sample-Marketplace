FROM openjdk:23
COPY target/market-0.0.1-SNAPSHOT.jar /market.jar
CMD ["/usr/bin/java", "-jar", "/market.jar"]