# language: pt
Funcionalidade: Gerenciamento de Usuários

  Cenário: Criar um usuário admin com sucesso
    Dado que eu tenho os seguintes dados do usuário admin:
      | campo    | valor                      |
      | name     | Jack Ryan                  |
      | email    | jackryan@restaurantsync.com|
      | login    | jackryan                   |
      | password | password123                |
      | street   | Rua das Flores             |
      | number   | 123                        |
      | city     | São Paulo                  |
      | state    | SP                         |
      | zipCode  | 12345-678                  |
    Quando eu envio uma requisição POST para "/v1/users" com os dados do usuário
    Então a resposta deve ter status 201
    E o corpo da resposta deve conter os dados do usuário criado

  Cenário: Criar um usuário cliente com sucesso
    Dado que eu tenho os seguintes dados do usuário cliente:
      | campo    | valor               |
      | name     | John Doe            |
      | email    | johndoe@example.com |
      | login    | johndoe             |
      | password | password123         |
      | street   | Rua das Flores      |
      | number   | 123                 |
      | city     | São Paulo           |
      | state    | SP                  |
      | zipCode  | 12345-678           |
    Quando eu envio uma requisição POST para "/v1/users" com os dados do usuário
    Então a resposta deve ter status 201
    E o corpo da resposta deve conter os dados do usuário criado