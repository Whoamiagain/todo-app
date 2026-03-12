
FROM openjdk:25-jdk-slim AS build
RUN apt-get update && apt-get install -y maven

COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:25-jdk-slim
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]