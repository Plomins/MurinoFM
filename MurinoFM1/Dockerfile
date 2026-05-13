# Этап сборки
FROM gradle:8.12-jdk21-alpine AS build
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon
COPY src ./src
RUN ./gradlew bootJar --no-daemon

# Финальный образ
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Создаём пользователя и группу (например, 'app')
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Создаём папки для загружаемых файлов и даём права
RUN mkdir -p /app/uploads/audio /app/uploads/images && \
    chown -R appuser:appgroup /app/uploads

COPY --from=build /app/build/libs/*.jar app.jar

# Переключаемся на непривилегированного пользователя
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]