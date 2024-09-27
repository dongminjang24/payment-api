## notification-api/Dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY build/libs/*.jar notification-api.jar
RUN mkdir /logs && chmod 777 /logs
COPY src/main/resources/application.yml application.yml
COPY src/main/resources/logback-spring.xml logback-spring.xml

EXPOSE 8081  # 알림 서버용 포트 (결제 서버와 충돌 방지)

ENTRYPOINT ["java", "-jar", "notification-api.jar"]