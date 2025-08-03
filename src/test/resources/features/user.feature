# language: pt
Funcionalidade: Gerenciamento de Usuários Admin

  Cenário: Criar um novo usuário admin
    Dado que eu tenho os dados do usuário admin Jack Ryan
    Quando eu envio uma requisição POST para "/v1/users" com os dados do usuário
    Então a resposta deve ter status 201
    E o corpo da resposta deve conter os dados do usuário criado

  Cenário: Login do usuário admin
    Dado que o usuário admin Jack Ryan está cadastrado
    Quando eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    Então o login deve ser bem sucedido
    E um token de acesso deve ser retornado

  Cenário: Consultar lista de usuários paginada
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    Quando eu consulto a lista de usuários paginada em "/v1/users?page=0&size=10"
    Então a lista de usuários deve ser retornada com status 200
    E o corpo da resposta deve conter a lista de usuários

  Cenário: Consultar usuário por ID
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    Quando eu consulto o usuário com ID 1 em "/v1/users/1"
    Então a resposta deve ter status 200
    E o corpo da resposta deve conter os dados do usuário Jack Ryan