FROM maven:3.9.6-eclipse-temurin-17 AS java-build

WORKDIR /app

COPY src ./src
COPY pom.xml .

RUN mvn clean package

FROM openjdk:17

WORKDIR /app
COPY --from=java-build /app/target/traffic-1.1-SNAPSHOT.jar server.jar
VOLUME ["/app/output"]
ENTRYPOINT ["java", "-jar", "server.jar"]
