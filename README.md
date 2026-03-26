# Gerenciador de Ambientes UEPB — Server

Backend da plataforma de **gerenciamento e reserva de ambientes físicos** da Universidade Estadual da Paraíba (UEPB). Permite que usuários solicitem a reserva de salas, laboratórios e outros espaços, com fluxo de aprovação por administradores.

---

## 🚀 Tecnologias

| Tecnologia          | Versão     | Finalidade                                       |
| ------------------- | ---------- | ------------------------------------------------ |
| Java                | 21         | Linguagem principal                              |
| Spring Boot         | 4.x        | Framework web e IoC                              |
| Spring Security     | (via Boot) | Autenticação e autorização                       |
| Spring Data MongoDB | (via Boot) | Persistência de dados                            |
| MongoDB             | 6+         | Banco de dados NoSQL                             |
| Auth0 Java JWT      | 4.5.0      | Geração e validação de tokens JWT                |
| SpringDoc OpenAPI   | 2.5.0      | Documentação interativa (Swagger UI)             |
| Lombok              | (via Boot) | Redução de código boilerplate                    |
| Hibernate Validator | (via Boot) | Validação de dados de entrada                    |
| dotenv-java         | 3.0.0      | Carregamento de variáveis de ambiente via `.env` |
| Maven               | 3.x        | Gerenciador de build e dependências              |

---

## 📐 Arquitetura

```
src/
└── main/
    ├── java/dev/uepb/gereciador/ambientes/
    │   ├── AmbientesApplication.java     # Classe principal
    │   ├── config/                       # Configurações (Security, JWT, MongoDB, OpenAPI)
    │   ├── controller/                   # Controllers REST (AuthController, EnvironmentController, ...)
    │   ├── dto/                          # Data Transfer Objects (request e response)
    │   ├── entity/                       # Entidades MongoDB (User, Environment, Reserve, Role)
    │   ├── enums/                        # Enumerações (UserRole, ReserveStatus)
    │   ├── repository/                   # Interfaces MongoRepository
    │   ├── seeder/                       # Seeder de dados iniciais (roles)
    │   └── service/                      # Lógica de negócio
    └── resources/
        ├── application.properties        # Configuração base
        ├── application-dev.properties    # Configuração de desenvolvimento
        └── application-test.properties   # Configuração de testes
```

---

## 🔐 Autenticação

A API utiliza **JWT (JSON Web Token)** via o esquema Bearer. O token é obtido no endpoint `POST /auth/login` e deve ser enviado em todas as requisições protegidas no header:

```
Authorization: Bearer {token}
```

O token expira em **4 horas** a partir da emissão.

### Perfis de acesso

| Perfil  | Permissões                                          |
| ------- | --------------------------------------------------- |
| `USER`  | Realizar e consultar reservas próprias              |
| `ADMIN` | Gerenciar ambientes, listar todos os usuários       |
| `OWNER` | Acesso total — incluindo criação de administradores |

Os papéis são criados automaticamente pelo `RoleSeeder` na inicialização da aplicação.

---

## 🗓️ Regras de Negócio — Reservas

- Reservas só podem ser feitas para datas **presentes ou futuras**
- Cada slot tem duração exata de **1 hora** e deve começar em **hora cheia** (ex.: 09:00, 14:00)
- Horário de funcionamento permitido: **08:00 às 22:00**
- Não é possível reservar horários já passados no dia atual
- **Não pode haver sobreposição** de slots para o mesmo ambiente e data
- O status inicial de toda reserva é `PENDING` (aguarda aprovação)

---

## 📖 Documentação da API (Swagger UI)

Com a aplicação em execução, acesse a documentação interativa em:

> **http://localhost:8080/swagger-ui/index.html**

A especificação OpenAPI (JSON) está disponível em:

> **http://localhost:8080/v3/api-docs**

### Endpoints disponíveis

#### Autenticação (`/auth`)

| Método | Rota             | Descrição                           | Acesso  |
| ------ | ---------------- | ----------------------------------- | ------- |
| `POST` | `/auth/login`    | Autentica e retorna token JWT       | Público |
| `POST` | `/auth/register` | Registra novo usuário (perfil USER) | Público |

#### Ambientes (`/environments`)

| Método | Rota                 | Descrição                | Acesso      |
| ------ | -------------------- | ------------------------ | ----------- |
| `GET`  | `/environments`      | Lista todos os ambientes | Autenticado |
| `POST` | `/environments`      | Cria novo ambiente       | Autenticado |
| `PUT`  | `/environments/{id}` | Atualiza um ambiente     | Autenticado |

