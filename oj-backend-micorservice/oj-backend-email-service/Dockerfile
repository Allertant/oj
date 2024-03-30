FROM openjdk:8-jdk-alpine

WORKDIR /app

ADD target/oj-backend-email-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/oj-backend-email-service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]