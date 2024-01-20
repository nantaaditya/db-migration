FROM eclipse-temurin:17.0.8.1_1-jre-jammy

MAINTAINER nantaaditya

LABEL CREATOR=nantaaditya
LABEL PROJECT_NAME=db-migration
LABEL JDK_VERSION=17
LABEL JDK_DISTRIBUTION=eclipse-temurin
LABEL JDK_BUILD_VERSION=17.0.8.1_1-jre-jammy
LABEL SPRING_VERSION=3.1.5

ENV TZ=Asia/Jakarta

RUN mkdir app
RUN mkdir app/logs
RUN mkdir app/files

ADD ../target/*.jar app/app.jar

WORKDIR /app

ENTRYPOINT exec java -server -XX:+UnlockExperimentalVMOptions $JAVA_OPTS -jar app.jar

