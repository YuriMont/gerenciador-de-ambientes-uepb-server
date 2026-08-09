# AGENTS.md

Guia para agentes de IA que trabalham neste repositório (monorepo do **Gerenciador de Ambientes UEPB**).

## Visão geral do projeto

Plataforma de **gerenciamento e reserva de ambientes físicos** da Universidade Estadual da Paraíba (UEPB).

- Usuários solicitam reservas de salas/laboratórios em slots de 1 h (08h–22h).
- Reservas nascem com status `PENDING` e aguardam aprovação de um administrador.
- Perfis de acesso: `USER`, `ADMIN`, `OWNER` (criados automaticamente pelo `RoleSeeder`).

## Estado atual — leia antes de planejar

Não presuma que existe o que ainda não foi construído:

| Parte | Situação |
| ----- | -------- |
| `apps/server/` | **Funcional.** Auth JWT, CRUD de ambientes, ciclo completo de reservas (criar, listar, aprovar, recusar, cancelar), disponibilidade por ambiente e painel da tela de início. |
| `apps/web/` | **Telas implementadas** a partir do protótipo: Entrar, Criar conta, Início, Ambientes (+ diálogo de novo ambiente), Reservar ambiente, Minhas reservas, Aprovar reservas e Usuários. Roteamento com `react-router-dom`, sessão em `src/lib/auth.tsx` e client HTTP escrito à mão em `src/api/` — **não há `src/generated/`**, a pasta do orval só aparece depois de rodar `npm run generate:api` com o backend no ar. |
| `design/prototype.pen` | **Protótipo completo**, 14 telas (8 desktop + 6 mobile). É a especificação visual de `apps/web/`. |

> As telas mobile do protótipo não viraram rotas separadas: o layout é responsivo, e a
> barra lateral do desktop vira a barra de abas inferior abaixo de `lg`.

## Estrutura do monorepo

```
apps/
├── server/         # Backend Spring Boot (Java 21) + MongoDB
└── web/            # Frontend (Vite + React + TypeScript)
design/             # prototype.pen — protótipo de UI e design system
graphify-out/       # Grafo de conhecimento do repositório
.github/            # (a criar) CI/CD
.agents/skills/     # Skills instaladas (fonte primária)
.claude/skills/     # Espelho para o Claude Code
.vscode/            # Formatação, launch.json, extensões
```

### `apps/server/` — Backend Spring Boot

- **Java 21**, **Spring Boot 4.x** (parent 4.0.1), **Maven** (wrapper `./mvnw`).
- **MongoDB** (Spring Data), **JWT** (Auth0 `java-jwt`, 4 h), senhas **BCrypt**.
- **SpringDoc/OpenAPI** — Swagger UI em `/swagger-ui.html`, JSON em `/v3/api-docs`.
- Lombok, `dotenv-java` para carregar `apps/server/.env`.
- Pacote base: `dev.uepb.gereciador.ambientes`.

### `apps/web/` — Frontend

- **Vite 8**, **React 19**, **TypeScript 6**, **Tailwind CSS 4** (`@tailwindcss/vite`).
- **axios** + **@tanstack/react-query**; estado leve com **jotai**; validação com **zod**.
- **shadcn/ui** (`components.json`, estilo `radix-nova`, base `neutral`, ícones lucide), utilitário `cn` em `src/lib/utils.ts`.
- Client da API gerado por **orval** (`npm run generate:api`) a partir do OpenAPI do backend.
- **⚠️ NÃO editar `src/generated/`**: o orval roda com `clean: true` e reescreve a pasta inteira. Ajuste a fonte (anotações OpenAPI no backend) ou o `override`/`mutator` do `orval.config.ts`.
- **🔒 Toda modificação no frontend feita por um agente exige**, em `apps/web/`:
  ```bash
  npm run typecheck   # tsc -b --noEmit
  npm run format      # prettier --check (use `format:fix` para corrigir)
  ```
  Só finalizar com typecheck **sem erros** e código **formatado**.

## Design — `design/prototype.pen`

O protótipo é a referência para qualquer trabalho de UI no `apps/web/`.

- **Nunca use `Read`, `Grep` ou editor de texto em arquivos `.pen`** — são criptografados. Use as ferramentas MCP do Pencil (`get_app_state`, `execute`, `get_screenshot`, `export_html`).
- Antes de qualquer operação, chame `get_app_state({ include_schema: true, include_canvas_design: true, include_scripts_and_shaders: false })` para carregar o schema.
- Ao alterar o protótipo, verifique o resultado com `get_screenshot` e cheque nós clipados/colapsados com um visitor `Get((n,c) => c.problems && ...)`.

### Telas existentes

- **Desktop (1440×860):** Entrar, Início, Ambientes, Novo ambiente, Reservar ambiente, Minhas reservas, Aprovar reservas, Usuários.
- **Mobile (390×844):** Entrar, Ambientes, Horários do ambiente, Revisar solicitação, Minhas reservas, Aprovar reservas.
- **Componentes:** folha de design system + `Mobile / Status bar`, `Mobile / Tab bar` e `App / Avatar do usuário` (reutilizáveis).

