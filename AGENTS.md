# AGENTS.md

Guia para agentes de IA que trabalham neste repositório (monorepo do **Gerenciador de Ambientes UEPB**).

## Visão geral do projeto

Plataforma de **gerenciamento e reserva de ambientes físicos** da Universidade Estadual da Paraíba (UEPB).

- Usuários solicitam reservas de salas/laboratórios em slots de 1h (08h–22h).
- Reservas nascem com status `PENDING` e aguardam aprovação de um administrador.
- Perfis de acesso: `USER`, `ADMIN`, `OWNER` (papéis criados automaticamente pelo `RoleSeeder`).

## Estrutura do monorepo

```
apps/               # Aplicações do monorepo
├── server/         # Backend Spring Boot (Java 21) + MongoDB
└── web/            # Frontend (Vite + React + TypeScript)
.github/            # (a criar) CI/CD
.agents/            # Skills instaladas (find-skills, frontend-design)
.claude/            # Espelho das skills para Claude Code
.vscode/            # Configurações de editor (formatação, launch.json, extensões)
```

### `apps/server/` — Backend Spring Boot

- **Java 21**, **Spring Boot 4.x** (parent `spring-boot-starter-parent` 4.0.1), **Maven** (wrapper `./mvnw`).
- Persistência **MongoDB** (Spring Data), autenticação **JWT** (Auth0 `java-jwt`, expiração 4h), senhas **BCrypt**.
- Documentação via **SpringDoc/OpenAPI** (Swagger UI em `/swagger-ui.html`).
- Lombok (getters/setters/records), `dotenv-java` para carregar `apps/server/.env`.
- Pacote base: `dev.uepb.gereciador.ambientes`.

`apps/server/.env.example` documenta as variáveis necessárias (`SECRET`, `API_SERVER_URL`, `APP_PROFILE`, `MONGODB_URI`).

### `apps/web/` — Frontend (Vite + React + TypeScript)

- **Vite 8**, **React 19**, **TypeScript 6**, Tailwind CSS 4 (`@tailwindcss/vite`).
- Client HTTP: **axios** + **@tanstack/react-query**; estado leve via **jotai**; formulários validados com **zod**.
- Geração de código da API com **orval** (`npm run generate:api` em `apps/web`) a partir do OpenAPI do backend.
- UI components via **shadcn** (`components.json`), utilitário `cn` em `src/lib/utils.ts`.
- **⚠️ NÃO editar manualmente `src/generated/`**: a pasta `apps/web/src/generated/` (client `react-query` + schemas zod) é **gerada automaticamente** pelo orval e sobrescrita a cada `npm run generate:api`. Alterações manuais são perdidas. Qualquer ajuste deve ser feito na fonte (backend OpenAPI) ou via `override`/`mutator` no `orval.config.ts`.
- **🔒 Toda modificação no frontend feita por um agente exige** rodar, em `apps/web/`:
  ```bash
  npm run typecheck   # tsc -b --noEmit
  npm run format      # prettier --check (use `format:fix` para corrigir)
  ```
  Só finalizar a tarefa com typecheck **sem erros** e código **formatado** (ou, caso a formatação seja corrigida por `format:fix`, o `format` voltar a passar).

## Requisitos de ambiente

| Ferramenta | Versão  | Obrigatório |
| ---------- | ------- | ----------- |
| JDK        | 21      | Sim         |
| Maven      | 3.x     | Sim (ou usar `./mvnw`) |
| Node.js    | >= 20   | Somente para scripts raiz/web |
| MongoDB    | 6+      | Sim (local ou Atlas) |

## Como rodar

### Backend (apps/server/)
```bash
cd apps/server
./mvnw spring-boot:run        # sobe em http://localhost:8080
./mvnw test                   # testes unitários (Mockito)
./mvnw clean package -DskipTests   # build do JAR (Dockerfile usa este comando)
```

### Frontend (apps/web/)
```bash
cd apps/web
npm run dev        # sobe em http://localhost:5173
npm run build      # typecheck (tsc -b) + build (vite build)
npm run generate:api   # gera client da API (orval) a partir do OpenAPI
```

### Raiz (monorepo)
```bash
npm install        # instala `concurrently`
npm run dev        # roda server + web juntos (web precisa existir)
npm run dev:server # apenas o backend (hot-reload via DevTools + auto-build do VS Code)
npm run dev:web    # apenas o frontend
```

## Variáveis de ambiente (apps/server/.env)

O backend lê `apps/server/.env` de duas formas: via `spring.config.import=optional:file:.env[.properties]` e via `dotenv-java` no `main`. O working directory deve ser `apps/server/`.

```bash
SECRET=chave-longa-aleatoria       # assinatura JWT (obrigatória)
API_SERVER_URL=http://localhost:8080
APP_PROFILE=dev                    # dev | test
MONGODB_URI=mongodb://usuario:senha@localhost:27017/ambientes?authSource=admin
```

