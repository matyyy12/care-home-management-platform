FROM maven:3.9-eclipse-temurin-17 AS backend-build

WORKDIR /app/backend

COPY backend/pom.xml ./
RUN mvn -DskipTests dependency:go-offline

COPY backend/src ./src
RUN mvn -DskipTests package

FROM maven:3.9-eclipse-temurin-17 AS backend-test

WORKDIR /app/backend

COPY backend/pom.xml ./
RUN mvn -DskipTests dependency:go-offline

COPY backend/src ./src

CMD ["mvn", "-B", "-ntp", "test"]

FROM eclipse-temurin:17-jre AS backend

WORKDIR /app

COPY --from=backend-build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
