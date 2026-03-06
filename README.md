# Java + Docker (Treinamento Inicial)

Projeto de exemplo com Spring Boot + H2 para praticar empacotamento e execucao com Docker.

## Objetivo do treinamento
- Entender como gerar a aplicacao Java dentro de uma imagem Docker.
- Aprender a subir a aplicacao com `docker compose`.
- Ver como expor a porta da API e acessar o console H2.

## Estrutura do projeto
```text
java-docker-h2/
|- src/
|  |- main/
|  |  |- java/com/docker/api/ApiApplication.java
|  |  |- resources/application.properties
|  |- test/
|     |- java/com/docker/api/ApiApplicationTests.java
|- Dockerfile
|- docker-compose.yml
|- pom.xml
|- README.md
```

## Pre-requisitos
- Docker Desktop instalado e em execucao
- Docker Compose (ja vem com Docker Desktop atual)

## Dockerfile explicado (linha a linha)
```dockerfile
# Etapa 1: imagem com Maven + JDK para compilar o projeto
FROM maven:3.9.11-eclipse-temurin-25 AS build

# Diretorio de trabalho dentro do container
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar cache de dependencias
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copia o codigo-fonte e gera o .jar
COPY src ./src
RUN mvn -B -DskipTests package

# Etapa 2: imagem final mais leve, so para executar
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copia o .jar gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

# Porta da aplicacao Spring Boot
EXPOSE 8080

# Comando de inicializacao da aplicacao
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## docker-compose explicado
```yaml
services:
  app:
    # Build usando Dockerfile da raiz do projeto
    build: .
    # Nome fixo do container para facilitar localizacao
    container_name: spring_h2_app
    # Mapeia host:container (localhost:8080 -> app:8080)
    ports:
      - "8080:8080"
    # Reinicia automaticamente caso o processo caia
    restart: always
```

## Como rodar
1. Na raiz do projeto, execute:
```bash
docker compose up --build
```
2. Acesse a aplicacao:
- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`

## Como parar
```bash
docker compose down
```

## Troubleshooting rapido
- Erro `failed to solve: lstat /target: no such file or directory`:
  - O Dockerfile antigo tentava copiar `target/*.jar` do host.
  - Este projeto agora compila o `.jar` dentro do container (multi-stage), evitando esse erro.
- Se a porta 8080 estiver ocupada:
  - Altere para outra porta no `docker-compose.yml`, exemplo: `"8081:8080"`.
