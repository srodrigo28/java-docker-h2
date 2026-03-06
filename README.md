# Java + Docker (Treinamento Inicial)

Projeto de exemplo com Spring Boot + H2 para praticar empacotamento e execucao com Docker.

## Objetivo do treinamento
- Entender como gerar a aplicacao Java dentro de uma imagem Docker.
- Aprender a subir a aplicacao com `docker compose`.
- Ver como expor a porta da API e acessar o console H2.

## Estrutura do projeto
```text
meu-projeto/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/seuprojeto/app/
│  │  │     ├─ AppApplication.java
│  │  │     ├─ controller/
│  │  │     │  └─ ProdutoController.java
│  │  │     ├─ service/
│  │  │     │  └─ ProdutoService.java
│  │  │     ├─ repository/
│  │  │     │  └─ ProdutoRepository.java
│  │  │     ├─ model/
│  │  │     │  └─ Produto.java
│  │  │     └─ dto/
│  │  │        └─ ProdutoRequestDTO.java
│  │  └─ resources/
│  │     ├─ application.properties
│  │     └─ data.sql
│  └─ test/
│     └─ java/
├─ target/
├─ Dockerfile
├─ docker-compose.yml
├─ pom.xml
└─ README.md
```

## Pre-requisitos
- Docker Desktop instalado e em execucao
- Docker Compose (ja vem com Docker Desktop atual)

## Dockerfile explicado (linha a linha)
```dockerfile
# Etapa 1: imagem com Maven + JDK para compilar o projeto
FROM maven:3.9.11-eclipse-temurin-21 AS build

# Diretorio de trabalho dentro do container
WORKDIR /app

# Copia apenas o pom.xml primeiro para aproveitar cache de dependencias
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copia o codigo-fonte e gera o .jar
COPY src ./src
RUN mvn -B -Dmaven.test.skip=true package

# Etapa 2: imagem final mais leve, so para executar
FROM eclipse-temurin:21-jre
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

## Plano do 1 CRUD: Produtos
Objetivo inicial: cadastrar e listar produtos.

### Campos do produto
- `nome` (texto, obrigatorio)
- `qtd` (inteiro, obrigatorio, minimo 0)
- `valor` (decimal, obrigatorio, maior que 0)

### Rotas da API
1. `POST /produtos`
   - Cadastra um novo produto.
   - Body JSON:
```json
{
  "nome": "Notebook",
  "qtd": 10,
  "valor": 3500.00
}
```
2. `GET /produtos`
   - Lista todos os produtos cadastrados.

## Plano de testes (simples e sequencial)
Objetivo: validar cada passo do CRUD com testes maduros e evolutivos.

### Estrutura sugerida
```text
src/
├─ test/
│  ├─ java/com/docker/api/produto/
│  │  └─ ProdutoCrudFluxoTest.java
│  └─ resources/
│     ├─ application-test.properties
│     └─ data-test.sql
```

### Seeds para testes
- Usar seeds apenas no ambiente de teste.
- `data-test.sql` deve inserir dados controlados para os cenarios.
- Perfil `test` com H2 em memoria para execucao rapida e isolada.

### Ordem dos testes (passos)
1. Cadastrar produto
2. Listar produtos
3. Buscar produto por id (proxima etapa)
4. Atualizar produto (proxima etapa)
5. Deletar produto (proxima etapa)

### Como contar passos que deram certo
- A contagem oficial deve vir do relatorio JUnit/Surefire (`tests run`, `failures`, `errors`).
- Nao usar contador manual no codigo de producao.

## Troubleshooting rapido
- Erro `failed to solve: lstat /target: no such file or directory`:
  - O Dockerfile antigo tentava copiar `target/*.jar` do host.
  - Este projeto agora compila o `.jar` dentro do container (multi-stage), evitando esse erro.
- Se a porta 8080 estiver ocupada:
  - Altere para outra porta no `docker-compose.yml`, exemplo: `"8081:8080"`.
