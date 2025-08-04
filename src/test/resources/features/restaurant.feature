# language: pt
Funcionalidade: Gerenciamento de Restaurantes

  Cenário: Criar um novo restaurante
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    E eu tenho os dados do restaurante "Gourmet Bistro"
    Quando eu envio uma requisição POST para "/v1/restaurants" com os dados do restaurante
    Então a resposta do restaurante deve ter status 201
    E o corpo da resposta deve conter os dados do restaurante criado

  Cenário: Listar restaurantes paginados
    Dado que o usuário admin Jack Ryan está cadastrado
    E eu realizo login com email "jackryan@restaurantsync.com" e senha "password123"
    Quando eu consulto a lista de restaurantes em "/v1/restaurants?page=0&size=10"
    Então a resposta de lista de restaurantes deve ter status 200
    E o corpo da resposta deve conter a lista de restaurantes