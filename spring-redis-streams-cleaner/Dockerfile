FROM openjdk:11-jdk-slim as build-env
COPY . /app
WORKDIR /app

RUN ./gradlew clean build --info --stacktrace --no-daemon

FROM openjdk:11-jre-slim
WORKDIR /app
ADD ./java-entrypoint.sh /app/java-entrypoint.sh
COPY --from=build-env /app/build/libs/*.jar /app/app.jar

USER root
RUN chmod +x /app/java-entrypoint.sh

ENTRYPOINT ["./java-entrypoint.sh"]
