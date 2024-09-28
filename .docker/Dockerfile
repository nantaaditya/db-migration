FROM eclipse-temurin:17.0.8.1_1-jre-jammy

ARG APP_VERSION
ARG APP_CREATED_DATE
ARG APP_PORT

LABEL org.opencontainers.image.created=${APP_CREATED_DATE}
LABEL org.opencontainers.image.authors=nantaaditya
LABEL org.opencontainers.image.url=https://hub.docker.com/r/nantaaditya/db-migration
LABEL org.opencontainers.image.documentation=https://github.com/nantaaditya/db-migration/blob/master/README.md
LABEL org.opencontainers.image.source=https://github.com/nantaaditya/db-migration
LABEL org.opencontainers.image.version=${APP_VERSION}
LABEL org.opencontainers.image.title=db-migration

LABEL JRE_VERSION=17
LABEL JRE_DISTRIBUTION=eclipse-temurin
LABEL JRE_BUILD_VERSION=17.0.8.1_1-jre-jammy
LABEL SPRING_VERSION=3.1.5

ENV TZ=Asia/Jakarta

RUN mkdir app
RUN mkdir app/logs
RUN mkdir app/files

ADD ../target/*.jar app/app.jar

WORKDIR /app

EXPOSE ${APP_PORT}

ENTRYPOINT exec java -server -XX:+UnlockExperimentalVMOptions -XX:+CompactStrings $JAVA_OPTS -jar app.jar

