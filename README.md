#  Tech Challenge 1ª Fase - **restaurant-sync**

## ÍNDICE

* [Descrição do Projeto](#descricaoDoProjeto)
* [Funcionalidades](#funcionalidades)
* [Estrutura do Projeto](#estruturaDoProjeto)
* [Tecnologias Utilizadas](#tecnologiasUtilizadas)
* [Requisitos](#requisitos)
* [Como Rodar o Projeto](#comoRodarOProjeto)
* [Endpoints](#endpoints)
* [Documentação da API](#documentacaoDaApi)
* [Testes](#testes)
* [Qualidade do Código e Boas Práticas](#qualidadeDoCodigo)
* [Collection POSTMAN](#collectionPostman)

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

- **Spring Boot**: Framework principal para desenvolvimento da API.
- **Docker & Docker Compose**: Utilizados para orquestrar a aplicação e o banco de dados, garantindo portabilidade e escalabilidade.
- **Banco de dados relacional**: Suporte a **PostgreSQL** e **H2** para testes.
- **Postman** (ou ferramenta similar): Para testar os endpoints da API.

## Estrutura do Projeto

A aplicação segue uma arquitetura em camadas bem definida, visando modularidade e manutenibilidade. Abaixo, a estrutura de pastas principal:

```
├── collection                   // Contém as collections do Postman para teste dos endpoints da API 
├── src
│   ├── main
│   │   ├── java
│   │   │   └── br
│   │   │       └── com
│   │   │           └── fiap
│   │   │               └── postech
│   │   │                   └── restaurantsync
│   │   │                       ├── config           // Configurações gerais da aplicação e de segurança (OpenAPI/Swagger, OAuth2)
│   │   │                       │   ├── doc
│   │   │                       │   └── security
│   │   │                       │       └── customgrant
│   │   │                       ├── controllers      // Camada de Controllers: Responsáveis por expor os endpoints da API REST
│   │   │                       ├── dtos             // Data Transfer Objects: Classes para transferência de dados entre camadas e para as requisições/respostas da API
│   │   │                       │   ├── requests
│   │   │                       │   └── responses
│   │   │                       ├── entities         // Camada de Entidades: Representam os modelos de dados persistidos no banco
│   │   │                       ├── repositories     // Camada de Repositórios: Interfaces para acesso e manipulação de dados no banco (Spring Data JPA)
│   │   │                       ├── resources        // Recursos auxiliares, como classes de exceção, tradutores e validações
│   │   │                       │   ├── exceptions
│   │   │                       │   │   └── handler
│   │   │                       │   ├── translator
│   │   │                       │   │   └── impl
│   │   │                       │   └── validations
│   │   │                       └── services         // Camada de Serviços: Contém a lógica de negócio da aplicação
│   │   └── resources        // Arquivos de configuração, como application.properties e recursos estáticos 
│   │       ├── static
│   │       └── templates
│   └── test                 // Classes de testes unitários e de integração
│       └── java
└── target                   // Diretório gerado pela compilação (classes compiladas, artefatos .jar, etc.)
```

## Requisitos

Antes de rodar o projeto localmente, você precisará de:

- **Java 21** ou superior.
- **Docker** e **Docker Compose** instalados.

## Como Rodar o Projeto

1. **Clone o repositório:**
   ```bash 
   git clone https://github.com/luizffdemoraes/restaurant-sync-api-tech-challenge```
   ```
   
2. Dado a prévia existencia do jar presente na pasta target não é necessário realizar essa etapa:
    ```bash
    ./mvnw clean package -DskipTests
    ```

3.  Realize o build do projeto com o comando:
   ```bash
   docker compose build --no-cache
   ```
   
4. Execute o comando abaixo para iniciar os containers:
   ```bash
   docker compose up
   ```

4. A API estará disponível em `http://localhost:8080`.

## Endpoints

- **POST /v1/usuarios**: Cria um novo usuário.
- **GET /v1/usuarios**: Lista usuários (paginado, apenas para admin).
- **GET /v1/usuarios/{id}**: Busca usuário por ID.
- **PUT /v1/usuarios/{id}**: Atualiza as informações de um usuário.
- **PATCH /v1/usuarios/{id}/senha**: Troca a senha de um usuário.
- **DELETE /v1/usuarios/{id}**: Exclui um usuário (apenas para admin).
- **POST /oauth2/token**: Valida o login de um usuário.

## Documentação da API

A documentação completa da API está disponível através do Swagger UI em:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Cobertura de código

![restaurant-sync-coverage](https://github.com/luizffdemoraes/restaurant-sync-api-tech-challenge/blob/main/images/restaurant-sysc-coverage.png)

## Collection POSTMAN

https://github.com/luizffdemoraes/restaurant-sync-api-tech-challenge/blob/main/collection/Restaurant%20Sync%20-%20FIAP.postman_environment.json

