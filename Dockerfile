FROM eclipse-temurin:21-alpine as builder
ARG MODULE
WORKDIR /app
COPY gradle gradle
COPY gradlew .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY libs/ libs/
COPY apps/ apps/
RUN ./gradlew :apps:${MODULE}:bootJar --no-daemon

FROM eclipse-temurin:21-alpine
ARG MODULE
WORKDIR /app
COPY --from=builder /app/apps/${MODULE}/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]