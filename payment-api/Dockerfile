## payment-api/Dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/*.jar payment-api.jar
RUN mkdir /logs && chmod 777 /logs
COPY src/main/resources/application.yml application.yml
COPY src/main/resources/logback-spring.xml logback-spring.xml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "payment-api.jar"]