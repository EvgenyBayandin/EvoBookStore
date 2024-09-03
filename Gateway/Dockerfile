# Gateway Dockerfile
FROM openjdk:17-jdk-alpine AS build

# Установка Maven
RUN apk add --no-cache maven

# Копирование исходных файлов и файла pom.xml
COPY src /app/src
COPY pom.xml /app/pom.xml

# Сборка проекта
WORKDIR /app
RUN mvn clean package

# Создание образа для запуска приложения
FROM openjdk:17-jdk-alpine

# Копирование собранного JAR-файла
COPY --from=build /app/target/Gateway-0.0.1-SNAPSHOT.jar /app/Gateway-0.0.1-SNAPSHOT.jar

# Экспорт порта 8080
EXPOSE 8080

# Команда для запуска приложения
CMD ["java", "-jar", "/app/Gateway-0.0.1-SNAPSHOT.jar"]