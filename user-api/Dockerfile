## user-api/Dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/*.jar user-api.jar
RUN mkdir /logs && chmod 777 /logs
COPY src/main/resources/application.yml application.yml
COPY src/main/resources/logback-spring.xml logback-spring.xml

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "user-api.jar"]