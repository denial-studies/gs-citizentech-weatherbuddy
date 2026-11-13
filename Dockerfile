FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw clean package -DskipTests && cp presentation/target/presentation-*.jar app.jar

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

