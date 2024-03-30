FROM openjdk:8-jdk-alpine

WORKDIR /app

ADD target/oj-backend-question-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/oj-backend-question-service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]