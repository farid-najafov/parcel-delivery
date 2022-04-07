FROM openjdk:11-jre-slim
EXPOSE 8761
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
