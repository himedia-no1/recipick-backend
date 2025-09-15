FROM eclipse-temurin:21-alpine AS builder
ARG MODULE
WORKDIR /app
COPY gradle gradle
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY libs libs
COPY apps apps
RUN chmod +x ./gradlew && \
    ./gradlew :apps:${MODULE}:bootJar --no-daemon -x test && \
    find apps/${MODULE}/build/libs -name "*.jar" -not -name "*-plain.jar" -exec cp {} app.jar \;

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -g 1001 app && adduser -u 1001 -G app -s /bin/sh -D app
COPY --from=builder --chown=app:app /app/app.jar .
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]