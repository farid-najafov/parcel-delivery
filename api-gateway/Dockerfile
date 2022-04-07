FROM openjdk:11-jre-slim
EXPOSE 8090
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
