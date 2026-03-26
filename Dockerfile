# Estágio de Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e compila o projeto
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio de Execução
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

# O Render define a variável de ambiente PORT automaticamente.
# O Spring Boot reconhece a variável SERVER_PORT, então mapeamos uma para a outra.
ENV SERVER_PORT=8080
EXPOSE 8080

# Comando para iniciar a aplicação, garantindo que a porta do Render seja utilizada
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]