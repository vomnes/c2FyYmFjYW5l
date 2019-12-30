FROM openjdk:11-jdk as build

WORKDIR /java
COPY . /java

RUN ./mvnw package -Dmaven.test.skip=true

ENTRYPOINT ./mvnw test | egrep "(^\[INFO\]|^\[DEBUG\])" && java -jar target/CSV-Extractor-0.0.1-SNAPSHOT.jar
