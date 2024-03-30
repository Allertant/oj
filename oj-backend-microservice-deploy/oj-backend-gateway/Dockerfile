FROM openjdk:8-jdk-alpine

WORKDIR /app

ADD target/oj-backend-gateway-0.0.1-SNAPSHOT.jar .

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/oj-backend-gateway-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]