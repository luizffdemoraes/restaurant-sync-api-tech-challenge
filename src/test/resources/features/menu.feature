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