### Convenções do design

Espelham o `apps/web/src/index.css` — base *neutral* do shadcn, Inter, raio 10 px:

- Cores vêm de **variáveis do arquivo** (`$ink`, `$body`, `$muted`, `$line`, `$surface`, `$canvas`, `$accent`, `$accent-strong`, `$accent-soft`, `$green`, `$amber`, `$red` e os `-soft`). Não introduza hex solto.
- **Badges de status e perfil**: fundo suave + texto forte + ponto colorido. Ação destrutiva é fundo suave, nunca botão sólido vermelho.
- **Header da aplicação**: 72 px, fundo branco com hairline na base. À esquerda o título da página; à direita `ação primária · notificações │ conta`. **Busca não fica no header** — vai junto da lista que ela filtra.
- **Slots** de 1 h têm quatro estados: livre, selecionado, reservado e encerrado (horário já passado no dia de hoje).

## Requisitos de ambiente

| Ferramenta | Versão | Obrigatório |
| ---------- | ------ | ----------- |
| JDK        | 21     | Sim |
| Maven      | 3.x    | Sim (ou `./mvnw`) |
| Node.js    | >= 20  | Para scripts da raiz e do web |
| MongoDB    | 6+     | Sim (local ou Atlas) |

## Como rodar

### Backend (`apps/server/`)
```bash
cd apps/server
./mvnw spring-boot:run             # http://localhost:8080
./mvnw test                        # testes
./mvnw clean package -DskipTests   # build do JAR (usado pelo Dockerfile)
```

### Frontend (`apps/web/`)
```bash
cd apps/web
npm run dev            # http://localhost:5173
npm run build          # tsc -b + vite build
npm run generate:api   # gera o client da API (orval)
```

### Raiz
```bash
npm install        # instala o concurrently
npm run dev        # server + web juntos
npm run dev:server # só o backend (hot-reload via DevTools)
npm run dev:web    # só o frontend
```

## Variáveis de ambiente

**`apps/server/.env`** — lido via `spring.config.import=optional:file:.env[.properties]` e via `dotenv-java` no `main`. O diretório de trabalho **precisa** ser `apps/server/`.

```bash
SECRET=chave-longa-aleatoria       # assinatura JWT (obrigatória)
API_SERVER_URL=http://localhost:8080
APP_PROFILE=dev                    # dev | test
MONGODB_URI=mongodb://usuario:senha@localhost:27017/ambientes?authSource=admin
```

**`apps/web/.env`** — hoje só `API_URL` (veja as armadilhas abaixo).

> **NUNCA commite um `.env`** com credenciais reais. Ambos estão no `.gitignore`; use os `.env.example`.

Se o MongoDB local rodar com `--auth`, a URI precisa de usuário e senha — sem isso o `@SpringBootTest` falha ao carregar o contexto.

## Arquitetura do backend

```
src/main/java/dev/uepb/gereciador/ambientes/
├── config/        # Security, JWT (TokenConfig), CORS, Mongo, OpenAPI, AuthConfig
├── controller/    # Auth, Environment, Reserve, Person
├── dto/           # resquest/ (entrada, typo intencional) e response/
├── entity/        # User, Role, Environment, Reserve
├── enums/         # UserRole, ReserveStatus
├── repository/    # Interfaces MongoRepository
├── seeder/        # RoleSeeder (USER/ADMIN/OWNER)
├── service/       # Regras de negócio
└── AmbientesApplication.java
```

## Regras de negócio (`ReserveService`)

- Data **presente ou futura**.
- Slots de **exatamente 1 hora**, começando em **hora cheia**.
- Funcionamento **08:00–22:00** — 14 slots por dia.
- No dia de hoje, horários já passados são recusados.
- O número de participantes não passa da **capacidade do ambiente** (`Environment.capacity`).
  Ambientes salvos antes do campo existir têm `capacity` nulo e ficam sem limite.
- Status inicial sempre `PENDING`.
- **Só reservas `APPROVED` ocupam um horário.** Um pedido pendente não impede que outra pessoa
  peça o mesmo slot, e recusar devolve o horário à agenda — é o que o protótipo promete em
  "Enquanto está PENDENTE, o horário segue disponível" e "Ao recusar, o horário volta a ficar
  livre". O `409 Conflict` acontece na criação **e** na aprovação, sempre contra o que já está
  aprovado.
- `GET /reserves/{environmentId}?date=` devolve os slots **livres**, não os ocupados.
- `GET /reserves/{environmentId}/availability?date=` devolve os 14 horários com a situação de
  cada um: `AVAILABLE`, `RESERVED` ou `CLOSED` (horário já passado hoje).

### Endpoints de reserva

