# ---------- Etapa 1: COMPILAR (imagen grande, con Maven y el JDK) ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# primero solo el pom.xml: así Docker guarda en caché las dependencias
# y no las vuelve a descargar si solo cambia el código
COPY pom.xml .
RUN mvn dependency:go-offline -q

# después el código, y genera el .jar (los tests ya se pasan aparte)
COPY src ./src
RUN mvn package -DskipTests -q

# ---------- Etapa 2: EJECUTAR (imagen pequeña, solo con Java) ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# de la etapa 1 solo nos llevamos el .jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
