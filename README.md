### Estrutura total
meu-projeto/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/seuprojeto/app/
│  │  │     ├─ controller/
│  │  │     │  └─ ProdutoController.java
│  │  │     ├─ service/
│  │  │     │  └─ ProdutoService.java
│  │  │     ├─ repository/
│  │  │     │  └─ ProdutoRepository.java
│  │  │     ├─ model/
│  │  │     │  └─ Produto.java
│  │  │     └─ AppApplication.java
│  │  └─ resources/
│  │     ├─ application.properties
│  │     └─ data.sql
├─ Dockerfile
├─ docker-compose.yml
├─ pom.xml
└─ README.md

### Resumo
src/main/java/com/seuprojeto/app/
├─ controller/      -> endpoints da aplicação
├─ service/         -> regras de negócio
├─ repository/      -> acesso aos dados com JPA
├─ model/           -> entidades
└─ AppApplication.java

### Dockerfile
```
FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml
```
version: "3.8"

services:
  app:
    build: .
    container_name: spring_h2_app
    ports:
      - "8080:8080"
    restart: always
```

### application.properties
```
spring.application.name=app

server.port=8080

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

### Rodar o projeto
```
docker compose up --build
```