| Endpoint | Acesso |
| -------- | ------ |
| `POST /reserves` | Autenticado |
| `GET /reserves?status=&date=&environmentId=` | ADMIN/OWNER |
| `GET /reserves/mine?status=` | Autenticado |
| `GET /reserves/dashboard` | ADMIN/OWNER |
| `GET /reserves/availability?date=` | Autenticado |
| `GET /reserves/{environmentId}?date=` | Autenticado |
| `GET /reserves/{environmentId}/availability?date=` | Autenticado |
| `PATCH /reserves/{reserveId}/approve` | ADMIN/OWNER |
| `PATCH /reserves/{reserveId}/reject` | ADMIN/OWNER |
| `DELETE /reserves/{reserveId}` | Dono da reserva ou ADMIN/OWNER |

## Convenções de código

- **Formatação:** Google Java Style (`.vscode/settings.json`, formatter `eclipse`), `formatOnSave` + organize imports.
- **Lombok:** `@Getter`, `@Setter`, `record` para DTOs.
- **Idioma:** código, Javadoc, mensagens e respostas de API em **PT-BR**. Toda classe pública tem Javadoc.
- **DTOs:** respostas em `dto/response`, requisições em `dto/resquest` (typo mantido), com Bean Validation (`@Valid`).
- **Typos históricos intencionais:** pacote `gereciador` e diretório `resquest`. **Não corrigir** — quebra imports em todo o projeto.

## Armadilhas conhecidas

Coisas que já custaram tempo e não são óbvias no código:

1. **`npm run generate:api` precisa do backend no ar.** O orval busca o OpenAPI em
   `${VITE_API_URL}/v3/api-docs`; sem servidor rodando ele falha. Por isso o client HTTP de
   `apps/web/src/api/` é escrito à mão — ao gerar o client, troque os imports desses módulos
   pelos gerados em `src/generated/`, que têm os mesmos formatos.
2. **`VITE_API_URL`, não `API_URL`.** O Vite só expõe ao browser variáveis com o prefixo
   `VITE_`. `src/lib/api.ts` lê `import.meta.env.VITE_API_URL`; sem ela o axios cai em URL
   relativa. No `orval.config.ts` a leitura é `process.env.VITE_API_URL` via dotenv, no Node.
3. **`LocalTime` chega como `HH:mm:ss`.** O Jackson serializa os slots com segundos; a interface
   recorta com `formatTime` em `apps/web/src/lib/format.ts`. Ao enviar uma reserva, mande o
   mesmo formato que veio da disponibilidade.
4. **`new Date("2026-04-16")` é interpretado como UTC** e volta um dia atrás em fusos negativos.
   Use `parseIsoDate` de `lib/format.ts` para datas `yyyy-MM-dd`.

Além disso: o CORS em `CorsConfig.java` libera `http://localhost:5173`, `http://localhost:3000` e a URL do Render. O deploy usa `apps/server/Dockerfile` (multistage, JRE 21) e respeita `PORT`.

## Skills — MEDIDAS OBRIGATÓRIAS 🔒

O uso de skills é **obrigatório** — não comece nenhuma tarefa sem verificar se ela se aplica.

**Antes de iniciar qualquer trabalho**, o agente deve:
1. Consultar as skills instaladas (`skills-lock.json` e `.agents/skills/*/SKILL.md`).
2. Carregar a skill correspondente ao escopo da tarefa **antes** de implementar.
3. Seguir o fluxo definido na skill em todas as etapas.

| Skill | Quando usar (OBRIGATÓRIO) |
| ----- | -------------------------- |
| `find-skills` | Sempre que o usuário perguntar "como faz X?", "existe skill para X?", "você consegue X?", quiser descobrir/instalar novas capacidades, ou pedir para encontrar share de widgets. Não recomende skills sem verificar instalação e reputação da fonte. |
| `frontend-design` | **Toda** vez que houver criação ou reformulação de UI — novo componente/página em `apps/web/`, ajustes visuais, estilos, tipografia, identidade. Também para arquivos `.pen`/designs. Nunca iniciar UI sem carregá-la. |
| `shadcn` | **Toda** vez que houver trabalho de UI com **shadcn/ui** no frontend — adicionar, buscar, corrigir, debugar, estilizar ou compor componentes no `apps/web/` (ou qualquer projeto com `components.json`), incluindo chat interfaces e presets. |

> **Regra de ouro:** para o frontend (`apps/web/` e designs), `frontend-design` SEMPRE. Para componentes shadcn, `shadcn` SEMPRE. Para descoberta/instalação de capacidades, `find-skills` SEMPRE. Se uma skill do catálogo for aplicável, usá-la tem prioridade máxima e não pode ser pulada.

Onde as skills vivem:
- `.agents/skills/<nome>/SKILL.md` — skills oficiais (fonte primária).
- `.claude/skills/` — espelho para o Claude Code.
- `skills-lock.json` — lock das versões instaladas.

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

When the user types `/graphify`, use the installed graphify skill or instructions before doing anything else.

Rules:
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- Dirty graphify-out/ files are expected after hooks or incremental updates; dirty graph files are not a reason to skip graphify. Only skip graphify if the task is about stale or incorrect graph output, or the user explicitly says not to use it.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).
