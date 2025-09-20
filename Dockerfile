FROM eclipse-temurin:21-alpine AS builder

ARG MODULE
WORKDIR /app

# Gradle 설정 파일 먼저 복사 (캐시 최적화)
COPY gradle gradle
COPY gradlew settings.gradle.kts build.gradle.kts ./

# 의존성 다운로드 (별도 레이어로 캐시 최적화)
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies

# 소스 코드 복사
COPY libs libs
COPY apps apps

# 빌드 실행
RUN ./gradlew :apps:${MODULE}:bootJar --no-daemon -x test && \
    find apps/${MODULE}/build/libs -name "*.jar" -not -name "*-plain.jar" -exec cp {} app.jar \;

FROM eclipse-temurin:21-jre-alpine

# 보안 업데이트 및 필수 패키지
RUN apk --no-cache add dumb-init && \
    addgroup -g 1001 app && \
    adduser -u 1001 -G app -s /bin/sh -D app

WORKDIR /app
COPY --from=builder --chown=app:app /app/app.jar .

USER app
EXPOSE 8080

# dumb-init으로 시그널 처리 개선 및 JVM 최적화
ENTRYPOINT ["dumb-init", "java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=80.0", "-jar", "app.jar"]