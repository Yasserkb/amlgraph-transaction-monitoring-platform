FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY services ./services

WORKDIR /workspace/services
RUN mvn -B -ntp -pl gateway -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S amlgraph && adduser -S amlgraph -G amlgraph
COPY --from=build /workspace/services/gateway/target/gateway-*.jar app.jar
USER amlgraph
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
