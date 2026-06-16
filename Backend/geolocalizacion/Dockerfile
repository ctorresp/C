# ETAPA 1: Construcción (Compila el código fuente)
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución (Crea la imagen ligera)
FROM eclipse-temurin:22-jre
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Copia el .jar que se acaba de fabricar en la ETAPA 1
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8082 
ENTRYPOINT ["java", "-jar", "app.jar"]