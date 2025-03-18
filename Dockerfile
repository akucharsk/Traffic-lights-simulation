FROM maven:3.9.6-eclipse-temurin-17 AS java-build

WORKDIR /app

COPY src ./src
COPY pom.xml .

RUN mvn clean package

FROM openjdk:17

WORKDIR /app
COPY --from=java-build /app/target/*.jar server.jar
VOLUME data ./data
ENTRYPOINT ["java", "-jar", "server.jar"]
