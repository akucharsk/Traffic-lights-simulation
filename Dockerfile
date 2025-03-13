FROM maven:3.9.6-eclipse-temurin-17 AS java-build
WORKDIR /app

COPY src ./src
COPY pom.xml pom.xml

RUN mvn clean package

FROM openjdk:17
WORKDIR /app

COPY --from=java-build /app/target/*.jar traffic.jar
COPY src/main/resources ./src/main/resources

ENTRYPOINT ["java", "-jar", "traffic.jar"]
