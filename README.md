# Gerenciador de Ambientes UEPB

Plataforma de **gerenciamento e reserva de ambientes físicos** da Universidade Estadual da Paraíba (UEPB). Usuários solicitam salas, laboratórios e auditórios em slots de 1 hora; administradores aprovam ou recusam cada pedido.

Monorepo com três partes: o **backend** (`apps/server/`), o **frontend** (`apps/web/`) e o **protótipo de interface** (`design/`).

## Estado atual

| Parte | Situação |
| ----- | -------- |
| `apps/server/` | **Funcional.** Auth JWT, CRUD de ambientes, criação de reservas e consulta de disponibilidade. Ainda **não existe** endpoint para aprovar/recusar reserva — o status nasce `PENDING` e não há como mudá-lo pela API. |
| `apps/web/` | **Esqueleto.** Vite + React + Tailwind + shadcn configurados, `lib/api.ts` (axios com interceptor de JWT) e `queryClient` prontos. Não há telas: `App.tsx` ainda é a página do template do Vite. |
| `design/prototype.pen` | **Protótipo completo.** 14 telas (8 desktop + 6 mobile) e o design system, para servir de referência ao construir `apps/web`. |

## Stack

| Camada   | Tecnologias                                                    |
| -------- | -------------------------------------------------------------- |
| Backend  | Java 21, Spring Boot 4.x, Spring Security, Spring Data MongoDB  |
| Frontend | Vite 8, React 19, TypeScript 6, Tailwind CSS 4, shadcn/ui       |
| Banco    | MongoDB 6+                                                      |
| Auth     | JWT (Auth0 `java-jwt`, expiração 4 h) + BCrypt                  |
| Docs API | SpringDoc / Swagger UI                                          |
| Design   | Pencil (`.pen`)                                                 |
| Deploy   | Docker + Render (backend)                                       |

## Estrutura

```
apps/
├── server/                 # Backend Spring Boot + MongoDB
│   ├── src/main/java/dev/uepb/gereciador/ambientes/
│   │   ├── config/         # Security, JWT, CORS, Mongo, OpenAPI
│   │   ├── controller/     # Auth, Environment, Reserve, Person
│   │   ├── dto/            # request/ (grafado "resquest") e response/
│   │   ├── entity/         # User, Role, Environment, Reserve
│   │   ├── enums/          # UserRole, ReserveStatus
│   │   ├── repository/     # MongoRepository
│   │   ├── seeder/         # RoleSeeder (USER, ADMIN, OWNER)
│   │   └── service/        # Regras de negócio
│   ├── .env.example
│   ├── Dockerfile          # Build multistage (JRE 21)
│   └── client.http         # Requisições de exemplo (REST Client do VS Code)
└── web/                    # Frontend Vite + React + TypeScript
    ├── src/lib/            # api (axios+JWT), queryClient, utils
    ├── src/components/ui/  # Componentes shadcn
    └── orval.config.ts     # Geração do client a partir do OpenAPI

design/prototype.pen        # Protótipo de UI (desktop + mobile) e design system
graphify-out/               # Grafo de conhecimento do repositório
```

## Requisitos

- **JDK 21**
- **Maven 3.x** (ou o wrapper `./mvnw`)
- **Node.js >= 20**
- **MongoDB 6+**, local ou Atlas

## Como rodar

### 1. Variáveis de ambiente

```bash
cp apps/server/.env.example apps/server/.env
cp apps/web/.env.example    apps/web/.env
```

`apps/server/.env`:

```env
SECRET=chave-longa-aleatoria          # assinatura do JWT (obrigatória)
API_SERVER_URL=http://localhost:8080  # URL exibida no Swagger
APP_PROFILE=dev                       # dev | test
MONGODB_URI=mongodb://localhost:27017/ambientes
```

Com MongoDB rodando com `--auth`, inclua as credenciais: `mongodb://usuario:senha@localhost:27017/ambientes?authSource=admin`. Para Atlas, use `mongodb+srv://...`.

Nenhum dos `.env` é commitado — ambos estão no `.gitignore`.

### 2. Backend

```bash
cd apps/server
./mvnw spring-boot:run    # http://localhost:8080
```

O diretório de trabalho precisa ser `apps/server/` — é de lá que o `.env` é lido, tanto pelo `spring.config.import` quanto pelo `dotenv-java`.

### 3. Frontend

```bash
cd apps/web
npm install
npm run dev               # http://localhost:5173
```

### 4. Os dois juntos

```bash
npm install               # instala o concurrently na raiz
npm run dev               # server (8080) + web (5173)
npm run dev:server        # só o backend, com hot-reload via DevTools
npm run dev:web           # só o frontend
```

### 5. Testes

```bash
cd apps/server
./mvnw test
```

`EnvironmentServiceTest` e `PersonServiceTest` são unitários com Mockito e rodam sem banco. `AmbientesApplicationTests` sobe o contexto Spring e **precisa** de um MongoDB acessível na URI de `application-test.properties`.

