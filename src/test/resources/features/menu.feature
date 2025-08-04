# language: pt
Funcionalidade: Gerenciamento de Menus

  Cenário: Criar um novo item de menu
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    E o restaurante "Fogo de Chão" está cadastrado
    E eu tenho os dados do item de menu "Feijoada Pequena"
    Quando eu envio uma requisição POST para "/v1/menus" com os dados do menu
    Então a resposta do menu deve ter status 201
    E o corpo da resposta deve conter os dados do menu criado

  Cenário: Listar itens de menu paginados
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    Quando eu consulto a lista de menus em "/v1/menus?page=0&size=10"
    Então a resposta do menu deve ter status 200
    E o corpo da resposta deve conter a lista de itens de menu

  Cenário: Consultar item de menu por ID
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    Quando eu consulto o item de menu com ID 1 em "/v1/menus/{id}"
    Então a resposta do menu deve ter status 200
    E o corpo da resposta deve conter os dados do item de menu consultado

  Cenário: Atualizar item de menu existente
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    E eu tenho os dados atualizados do item de menu
    Quando eu envio uma requisição PUT para "/v1/menus/{id}" com os dados atualizados do menu
    Então a resposta do menu deve ter status 200
    E o corpo da resposta deve conter os dados do menu atualizado

  Cenário: Atualizar disponibilidade do item de menu apenas no restaurante
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    E o item de menu "Feijoada Pequena" está cadastrado
    Quando eu envio uma requisição PATCH para "/v1/menus/1/restaurant-only" com availableOnlyRestaurant false
    Então a resposta do menu deve ter status 200
    E o corpo da resposta deve conter "availableOnlyRestaurant" igual a false

  Cenário: Deletar item de menu existente
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    E o item de menu "Feijoada Pequena" está cadastrado
    Quando eu envio uma requisição DELETE menu para "/v1/menus/{id}"
    Então a resposta do menu deve ter status 204
    