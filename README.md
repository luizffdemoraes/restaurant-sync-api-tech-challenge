#  Tech Challenge 1ª Fase - **restaurant-sync**

## ÍNDICE

* [Descrição do Projeto](#descricaoDoProjeto)

## Descrição do Projeto

API para gerenciamento de usuários de restaurantes, desenvolvida como parte da **Fase 1** do **Tech Challenge FIAP**. Esta fase foca na implementação das funcionalidades essenciais para o gerenciamento de usuários, permitindo o cadastro, atualização, exclusão e validação de login de dois tipos de usuários: **donos de restaurante** e **clientes**.

O sistema é construído com **Spring Boot**, **Docker**, e **Docker Compose**, sendo integrado a um banco de dados relacional **PostgreSQL** e **H2** para testes.

## Funcionalidades

- **Cadastro de usuário**: Criação de um novo usuário (dono de restaurante ou cliente).
- **Alteração de dados**: Atualização das informações do usuário.
- **Exclusão de usuário**: Remoção de um usuário.
- **Troca de senha**: Funcionalidade para alteração da senha de um usuário.
- **Validação de login**: Verificação de credenciais de login para acesso ao sistema.

## Tecnologias Utilizadas

- **Spring Boot**: Framework principal para desenvolvimento da API.
- **Docker & Docker Compose**: Utilizados para orquestrar a aplicação e o banco de dados, garantindo portabilidade e escalabilidade.
- **Banco de dados relacional**: Suporte a **PostgreSQL** e **H2** para testes.
- **Postman** (ou ferramenta similar): Para testar os endpoints da API.

## Requisitos

Antes de rodar o projeto localmente, você precisará de:

- **Java 21** ou superior.
- **Docker** e **Docker Compose** instalados.

## Como Rodar o Projeto

1. Clone o repositório:

    ```bash
    git clone https://github.com/luizffdemoraes/restaurant-sync-api-tech-challenge
    ```

2. Configure o banco de dados no arquivo `application.properties` (se necessário).

3. Inicie os containers Docker com o seguinte comando:

    ```bash
    docker compose up
    ```

    Isso iniciará a aplicação Spring Boot e o banco de dados.

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

## Testes

Para testar a API, utilize o **Postman** ou ferramenta similar. Inclua os testes dos endpoints no formato de **collections** para facilitar o processo de verificação.

## Estrutura do Projeto

- **src/main/java**: Código-fonte da aplicação.
- **src/main/resources**: Arquivos de configuração e templates.
- **docker-compose.yml**: Arquivo para orquestrar a aplicação e o banco de dados via Docker.
- **README.md**: Este arquivo.

## Collection POSTMAN
