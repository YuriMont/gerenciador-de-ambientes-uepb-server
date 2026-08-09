# Imagem da API (Spring Boot). O frontend em apps/web é publicado à parte,
# como Static Site no Render, então nada de Node aqui.
#
# O contexto de build é apps/server — não a raiz do repositório. No Render:
#   Dockerfile Path:  ./apps/server/Dockerfile
#   Docker Build Context Directory: ./apps/server

# ---------- Estágio de build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# As dependências vêm antes do código: esta camada só é refeita quando o pom.xml
# muda, o que poupa vários minutos nos deploys em que só o src mudou.
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp package -DskipTests

# ---------- Estágio de execução ----------
# Só o JRE: a imagem final não carrega Maven nem o JDK, ficando bem menor.
FROM eclipse-temurin:21-jre AS runtime

# Rodar como root dentro do container não traz nenhum ganho aqui.
RUN groupadd --system spring \
    && useradd --system --gid spring --shell /usr/sbin/nologin spring

WORKDIR /app

# O spring-boot-maven-plugin deixa em target/ o jar executável e um
# .jar.original (que não casa com o glob abaixo, por não terminar em .jar).
COPY --from=build --chown=spring:spring /build/target/*.jar app.jar

USER spring

# O container nasce em UTC. Como as reservas são por data e por slot de hora,
# sem isto o "hoje" do servidor virava 3 h antes da meia-noite local.
ENV TZ=America/Recife

# A aplicação exige um perfil ativo: application.properties tem
# `spring.profiles.active=${APP_PROFILE}` sem valor padrão, e é o perfil que
# traz `spring.data.mongodb.uri`. Os perfis existentes são dev e test — só o dev
# serve para rodar de verdade. Dá para sobrescrever pelo dashboard do Render.
ENV APP_PROFILE=dev

# MaxRAMPercentage faz a JVM enxergar o limite do container em vez da RAM da
# máquina — sem isso a heap estoura os 512 MB dos planos menores do Render.
# SerialGC gasta menos memória e menos CPU nos planos de 0,1–0,5 vCPU.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC"

# Documental: quem manda de fato é a variável PORT injetada pelo Render.
EXPOSE 8080

# `exec` faz o java virar PID 1, para que o SIGTERM do Render chegue nele e o
# desligamento seja limpo em vez de terminar por timeout.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