## Autenticação

Exceto `POST /auth/**` e as rotas do Swagger, todo endpoint exige `Authorization: Bearer {token}`. O token vem de `POST /auth/login` e expira em 4 horas. Os papéis são criados na inicialização pelo `RoleSeeder`.

| Perfil  | Permissões pretendidas                        |
| ------- | --------------------------------------------- |
| `USER`  | Realizar e consultar as próprias reservas     |
| `ADMIN` | Gerenciar ambientes e listar usuários         |
| `OWNER` | Acesso total, incluindo criar administradores |

> Esta tabela descreve a **intenção**. Hoje só `/person/**` tem checagem de papel — veja [Pontos de atenção](#pontos-de-atenção).

## Regras de negócio (reservas)

Aplicadas em `ReserveService`:

- Data **presente ou futura**.
- Slots de **exatamente 1 hora**, começando em **hora cheia**.
- Janela permitida: **08:00 – 22:00** (14 slots por dia).
- No dia de hoje, horários que já passaram são recusados.
- **Sem sobreposição** para o mesmo ambiente e data (conflito → `409`).
- Status inicial sempre `PENDING`.

`GET /reserves/{environmentId}?date=` devolve os slots **livres** — os 14 possíveis menos os já reservados.

## API

Com a aplicação no ar:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

| Método | Rota | Acesso real (no código) |
| ------ | ---- | ----------------------- |
| `POST` | `/auth/login` | Público |
| `POST` | `/auth/register` | Público (cria com perfil `USER`) |
| `GET` | `/environments` | Autenticado |
| `GET` | `/environments/{id}` | Autenticado |
| `POST` | `/environments` | Autenticado ⚠️ |
| `PUT` | `/environments/{id}` | Autenticado ⚠️ |
| `DELETE` | `/environments/{id}` | Autenticado ⚠️ |
| `POST` | `/reserves` | Autenticado |
| `GET` | `/reserves/{environmentId}?date=YYYY-MM-DD` | Autenticado |
| `GET` | `/person/me` | Autenticado |
| `GET` | `/person/list` | `ADMIN` / `OWNER` |
| `POST` | `/person/create-admin` | `OWNER` |

Exemplos prontos para o REST Client do VS Code estão em `apps/server/client.http`.

## Design

O protótipo de interface vive em `design/prototype.pen`, editável no [Pencil](https://pencil.dev). Ele é a referência visual para implementar `apps/web` e cobre:

- **Desktop (1440×860):** Entrar, Início, Ambientes, Novo ambiente, Reservar ambiente, Minhas reservas, Aprovar reservas, Usuários.
- **Mobile (390×844):** Entrar, Ambientes, Horários do ambiente, Revisar solicitação, Minhas reservas, Aprovar reservas.
- **Design system:** tokens de cor, escala tipográfica (Inter), botões, campos, badges de `ReserveStatus` e `UserRole`, slots, cartões e navegação.

Os tokens do protótipo espelham o `apps/web/src/index.css` (base *neutral* do shadcn, Inter, raio de 10 px). O arquivo é binário/criptografado: abra com o Pencil, não com editor de texto.

## Pontos de atenção

Três coisas que quebram na primeira tentativa se você não souber:

1. **`npm run generate:api` falha com 404.** O `orval.config.ts` busca `${API_URL}/openapi.json`, mas o SpringDoc publica em `/v3/api-docs`. Alinhe os dois — ou `springdoc.api-docs.path=/openapi.json` no `application.properties`, ou a URL no `orval.config.ts`.
2. **`API_URL` não chega ao browser.** `src/lib/api.ts` lê `import.meta.env.API_URL`, mas o Vite só expõe variáveis com prefixo `VITE_`. O valor chega `undefined` e o axios cai em URL relativa. Renomeie para `VITE_API_URL` no `.env` e no `api.ts`. (No `orval.config.ts` é `process.env.API_URL`, lido pelo dotenv no Node — esse funciona.)
3. **Qualquer usuário autenticado pode criar, editar e apagar ambientes.** O `EnvironmentController` não tem `@PreAuthorize`, então um `USER` consegue apagar um laboratório. Falta `@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_OWNER')")` nos métodos de escrita.

Também vale saber: o CORS em `CorsConfig.java` libera `http://localhost:5173`, `http://localhost:3000` e a URL do Render. O pacote se chama `gereciador` e o diretório de DTOs de entrada `resquest` — dois typos históricos, mantidos de propósito para não quebrar imports.

## Roadmap

- [ ] Endpoint de aprovação/recusa de reservas (`APPROVED` / `REJECTED`)
- [ ] Telas do `apps/web` a partir do protótipo
- [ ] Proteger a escrita em `/environments` por papel
- [ ] CI/CD em `.github/workflows` com build e testes
