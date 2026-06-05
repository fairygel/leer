FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY gradle/ ./gradle/
COPY gradlew build.gradle.kts settings.gradle.kts ./

RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN groupadd -r -g 1001 appgroup && \
    useradd -r -u 1001 -g appgroup -s /sbin/nologin -M appuser

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build --chown=appuser:appgroup  /app/build/libs/*.jar app.jar

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-jar", "app.jar"]