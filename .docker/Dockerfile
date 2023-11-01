FROM eclipse-temurin:17.0.8.1_1-jre-jammy

ENV TZ=Asia/Jakarta

RUN mkdir app
RUN mkdir app/logs

ADD ../target/*.jar app/app.jar

WORKDIR /app

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar

