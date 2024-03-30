FROM openjdk:8-jdk-alpine

WORKDIR /app

ADD target/oj-backend-judge-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/oj-backend-judge-service-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]