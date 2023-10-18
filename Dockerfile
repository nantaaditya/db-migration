FROM openjdk:17-jdk-alpine

ENV TZ=Asia/Jakarta

RUN mkdir app
RUN mkdir app/resources-patch
RUN mkdir app/patch
RUN mkdir app/logs
RUN mkdir app/lib

ADD src/main/resources app/resources

ADD target/*.jar app/lib/
ADD target/dependency/*.jar app/lib/

WORKDIR /app

ENTRYPOINT [ "java","-cp",".:resources:patch/*:lib/*","com.nantaaditya.dbmigration.DbMigrationApplication" ]

