FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/*.jar app.jar
EXPOSE 8999
ENTRYPOINT [ "sh", "-c", "java -jar app.jar" ]
