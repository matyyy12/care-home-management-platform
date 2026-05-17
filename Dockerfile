FROM node:22-alpine AS frontend-build

WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

FROM nginx:1.27-alpine AS frontend

COPY --from=frontend-build /app/frontend/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

FROM maven:3.9-eclipse-temurin-17-alpine AS backend-build

WORKDIR /app/backend

COPY backend/pom.xml ./
RUN mvn -DskipTests dependency:go-offline

COPY backend/src ./src
RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre-alpine AS backend

WORKDIR /app

COPY --from=backend-build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
