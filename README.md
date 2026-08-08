# Gerenciador de Ambientes UEPB

Plataforma de **gerenciamento e reserva de ambientes físicos** da Universidade Estadual da Paraíba (UEPB). Usuários solicitam reservas de salas, laboratórios e outros espaços em slots de 1 hora, e administradores aprovam ou rejeitam as solicitações.

> Monorepo contendo o **backend** (`apps/server/`) e o **frontend** (`apps/web/`).

## Stack

| Camada    | Tecnologias                                                            |
| --------- | --------------------------------------------------------------------- |
| Backend   | Java 21, Spring Boot 4.x, Spring Security, Spring Data MongoDB         |
| Frontend  | Vite 8, React 19, TypeScript 6, Tailwind CSS 4                         |
| Banco     | MongoDB 6+                                                             |
| Auth      | JWT (Auth0 `java-jwt`, expiração 4h) + BCrypt                          |
| Docs      | SpringDoc / Swagger UI                                                |
| Deploy    | Docker + Render (back-end)                                           |

## Estrutura do monorepo

```
apps/                 # Aplicações do monorepo
├── server/            # Backend Spring Boot + MongoDB
│   ├── src/main/java/dev/uepb/gereciador/ambientes/
│   │   ├── controller/   # REST: Auth, Environment, Reserve, Person
│   │   ├── service/      # Regras de negócio
│   │   ├── entity/       # User, Role, Environment, Reserve
│   │   ├── repository/   # MongoRepository
│   │   ├── config/       # Security, JWT, CORS, Mongo, OpenAPI
│   │   ├── dto/          # Request/Response
│   │   ├── seeder/       # RoleSeeder (USER, ADMIN, OWNER)
│   │   └── enums/        # UserRole, ReserveStatus
│   ├── .env.example      # Template das variáveis de ambiente
│   ├── Dockerfile        # Build multistage (JRE 21)
│   └── client.http       # Exemplos de requisições (VS Code REST Client)
└── web/                  # Frontend Vite + React + TypeScript
    ├── src/              # Componentes, telas e libs (api, queryClient, utils)
    ├── orval.config.ts   # Geração do client da API (react-query + zod)
    └── vite.config.ts
```

## Requisitos

- **JDK 21**
- **Maven 3.x** (ou use o wrapper `./mvnw`)
- **Node.js >= 20** (somente scripts raiz)
- **MongoDB 6+** local ou Atlas

## Como rodar

### 1. Configure o ambiente

```bash
cp apps/server/.env.example apps/server/.env
```

Edite `apps/server/.env` com os valores reais:

```env
SECRET=chave-longa-aleatoria
API_SERVER_URL=http://localhost:8080
APP_PROFILE=dev
MONGODB_URI=mongodb://usuario:senha@localhost:27017/ambientes?authSource=admin
```

- Sem credenciais locais: `MONGODB_URI=mongodb://localhost:27017/ambientes`
- Para **MongoDB Atlas**: `mongodb+srv://...`
- O `.env` **não é commitado** (está no `.gitignore`).

### 2. Backend (server)

```bash
cd apps/server
./mvnw spring-boot:run      # http://localhost:8080
```

### 3. Testes

```bash
cd apps/server
./mvnw test
```

- Unitários: Mockito (EnvironmentServiceTest, PersonServiceTest) — não precisam de banco.
- `AmbientesApplicationTests` (`@SpringBootTest`) — precisa de MongoDB acessível com a URI de teste.

### 4. Frontend (web)

```bash
cd apps/web
npm install
npm run dev                 # http://localhost:5173
```

Tudo junto da raiz do monorepo:

```bash
npm install       # instala concurrently
npm run dev       # server (8080) + web (5173)
npm run dev:server # Backend com hot-reload: salve o .java no VS Code e o DevTools reinicia sozinho
npm run dev:web    # apenas o frontend
```

> **Nota:** o client HTTP da API é gerado com **orval** a partir do OpenAPI do backend. Depois de subir o backend, rode `npm run generate:api` em `apps/web/` para (re)gerar os clientes (`react-query` + `zod`).
>
> **⚠️ Não edite manualmente `apps/web/src/generated/`**: essa pasta é **gerada automaticamente** pelo orval e sobrescrita a cada `npm run generate:api`. Altere a fonte (OpenAPI do backend) ou o `orval.config.ts` (`override`/`mutator`), nunca os arquivos gerados.

## Autenticação

A maioria dos endpoints exige `Authorization: Bearer {token}`. Obtenha o token em `POST /auth/login` (expira em 4h).

| Perfil  | Permissões                                        |
| ------- | ------------------------------------------------- |
| `USER`  | Realizar e consultar reservas próprias            |
| `ADMIN` | Gerenciar ambientes, listar usuários              |
| `OWNER` | Acesso total — inclui criar administradores       |

## Regras de negócio (reservas)

- Data **presente ou futura**
- Slots de **exatamente 1 hora**, iniciando em **hora cheia**
- Horário permitido: **08:00 – 22:00**
- **Sem sobreposição** em mesmo ambiente + dia
- Status inicial sempre `PENDING`

## Documentação da API

Com a aplicação rodando:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

### Endpoints principais

| Método | Rota                                  | Acesso            |
| ------ | ------------------------------------- | ----------------- |
| POST   | `/auth/login`                         | Público           |
| POST   | `/auth/register`                      | Público           |
| GET    | `/environments`                       | Autenticado       |
| POST   | `/environments`                       | Autenticado       |
| GET    | `/environments/{id}`                  | Autenticado       |
| PUT    | `/environments/{id}`                  | Autenticado       |
| DELETE | `/environments/{id}`                  | Autenticado       |
| POST   | `/reserves`                            | Autenticado       |
| GET    | `/reserves/{environmentId}?date=YYYY-MM-DD` | Autenticado |
| GET    | `/person/me`                           | Autenticado       |
| GET    | `/person/list`                          | ADMIN / OWNER     |
| POST   | `/person/create-admin`                  | OWNER             |

## Roadmap

- [ ] Fluxo de aprovação/rejeição de reservas no frontend (`APPROVED` / `REJECTED`)
- [ ] CI/CD (`.github/workflows`) com build e testes