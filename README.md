#  Tech Challenge 1ª Fase - **restaurant-sync**

## ÍNDICE

* [Descrição do Projeto](#descrição-do-projeto)
* [Funcionalidades](#funcionalidades)
* [Tecnologias Utilizadas](#tecnologias-utilizadas)
* [Estrutura do Projeto](#estrutura-do-projeto)
* [Diagrama das tabelas de banco de dados](#diagrama-das-tabelas-de-banco-de-dados)
* [Requisitos](#requisitos)
* [Como Rodar o Projeto](#como-rodar-o-projeto)
* [Endpoints](#endpoints)
* [Documentação da API](#documentação-da-api)
* [Cobertura de código](#cobertura-de-código)
* [Collection POSTMAN](#collection-postman)
* [Environment POSTMAN](#environment-postman)

## Descrição do Projeto

API para gerenciamento de usuários de restaurantes, desenvolvida como parte da **Fase 1** do **Tech Challenge FIAP**. Esta fase foca na implementação das funcionalidades essenciais para o gerenciamento de usuários, permitindo o cadastro, atualização, exclusão e validação de login de dois tipos de usuários: **donos de restaurante** e **clientes**.

O sistema é construído com **Spring Boot**, **Docker**, e **Docker Compose**, sendo integrado a um banco de dados relacional **PostgreSQL** e **H2** para testes.

## Funcionalidades

A API oferece as seguintes funcionalidades para o gerenciamento de usuários:

* **Cadastro de Usuário**: Permite a criação de novos usuários no sistema, sejam eles donos de restaurante ou clientes.
* **Busca de Usuário por ID**: Habilita a recuperação de informações detalhadas de um usuário específico utilizando seu identificador.
* **Listagem de Usuários (Paginada)**: Oferece a capacidade de listar todos os usuários registrados no sistema, com suporte a paginação para otimização de desempenho. Esta funcionalidade é tipicamente restrita a administradores.
* **Alteração de Dados do Usuário**: Permite a atualização das informações de um usuário existente, como nome, email e endereço.
* **Troca de Senha**: Funcionalidade específica para a alteração segura da senha de um usuário.
* **Exclusão de Usuário**: Possibilita a remoção permanente de um usuário do sistema. Esta funcionalidade é geralmente restrita a administradores.
* **Validação de Login (Autenticação)**: Realiza a verificação das credenciais de login para autenticar o usuário e conceder acesso ao sistema, geralmente retornando um token de autenticação.

## Tecnologias Utilizadas

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2_Database-0F4B8D?style=for-the-badge&logo=h2-database&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white)
![DBeaver](https://img.shields.io/badge/DBeaver-37226C?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjQ0IiBoZWlnaHQ9IjY0NCIgdmlld0JveD0iMCAwIDY0NCA2NDQiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CiAgPGNpcmNsZSBjeD0iMzIyIiBjeT0iMzIyIiByPSIzMjIiIGZpbGw9IiMzNzIyNkMiIC8+CiAgPHRleHQgeD0iMzIyIiB5PSIzMzAiIGZvbnQtc2l6ZT0iMjQwIiBmb250LXdlaWdodD0iYm9sZCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZmlsbD0id2hpdGUiPkRCPC90ZXh0Pgo8L3N2Zz4=)
![Flyway](https://img.shields.io/badge/Flyway-Database_Migrations-red?logo=flyway)

## Estrutura do Projeto

A aplicação segue uma arquitetura em camadas bem definida, visando modularidade e manutenibilidade. Abaixo está a estrutura principal de pastas:

```
├── collection                   // Collections do Postman para teste dos endpoints da API
├── src
│   ├── main
│   │   ├── java
│   │   │   └── br
│   │   │       └── com
│   │   │           └── fiap
│   │   │               └── postech
│   │   │                   └── restaurantsync
│   │   │                       ├── config           // Configurações gerais e de segurança
│   │   │                       │   ├── doc
│   │   │                       │   └── security
│   │   │                       │       └── customgrant
│   │   │                       ├── controllers      // Controllers REST
│   │   │                       ├── dtos             // DTOs de requisição e resposta
│   │   │                       │   ├── requests
│   │   │                       │   └── responses
│   │   │                       ├── entities         // Entidades JPA
│   │   │                       ├── repositories     // Repositórios Spring Data JPA
│   │   │                       ├── resources        // Exceções, tradutores, validações
│   │   │                       │   ├── exceptions
│   │   │                       │   │   └── handler
│   │   │                       │   ├── translator
│   │   │                       │   │   └── impl
│   │   │                       │   └── validations
│   │   │                       └── services         // Serviços (lógica de negócio)
│   │   └── resources            // Arquivos de configuração application.properties, db/migration, static, templates etc.
│   │       ├── db
│   │       │   └── migration
│   │       ├── static
│   │       └── templates
│   └── test
│       ├── java
│       │   └── br
│       │       └── com
│       │           └── fiap
│       │               └── postech
│       │                   └── restaurantsync
│       │                       ├── controllers
│       │                       ├── factories
│       │                       ├── resources
│       │                       │   └── exceptions
│       │                       │       └── handler
│       │                       └── services
│       └── resources            // Configurações e recursos para testes
└── target                       // Artefatos gerados pelo build (classes, jar, relatórios, etc.)
```

## Diagrama das tabelas de banco de dados

![Diagrama de banco de dados](images/diagrama-db.png)

## Requisitos

Para rodar via Docker, certifique-se de ter instalado e configurado: 

- [Git](https://git-scm.com/): necessário para clonar o repositório do projeto.
- [Java 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html): necessário para compilar e executar o projeto.
- [Maven](https://maven.apache.org/): utilizado para compilar o projeto e gerar o arquivo `.jar` que será empacotado e executado no container.
- [Docker Desktop](https://www.docker.com/): necessário para criar e executar os containers da aplicação e do banco de dados.
- [Postman](https://www.postman.com/): recomendado para testar os endpoints da API de forma interativa.

Para executar o projeto manualmente na sua máquina (sem Docker), você precisará de:

- Java 21 ou superior instalado.
- Maven instalado para compilar e construir o projeto.
- Altere o arquivo `application.properties` para utilizar o banco H2.

Utilize a configuração presente no seguinte arquivo:

```properties
src\test\resources\application-test.properties
```

Comentar a linha presente no arquivo `ResourceServerConfig.java`:

```properties
  @Profile("test")
```

## Como Rodar o Projeto

Antes de iniciar, **certifique-se que todos os requisitos descritos na seção anterior estão atendidos** (Java, Maven e Docker instalados e configurados).

1. **Clone o repositório:**
   ```bash 
   git clone https://github.com/luizffdemoraes/restaurant-sync-api-tech-challenge
   ```

2. **Realize o build do projeto:**
   ```bash
   docker compose build --no-cache
   ```
   
3. **Inicie os containers:**
   ```bash
   docker compose up
   ```

4.  A API estará disponível em `http://localhost:8080`.

## Endpoints

- **POST   `/v1/usuarios`**: Cria um novo usuário.
- **GET    `/v1/usuarios`**: Lista usuários (paginado, apenas para admin).
- **GET    `/v1/usuarios/{id}`**: Busca usuário por ID.
- **PUT    `/v1/usuarios/{id}`**: Atualiza as informações de um usuário.
- **PATCH  `/v1/usuarios/{id}/senha`**: Troca a senha de um usuário.
- **DELETE `/v1/usuarios/{id}`**: Exclui um usuário (apenas para admin).
- **POST   `/oauth2/token`**: Valida o login de um usuário.

> ⚠️ **Importante:**  
> Antes de utilizar a API para as demais funcionalidades, o usuário **precisa estar cadastrado** (`POST /v1/usuarios`) e **autenticado** (`POST /oauth2/token`) para obter o **token de acesso**.  
> Esse token deve ser incluído no cabeçalho `Authorization` das requisições aos endpoints protegidos:  
> `Authorization: Bearer <token>`
>
> Além disso, **algumas operações são restritas a administradores**, como listar ou excluir usuários.

## Documentação da API

A documentação completa da API está disponível através do Swagger UI em:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

> ⚠️ **Importante:**
> Para acessar a documentação interativa da API via Swagger UI e testar os endpoints, você precisará das seguintes credenciais de acesso:
>
> **Username:** myclientid<br>
> **Password:** myclientsecret
>
> ![swagger](images/swagger.png)

## Cobertura de código

A cobertura de código do projeto é gerada utilizando o JaCoCo (Java Code Coverage). O processo é automatizado através do Maven.

Gerando a cobertura de código
Execute os seguintes comandos no terminal, a partir da raiz do projeto:

Executar os testes e coletar os dados de cobertura:

```bash
mvn clean test
```

- Este comando executa todos os testes automatizados do projeto.
- Durante a execução, o JaCoCo coleta os dados necessários para análise de cobertura.

Gerar os relatórios de cobertura:

```bash
mvn jacoco:report
```

- Este comando processa os dados coletados e gera os relatórios nos formatos HTML/XML/CSV.

### Acessando o relatório
O relatório gerado pode ser visualizado abrindo o arquivo:

```
target/site/jacoco/index.html
```


### Exemplo da cobertura gerada:

![restaurant-sync-coverage](images/restaurant-sysc-coverage.png)

## Collection POSTMAN

https://github.com/luizffdemoraes/restaurant-sync-api-tech-challenge/blob/main/collection/restaurant-sync-fiap.postman_collection.json

## Environment POSTMAN

https://github.com/luizffdemoraes/restaurant-sync-api-tech-challenge/blob/main/collection/restaurant-sync-fiap.postman_environment.json
