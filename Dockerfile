FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
RUN mkdir /logs && chmod 777 /logs
COPY src/main/resources/application.yml application.yml
COPY src/main/resources/logback-spring.xml logback-spring.xml

ENTRYPOINT ["java", "-jar", "app.jar"]