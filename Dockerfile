# ─────────────────────────────────────────
# Etapa 1: Build
# ─────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiamos el pom primero para aprovechar el cache de capas de Docker:
# si las dependencias no cambian, esta capa se reutiliza sin re-descargar.
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiamos el código fuente y compilamos sin ejecutar tests
COPY src ./src
RUN mvn package -DskipTests -q

# ─────────────────────────────────────────
# Etapa 2: Runtime
# ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Usuario no-root por seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiamos únicamente el jar generado en la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# El proceso corre con el usuario sin privilegios
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
