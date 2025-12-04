💻 API de Gestão de Estoque com Módulo de Vendas
— Desenvolvida em Spring Boot

Este projeto consiste em uma API REST para gerenciamento de estoque, agora ampliada com um módulo completo de Vendas, capaz de controlar automaticamente a baixa de produtos, garantir transações consistentes e evitar inconsistências relacionadas ao estoque.

📌 Visão Geral

A API fornece recursos para:

Cadastro e gerenciamento de Clientes

Registro de Vendas

Controle de ItensVenda

Baixa automática do estoque ao concluir uma venda

Rollback em caso de falhas (ex.: estoque insuficiente)

Integração completa entre Cliente → Venda → Itens → Produto

O sistema assegura que nenhuma venda seja registrada caso a quantidade solicitada ultrapasse o estoque disponível.

🚀 Requisitos

Para rodar o projeto, instale:

Java 21

Maven

PostgreSQL, MySQL ou XAMPP

⚙️ Configuração
1️⃣ Clonar o Repositório
git clone https://github.com/Bufonn/ControleEstoque-20240447.git
cd ControleEstoque-20240447

2️⃣ Configurar o Banco de Dados

Edite:

src/main/resources/application.properties


Exemplo:

spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_seu_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

3️⃣ Executar a Aplicação
mvn clean install
mvn spring-boot:run


A API será iniciada em:

http://localhost:8080

🧭 Endpoints

Todos seguem o padrão:

/api/{recurso}

🔹 1. Clientes (CRUD)
Método	Endpoint	Descrição
POST	/api/clientes	Criar novo cliente
GET	/api/clientes/{id}	Buscar cliente por ID
GET	/api/clientes	Listar clientes
PUT	/api/clientes/{id}	Atualizar cliente
DELETE	/api/clientes/{id}	Remover cliente

📥 Exemplo de Payload

{
  "nome": "Maria Silva",
  "email": "maria.silva@exemplo.com"
}

🔹 2. Vendas
Endpoint principal:
Método	Endpoint	Descrição
POST	/api/vendas	Registra venda e realiza baixa de estoque

📥 Exemplo (exige produtos, fornecedores e categorias cadastrados):

{
  "clienteId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2,
      "precoUnitario": 129.99
    },
    {
      "produtoId": 3,
      "quantidade": 1,
      "precoUnitario": 2499.99
    }
  ]
}

🔹 3. Produtos
Método	Endpoint	Descrição
POST	/api/produtos	Registrar novo produto

📥 Exemplo:

{
  "nome": "Mouse Gamer RGB",
  "preco": 159.90,
  "categoria": { "id": 2 },
  "fornecedores": [
    { "id": 1 }
  ],
  "estoque": {
    "quantidade": 25
  }
}

🔹 4. Categorias e Fornecedores
Método	Endpoint	Descrição
POST	/api/categorias	Criar categoria
GET	/api/categorias	Listar todas
GET	/api/categorias/{id}	Buscar por ID
PUT	/api/categorias/{id}	Atualizar categoria
DELETE	/api/categorias/{id}	Remover categoria
POST	/api/fornecedores	Criar fornecedor
GET	/api/fornecedores	Listar fornecedores
GET	/api/fornecedores/{id}	Buscar por ID
PUT	/api/fornecedores/{id}	Atualizar fornecedor
DELETE	/api/fornecedores/{id}	Remover fornecedor

📥 Exemplos:

{ "nome": "Categoria X" }

{ "nome": "Fornecedor X" }

🔥 Lógica de Estoque (Transacional)

O processo de venda é executado dentro de uma transação:

@Transactional


Isso garante que todas as operações aconteçam como um único bloco:

✔ Quando tudo ocorre bem:

Estoque suficiente

Venda registrada

Itens descontados corretamente

❌ Quando ocorre erro:

Falta de estoque em qualquer item

Nenhuma alteração é aplicada

Venda não é salva

Estoque permanece intacto

O rollback é acionado automaticamente

Exemplo de erro (HTTP 400):

Estoque insuficiente para o produto X.

📦 Tecnologias Utilizadas

Spring Boot

Spring Data JPA

Spring Web

Maven

Lombok

MySQL Driver

Java 21

Qualquer SGBD compatível (MySQL, PostgreSQL etc.)