#### Reservas (`/reserves`)

| Método | Rota                              | Descrição                           | Acesso      |
| ------ | --------------------------------- | ----------------------------------- | ----------- |
| `POST` | `/reserves`                       | Cria nova reserva (status: PENDING) | Autenticado |
| `GET`  | `/reserves/{environmentId}?date=` | Lista slots disponíveis por dia     | Autenticado |

#### Usuários (`/person`)

| Método | Rota                   | Descrição                    | Acesso        |
| ------ | ---------------------- | ---------------------------- | ------------- |
| `GET`  | `/person/me`           | Dados do usuário autenticado | Autenticado   |
| `GET`  | `/person/list`         | Lista todos os usuários      | ADMIN / OWNER |
| `POST` | `/person/create-admin` | Cria novo administrador      | OWNER         |

---

## 🛠️ Como Executar

### Pré-requisitos

- **Java 21** (JDK)
- **Maven** (ou use o wrapper `./mvnw` incluso)
- **MongoDB** em execução local ou acesso a um cluster (ex.: [MongoDB Atlas](https://www.mongodb.com/atlas))

### 1. Clonar o repositório

```bash
git clone https://github.com/YuriMont/gerenciador-de-ambientes-uepb-server.git
cd gerenciador-de-ambientes-uepb-server
```

### 2. Configurar variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
# URI de conexão com o MongoDB
# Local:
MONGODB_URI=mongodb://localhost:27017/ambientes

# MongoDB Atlas (produção):
# MONGODB_URI=mongodb+srv://<usuario>:<senha>@<cluster>.mongodb.net/ambientes

# Chave secreta para assinatura dos tokens JWT (use uma string longa e aleatória)
SECRET=sua-chave-secreta-super-segura-aqui

# Perfil ativo da aplicação
APP_PROFILE=dev
```

> **⚠️ Atenção:** Nunca commite o arquivo `.env` com credenciais reais. Ele já está incluído no `.gitignore`.

### 3. Executar a aplicação

```bash
./mvnw spring-boot:run
```

Ou com Maven instalado globalmente:

```bash
mvn spring-boot:run
```

### 4. Executar os testes

```bash
./mvnw test
```

Os testes utilizam uma URI MongoDB configurada em `application-test.properties` (padrão: `mongodb://localhost:27017/ambientes_test`).

### 5. Acessar a API

A aplicação estará disponível em: **http://localhost:8080**

---

## 🗂️ Coleções MongoDB

| Coleção        | Entidade      | Descrição                             |
| -------------- | ------------- | ------------------------------------- |
| `users`        | `User`        | Usuários cadastrados                  |
| `roles`        | `Role`        | Perfis de acesso (USER, ADMIN, OWNER) |
| `environments` | `Environment` | Ambientes físicos disponíveis         |
| `reserves`     | `Reserve`     | Reservas realizadas                   |

---

## 🧪 Exemplo de uso rápido

### 1. Registrar usuário

```http
POST /auth/register
Content-Type: application/json

{
  "name": "João da Silva",
  "email": "joao@uepb.edu.br",
  "password": "senha@123"
}
```

### 2. Fazer login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "joao@uepb.edu.br",
  "password": "senha@123"
}
```

Resposta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. Criar reserva

```http
POST /reserves
Authorization: Bearer {token}
Content-Type: application/json

{
  "date": "2026-04-15",
  "environmentId": "664f1a2b3c4d5e6f7a8b9c0d",
  "numberOfParticipants": 25,
  "justification": "Aula prática de Redes de Computadores",
  "slots": [
    { "startTime": "09:00", "endTime": "10:00" },
    { "startTime": "10:00", "endTime": "11:00" }
  ]
}
```

### 4. Consultar slots disponíveis

```http
GET /reserves/664f1a2b3c4d5e6f7a8b9c0d?date=2026-04-15
Authorization: Bearer {token}
```

---

## 📝 Observações de desenvolvimento

- O projeto usa o perfil `dev` por padrão. Configure `APP_PROFILE=test` para usar as propriedades de teste.
- O `RoleSeeder` é executado na inicialização e garante que os papéis `USER`, `ADMIN` e `OWNER` existam no banco.
- A auditoria automática de campos `createdAt` e `updatedAt` é habilitada via `@EnableMongoAuditing`.
- O Spring Boot DevTools está incluído para hot-reload em desenvolvimento.
