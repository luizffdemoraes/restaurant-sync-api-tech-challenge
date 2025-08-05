# language: pt
Funcionalidade: Gerenciamento de Usuários

  Cenário: Criar um novo usuário admin
    Dado que eu tenho os dados do usuário admin Jack Ryan
    Quando eu envio uma requisição POST para "/v1/users" com os dados do usuário
    Então a resposta deve ter status 201 ou 422 se usuário já existir
    E o corpo da resposta deve conter os dados do usuário criado

  Cenário: Criar um novo usuário client
    Dado que eu tenho os dados do usuário client John Doe
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

  Cenário: Atualizar usuário existente
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    E eu tenho os dados atualizados do usuário para ID
    Quando eu envio uma requisição PUT para "/v1/users/2" com os dados do usuário
    Então a resposta deve ter status 200
    E o corpo da resposta deve conter os dados do usuário atualizado

  Cenário: Atualizar senha do usuário existente
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    Quando eu envio uma requisição PATCH para "/v1/users/2/password" com a nova senha "password321"
    Então a resposta deve ter status 204

  Cenário: Deletar usuário existente
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    Quando eu envio uma requisição DELETE para "/v1/users/2"
    Então a resposta deve ter status 204