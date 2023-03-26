FROM openjdk:17-alpine

RUN apk update && apk upgrade

WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]