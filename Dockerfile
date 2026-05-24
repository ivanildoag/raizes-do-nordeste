# =============================================================================
# Dockerfile para Raízes do Nordeste API
# Utiliza multi-stage build para otimizar o tamanho da imagem final
# =============================================================================

# --- Estágio 1: Build com Maven ---
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro para cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Estágio 2: Runtime com JRE ---
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria usuário não-root por segurança (boas práticas de segurança)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copia o JAR gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

# Define o usuário não-root
USER appuser

# Porta da aplicação
EXPOSE 8080

# Health check para verificar se a aplicação está rodando
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para iniciar a aplicação com o perfil Docker ativo
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
