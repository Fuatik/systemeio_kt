# build stage
FROM gradle:jdk-alpine AS build
WORKDIR /app
COPY . /app
RUN gradle clean build

# final stage
FROM eclipse-temurin:22-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
