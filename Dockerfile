FROM maven:3.9.6-eclipse-temurin-17 AS java-build

WORKDIR /app

COPY src ./src
COPY pom.xml .

RUN mvn clean package

FROM python:3.10

WORKDIR /app

COPY --from=java-build /app/target/*.jar server.jar
COPY src ./src
COPY input.json input.json
COPY output.json output.json

RUN apt-get update && apt-get install -y default-jdk
RUN pip install --no-cache-dir fastapi uvicorn jinja2

CMD java -jar server.jar & sleep 1 && PYTHONPATH=src/main/python uvicorn web:app --host 0.0.0.0 --port 8000