> **NUNCA commite o `.env`** com credenciais reais. Ele está no `.gitignore`. Use `.env.example` como template.

Se o MongoDB local rodar com `--auth`, a URI precisa contar usuário/senha. Sem credenciais, o `@SpringBootTest` (context load) falha ao conectar.

## Arquitetura do backend (estrutura de diretórios)

```
src/main/java/dev/uepb/gereciador/ambientes/
├── config/        # Security, JWT (TokenConfig), CORS, Mongo, OpenAPI, AuthConfig
├── controller/    # REST (Auth, Environment, Reserve, Person)
├── dto/           # Request/Response (resquest/ tem typo intencional no pacote)
├── entity/        # User, Role, Environment, Reserve
├── enums/         # UserRole, ReserveStatus
├── repository/    # MongoRepository interfaces
├── seeder/        # RoleSeeder (cria USER/ADMIN/OWNER)
├── service/       # Regras de negócio
└── AmbientesApplication.java
```

## Regras de negócio (ReserveService)

- Data da reserva deve ser **presente ou futura**.
- Slots de **exatamente 1 hora**, iniciando em **hora cheia**.
- Horário de funcionamento: **08:00–22:00**.
- **Sem sobreposição** de slots para o mesmo ambiente+data.
- Status inicial sempre `PENDING`.

## Convensões de código

- **Formatação**: Google Java Style (definido em `.vscode/settings.json`: `eclipse` formatter). `formatOnSave` + organize imports.
- **Lombok**: use `@Getter`, `@Setter`, records para DTOs.
- **Idioma**: código, javadocs, mensagens e respostas de API em **PT-BR**. Nome de pacote é `gereciador` (typo histórico, não corrigir).
- **Javadoc**: cada classe pública tem javadoc em PT-BR (padrão do projeto).
- **Dtos**: respostas utilizam records em `dto/response`; requests em `dto/resquest` (typo mantido), com bean validation (`@Valid`).
- **Security**: `@PreAuthorize` nos controllers (ex.: `hasRole('ROLE_ADMIN')`), Javadoc de perfil em cada endpoint.

## Testes

- Unit: Service tests com **Mockito** (`@ExtendWith(MockitoExtension.class)`) em `src/test/java`.
- Integration: `AmbientesApplicationTests` (`@SpringBootTest`) — precisa de MongoDB acessível (config da URI em `application-test.properties`).

## Skills — MEDIDAS OBRIGATÓRIAS 🔒

O uso de skills é **obrigatório** — não comece nenhuma tarefa sem verificar se ela se aplica.

**Antes de iniciar qualquer trabalho**, o agente deve:
1. Consultar as skills instaladas (`skills-lock.json` e `.agents/skills/*/SKILL.md`).
2. Carregar a skill correspondente ao escopo da tarefa (via a ferramenta de skill/agente) **antes** de implementar.
3. Seguir o fluxo definido na skill em todas as etapas.

| Skill | Quando usar (OBRIGATÓRIO) |
| ----- | -------------------------- |
| `find-skills` (`find-skills`) | Sempre que o usuário perguntar "como faz X?", "existe skill para X?", "você consegue X?", quiser descobrir/instalar novas capacidades, ou pedir para encontrar share de widgets. Não recomende skills sem verificar instalação e reputação da fonte. |
| `frontend-design` (`frontend-design`) | **Toda** vez que houver criação ou reformulação de UI — novo componente/página em `apps/web/`, ajustes visuais, estilos, tipografia, identidade. Também para arquivos `.pen`/designs. Nunca iniciar UI sem carregá-la. |
| `shadcn` (`shadcn`) | **Toda** vez que houver trabalho de UI com **shadcn/ui** no frontend — adicionar, buscar, corrigir, debugar, estilizar ou compor componentes no `apps/web/` (ou qualquer projeto com `components.json`), incluindo chat interfaces e presets. Sempre que for necessário criar/usar componentes shadcn, carregar esta skill antes. |

> **Regra de ouro:** para o frontend (`apps/web/` e designs), `frontend-design` SEMPRE. Para componentes shadcn no frontend, `shadcn` SEMPRE. Para descoberta/instalação de capacidades, `find-skills` SEMPRE. Se uma skill do catálogo for aplicável, usá-la tem prioridade máxima e não pode ser pulada.

Onde as skills vivem:
- `.agents/skills/<nome>/SKILL.md` — skills oficiais (fonte primária).
- `.claude/skills/` — espelho para o Claude Code.
- `skills-lock.json` — lock das versões instaladas.

## Pendências / cuidado

- CORS liberado em `CorsConfig.java`: `http://localhost:5173` (Vite), `http://localhost:3000` e a URL do Render (`https://gerenciador-de-ambientes-uepb-server.onrender.com`).
- Deploy: `apps/server/Dockerfile` (multistage, JRE 21), definido para hospedagem no **Render** (usa `PORT`).