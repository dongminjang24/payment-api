# 스프링 프로젝트 내 루트경로에 위치 Dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
COPY src/main/resources/application.yml application.yml
ENTRYPOINT ["java", "-jar", "app.jar"]