FROM amazoncorretto:21.0.1-al2-generic as build
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 80

ENTRYPOINT java \
  -XX:MaxRAMPercentage=60 \
  -jar app.jar
