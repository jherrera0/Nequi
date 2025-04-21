FROM gradle:8.13.0-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle bootJar

# Etapa de ejecución: Usando OpenJDK 17
FROM eclipse-temurin:17-jdk AS runtime
EXPOSE 8090
COPY --from=build /home/gradle/project/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]