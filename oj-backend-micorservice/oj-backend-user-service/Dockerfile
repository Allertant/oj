FROM openjdk:8-jdk-alpine

WORKDIR /app

ADD target/oj-backend-user-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/oj-backend-user-service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]