FROM openjdk:17-jdk-alpine

ENV TZ=Asia/Jakarta

RUN mkdir app
RUN mkdir app/logs

ADD src/main/resources app/resources

ADD target/*.jar app/app.jar

WORKDIR /app

ENTRYPOINT [ "java","-jar","app.jar" ]

