# Gerenciador de Ambientes UEPB — Server

Backend da plataforma de **gerenciamento e reserva de ambientes físicos** da Universidade Estadual da Paraíba (UEPB). Usuários solicitam a reserva de salas, laboratórios e auditórios; administradores aprovam ou recusam.

> Este é o app `apps/server/` do monorepo. Para visão geral, frontend e protótipo, veja o [README da raiz](../../README.md).

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
apps/server/
├── src/
│   ├── main/
│   │   ├── java/dev/uepb/gereciador/ambientes/
│   │   │   ├── AmbientesApplication.java     # Classe principal
│   │   │   ├── config/                       # Security, JWT, CORS, Mongo, OpenAPI
│   │   │   ├── controller/                   # Auth, Environment, Reserve, Person
│   │   │   ├── dto/                          # resquest/ (entrada) e response/ (saída)
│   │   │   ├── entity/                       # User, Environment, Reserve, Role
│   │   │   ├── enums/                        # UserRole, ReserveStatus
│   │   │   ├── repository/                   # Interfaces MongoRepository
│   │   │   ├── seeder/                       # RoleSeeder (papéis iniciais)
│   │   │   └── service/                      # Lógica de negócio
│   │   └── resources/
│   │       ├── application.properties        # Configuração base
│   │       ├── application-dev.properties    # Perfil de desenvolvimento
│   │       └── application-test.properties   # Perfil de testes
│   └── test/java/.../ambientes/              # EnvironmentServiceTest, PersonServiceTest, AmbientesApplicationTests
├── .env.example
├── client.http                               # Requisições de exemplo (REST Client do VS Code)
└── Dockerfile                                # Build multistage (JRE 21), usado no Render
```

O pacote é `dev.uepb.gereciador.ambientes` e os DTOs de entrada ficam em `dto/resquest/` — os dois typos são históricos e **não devem ser corrigidos**, sob pena de quebrar imports em todo o projeto.

---

## 🔐 Autenticação

A API usa **JWT** no esquema Bearer. O token vem de `POST /auth/login` e deve ir em toda requisição protegida:

```
Authorization: Bearer {token}
```

O token expira em **4 horas**.

Pela `SecurityConfig`, só são públicos `POST /auth/**` e as rotas do Swagger (`/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`). Todo o resto exige autenticação.

### Perfis de acesso

| Perfil  | Permissões pretendidas                              |
| ------- | --------------------------------------------------- |
| `USER`  | Realizar e consultar reservas próprias              |
| `ADMIN` | Gerenciar ambientes, listar todos os usuários       |
| `OWNER` | Acesso total — incluindo criação de administradores |

Os papéis são criados automaticamente pelo `RoleSeeder` na inicialização.

> ⚠️ **A tabela acima é a intenção, não o estado do código.** Hoje só o `PersonController` tem `@PreAuthorize`. O `EnvironmentController` não tem nenhum, então **qualquer usuário autenticado pode criar, editar e apagar ambientes** — inclusive um `USER`. Falta `@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_OWNER')")` nos métodos de escrita.

---

## 🗓️ Regras de Negócio — Reservas

Implementadas em `ReserveService`:

- Reservas só para datas **presentes ou futuras**
- Cada slot dura exatamente **1 hora** e começa em **hora cheia** (09:00, 14:00…)
- Janela de funcionamento: **08:00 às 22:00** — 14 slots por dia
- No dia de hoje, horários que já passaram são recusados
- **Sem sobreposição** de slots para o mesmo ambiente e data → `409 Conflict`
- Status inicial de toda reserva: `PENDING`

Violação de data ou de formato de slot devolve `400 Bad Request` com a mensagem `"Dia inválido"` ou `"Slot inválido"`.

> `ReserveStatus` já define `APPROVED` e `REJECTED`, mas **ainda não existe endpoint para mudar o status**. O fluxo de aprovação está no roadmap.

---

## 📖 Documentação da API (Swagger UI)

Com a aplicação em execução:

> **http://localhost:8080/swagger-ui.html** (caminho definido por `springdoc.swagger-ui.path`)

Especificação OpenAPI em JSON:

> **http://localhost:8080/v3/api-docs**

### Endpoints disponíveis

#### Autenticação (`/auth`)

| Método | Rota             | Descrição                           | Acesso  |
| ------ | ---------------- | ----------------------------------- | ------- |
| `POST` | `/auth/login`    | Autentica e retorna token JWT       | Público |
| `POST` | `/auth/register` | Registra novo usuário (perfil USER) | Público |

#### Ambientes (`/environments`)

| Método   | Rota                 | Descrição                  | Acesso        |
| -------- | -------------------- | -------------------------- | ------------- |
| `GET`    | `/environments`      | Lista todos os ambientes   | Autenticado   |
| `GET`    | `/environments/{id}` | Busca ambiente pelo ID     | Autenticado   |
| `POST`   | `/environments`      | Cria novo ambiente         | Autenticado ⚠️ |
| `PUT`    | `/environments/{id}` | Atualiza um ambiente       | Autenticado ⚠️ |
| `DELETE` | `/environments/{id}` | Deleta um ambiente pelo ID | Autenticado ⚠️ |

⚠️ Sem checagem de papel — veja a nota em [Perfis de acesso](#perfis-de-acesso).

#### Reservas (`/reserves`)

| Método | Rota                              | Descrição                                | Acesso      |
| ------ | --------------------------------- | ---------------------------------------- | ----------- |
| `POST` | `/reserves`                       | Cria nova reserva (status: PENDING)      | Autenticado |
| `GET`  | `/reserves/{environmentId}?date=` | Lista os slots **livres** naquele dia    | Autenticado |

O `GET` devolve os 14 slots possíveis do dia menos os já reservados — ou seja, o que ainda dá para pedir.

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
- **Maven** (ou o wrapper `./mvnw` incluso)
- **MongoDB** local ou um cluster ([MongoDB Atlas](https://www.mongodb.com/atlas))

### 1. Clonar o repositório

```bash
git clone https://github.com/YuriMont/gerenciador-de-ambientes-uepb-server.git
cd gerenciador-de-ambientes-uepb-server/apps/server
```

### 2. Configurar variáveis de ambiente

O `.env` fica em **`apps/server/.env`** — não na raiz do monorepo. Ele é lido de duas formas (`spring.config.import` e `dotenv-java`), e as duas dependem do diretório de trabalho ser `apps/server/`.

```bash
cp .env.example .env
```

```env
# Chave secreta para assinatura dos tokens JWT (string longa e aleatória)
SECRET=sua-chave-secreta-super-segura

# URL pública da API, exibida no Swagger
API_SERVER_URL=http://localhost:8080

# Perfil ativo: dev | test
APP_PROFILE=dev

# URI de conexão com o MongoDB
# Local (sem auth): mongodb://localhost:27017/ambientes
# Local (com auth): mongodb://usuario:senha@localhost:27017/ambientes?authSource=admin
# Atlas:            mongodb+srv://<usuario>:<senha>@<cluster>.mongodb.net/ambientes
MONGODB_URI=mongodb://localhost:27017/ambientes
```

> **⚠️ Nunca commite o `.env` com credenciais reais.** Ele já está no `.gitignore`.

### 3. Executar a aplicação

```bash
./mvnw spring-boot:run     # a partir de apps/server/
```

Ou, para subir backend e frontend juntos, `npm run dev` na raiz do monorepo.

### 4. Executar os testes

```bash
./mvnw test
```

- `EnvironmentServiceTest` e `PersonServiceTest` — unitários com Mockito, **não precisam de banco**.
- `AmbientesApplicationTests` — `@SpringBootTest`, sobe o contexto e **precisa de um MongoDB acessível** na URI de `application-test.properties` (padrão `mongodb://localhost:27017/ambientes_test`). Se o Mongo local exigir autenticação, a URI precisa das credenciais ou o carregamento do contexto falha.

### 5. Build do JAR

```bash
./mvnw clean package -DskipTests   # mesmo comando usado pelo Dockerfile
```

### 6. Acessar

A aplicação sobe em **http://localhost:8080**.

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

O arquivo `client.http` traz essas mesmas chamadas prontas para o REST Client do VS Code.

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

### 3. Consultar slots disponíveis

```http
GET /reserves/664f1a2b3c4d5e6f7a8b9c0d?date=2026-04-15
Authorization: Bearer {token}
```

### 4. Criar reserva

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

---

## 📝 Convenções e observações

- **Formatação:** Google Java Style, definida em `.vscode/settings.json` (formatter `eclipse`), com `formatOnSave` e organização automática de imports.
- **Idioma:** código, Javadoc, mensagens e respostas em **PT-BR**. Toda classe pública tem Javadoc.
- **Lombok:** `@Getter`/`@Setter` nas entidades, `record` nos DTOs com Bean Validation (`@Valid`).
- O perfil `dev` é o padrão; use `APP_PROFILE=test` para as propriedades de teste.
- `@EnableMongoAuditing` preenche `createdAt` e `updatedAt` automaticamente.
- O Spring Boot DevTools está incluído: salvar um `.java` no VS Code recompila e reinicia sozinho.
- **CORS** (`CorsConfig.java`) libera `http://localhost:5173` (Vite), `http://localhost:3000` e a URL do Render.
