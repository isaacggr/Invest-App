# InvestApp

API REST para gerenciamento de investimentos, permitindo registrar compras e vendas de ativos e calcular automaticamente a posição da carteira.

O objetivo do projeto é simular um **agregador de investimentos**, semelhante a plataformas como Investidor10, permitindo ao usuário controlar suas posições sem realizar operações reais de compra.

O projeto foi desenvolvido com foco em **boas práticas de desenvolvimento backend em Java**, arquitetura em camadas e validação de regras de negócio.

---

# Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- MySQL
- Docker
- Lombok
- Jakarta Validation
- Maven

---

# Arquitetura do Projeto

O projeto segue uma arquitetura simples baseada em camadas.

```
controller
service
repository
entity
dto
exception
enums
```

### Responsabilidades das camadas

**Controller**
- Recebe requisições HTTP
- Valida dados de entrada
- Retorna respostas da API

**Service**
- Implementa regras de negócio
- Orquestra entidades e repositórios

**Repository**
- Responsável pelo acesso ao banco de dados

**Entity**
- Representação do domínio da aplicação

**DTO**
- Objetos utilizados na comunicação da API

---

# Modelo de Domínio

```
User
 └── Account
       └── Transaction
              └── Stock
```

### Descrição das entidades

**User**
- Representa o usuário do sistema
- Possui uma ou mais carteiras

**Account**
- Carteira de investimentos do usuário

**Transaction**
- Representa operações de compra ou venda de ativos

**Stock**
- Representa o ativo negociado

---

# Funcionalidades

## Usuários

- Criar usuário
- Listar usuários
- Atualizar nome do usuário
- Soft delete de usuário

---

## Carteiras (Accounts)

- Criar carteira vinculada a um usuário
- Listar carteiras de um usuário
- Atualizar nome da carteira
- Soft delete de carteira

---

## Ativos (Stocks)

- Registro automático de ativos ao realizar uma transação
- Normalização automática de ticker

Tipos suportados:

- STOCK
- FII
- CRYPTO

---

## Transações

- Registro de compra e venda de ativos
- Validação de regras de negócio
- Controle de taxas
- Histórico completo de operações

---

## Posição da Carteira

Cálculo automático de:

- Quantidade atual
- Preço médio
- Custo total

Endpoints disponíveis:

- posição por ativo
- posição por ticker
- posição completa da carteira

---

# Regras de Negócio

Algumas validações implementadas:

- Não é possível vender mais ativos do que possui
- Não é possível criar carteiras duplicadas para o mesmo usuário
- Ativos são criados automaticamente ao registrar uma transação
- Usuários e carteiras utilizam **soft delete**
- Validação de dados usando **Jakarta Validation**

---

# Exemplo de Transação

Endpoint

```
POST /api/accounts/{accountId}/transactions
```

Body

```json
{
  "ticker": "PETR4",
  "assetType": "STOCK",
  "operation": "BUY",
  "tradeDate": "2026-03-05",
  "quantity": 10,
  "unitPrice": 32.58,
  "fees": 0,
  "notes": "Compra teste"
}
```

Resposta

- Transação criada
- Ativo retornado
- Posição atualizada da carteira

---

# Endpoints Principais

## Usuários

Criar usuário

```
POST /users
```

Listar usuários

```
GET /users
```

Atualizar nome

```
PATCH /users/{id}/name
```

Deletar usuário

```
DELETE /users/{id}
```

---

## Carteiras

Criar carteira

```
POST /accounts
```

Listar carteiras de um usuário

```
GET /accounts/user/{userId}
```

Atualizar nome da carteira

```
PATCH /accounts/{id}/name
```

Deletar carteira

```
DELETE /accounts/{id}
```

---

## Transações

Criar transação

```
POST /api/accounts/{accountId}/transactions
```

---

## Posição da Carteira

Posição por ativo

```
GET /api/accounts/{accountId}/positions/{stockId}
```

Posição por ticker

```
GET /api/accounts/{accountId}/positions/ticker/{ticker}
```

Carteira completa

```
GET /api/accounts/{accountId}/positions
```

---

# Banco de Dados

O projeto utiliza **MySQL** e pode ser executado utilizando Docker.

Exemplo de configuração:

```
spring.datasource.url=jdbc:mysql://localhost:3307/investapp
spring.datasource.username=finance_user
spring.datasource.password=finance123
```

---

# Como Executar o Projeto

## 1 Clonar o repositório

```
git clone https://github.com/isaacggr/Invest-App.git
```

---

## 2 Subir o banco com Docker

```
docker compose up -d
```

---

## 3 Executar a aplicação

Usando Maven Wrapper

```
./mvnw spring-boot:run
```

ou

```
mvn spring-boot:run
```

---

# Testando a API

A API pode ser testada com:

- Postman
- Insomnia

Fluxo recomendado:

1. Criar usuário
2. Criar carteira
3. Registrar transações
4. Consultar posição da carteira

---

# Melhorias Futuras

- Integração com API de mercado (BRAPI)
- Cálculo de valor de mercado
- Cálculo de lucro/prejuízo
- Autenticação com JWT
- Testes automatizados
- Paginação de resultados
- Cache de consultas

---

# Autor

Isaac Gregório

GitHub  
https://github.com/isaacggr