FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/amusementPark-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java -jar app.jar" ]