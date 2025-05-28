FROM eclipse-temurin:21-jdk

WORKDIR /app

RUN pwd && ls -la

COPY target/*.jar app.jar

RUN ls -la

EXPOSE 8080

ENTRYPOINT ["java", "-verbose", "-jar", "app.jar"]
