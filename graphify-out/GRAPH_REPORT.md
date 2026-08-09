# Graph Report - gerenciador-de-ambientes-uepb  (2026-08-09)

## Corpus Check
- 133 files · ~56,527 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1084 nodes · 2268 edges · 70 communities (67 shown, 3 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 51 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `47db4cd2`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- Role
- Environment
- ReserveController.java
- devDependencies
- dependencies
- Gerenciador de Ambientes UEPB — Server
- AuthController.java
- compilerOptions
- components.json
- compilerOptions
- AGENTS.md
- shadcn/ui
- PersonController.java
- Commands
- SecurityConfig.java
- Gerenciador de Ambientes UEPB
- Find Skills
- Customization & Theming
- Component Composition
- Styling & Customization
- input-group.tsx
- Tools
- mvnw
- package.json
- Registry Authoring and Addresses
- Base vs Radix
- Chat & Messaging
- Forms & Inputs
- MongoConfig.java
- Frontend Design
- CorsConfig.java
- SpringDocConfig.java
- Icons
- AmbientesApplication
- AmbientesApplicationTests.java
- auth-context.ts
- reserve.tsx
- status-badge.tsx
- tsconfig.json
- opencode.json
- graphify.js
- dev.uepb.gereciador:ambientes
- approvals.tsx
- environments.tsx
- my-reserves.tsx
- users.tsx
- PersonServiceTest.java
- format.ts
- PersonService
- User
- Gerenciador de Ambientes UEPB — Web
- App.tsx
- 🛠️ Como Executar
- UserRepository
- Endpoints disponíveis
- Como rodar
- 🧪 Exemplo de uso rápido
- Prioridade Graphify
- formatTime
- types.ts
- TestDataSeeder.java
- cn
- main.tsx
- alert.tsx
- utils.ts

## God Nodes (most connected - your core abstractions)
1. `cn()` - 141 edges
2. `User` - 39 edges
3. `Environment` - 27 edges
4. `Reserve` - 26 edges
5. `ReserveService` - 26 edges
6. `ReserveStatus` - 22 edges
7. `Role` - 21 edges
8. `UserRole` - 20 edges
9. `compilerOptions` - 19 edges
10. `ReserveController` - 18 edges

## Surprising Connections (you probably didn't know these)
- `NavItemLink()` --calls--> `cn()`  [EXTRACTED]
  apps/web/src/components/app-shell.tsx → apps/web/src/lib/utils.ts
- `AlertTitle()` --calls--> `cn()`  [EXTRACTED]
  apps/web/src/components/ui/alert.tsx → apps/web/src/lib/utils.ts
- `AlertAction()` --calls--> `cn()`  [EXTRACTED]
  apps/web/src/components/ui/alert.tsx → apps/web/src/lib/utils.ts
- `DialogOverlay()` --calls--> `cn()`  [EXTRACTED]
  apps/web/src/components/ui/dialog.tsx → apps/web/src/lib/utils.ts
- `DropdownMenuCheckboxItem()` --calls--> `cn()`  [EXTRACTED]
  apps/web/src/components/ui/dropdown-menu.tsx → apps/web/src/lib/utils.ts

## Import Cycles
- None detected.

## Communities (70 total, 3 thin omitted)

### Community 0 - "Role"
Cohesion: 0.15
Nodes (18): AllArgsConstructor, ApplicationListener, Document, Getter, NoArgsConstructor, Schema, Setter, Role (+10 more)

### Community 1 - "Environment"
Cohesion: 0.09
Nodes (34): EnvironmentController, ApiResponses, DeleteMapping, GetMapping, Operation, PostMapping, PreAuthorize, RequestMapping (+26 more)

### Community 2 - "ReserveController.java"
Cohesion: 0.05
Nodes (52): ApiResponses, DeleteMapping, GetMapping, Operation, PostMapping, PreAuthorize, RequestMapping, ResponseEntity (+44 more)

### Community 3 - "devDependencies"
Cohesion: 0.04
Nodes (44): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, orval, prettier (+36 more)

### Community 4 - "dependencies"
Cohesion: 0.05
Nodes (41): dependencies, axios, class-variance-authority, clsx, @fontsource-variable/inter, jotai, lucide-react, next-themes (+33 more)

### Community 5 - "Gerenciador de Ambientes UEPB — Server"
Cohesion: 0.25
Nodes (8): 📐 Arquitetura, 🔐 Autenticação, 🗂️ Coleções MongoDB, 📝 Convenções e observações, Gerenciador de Ambientes UEPB — Server, Perfis de acesso, 🗓️ Regras de Negócio — Reservas, 🚀 Tecnologias

### Community 6 - "AuthController.java"
Cohesion: 0.14
Nodes (18): Component, TokenConfig, AuthController, ApiResponses, AuthenticationManager, Operation, PasswordEncoder, PostMapping (+10 more)

### Community 7 - "compilerOptions"
Cohesion: 0.08
Nodes (24): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+16 more)

### Community 8 - "components.json"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 9 - "compilerOptions"
Cohesion: 0.10
Nodes (19): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+11 more)

### Community 10 - "AGENTS.md"
Cohesion: 0.09
Nodes (21): `apps/server/` — Backend Spring Boot, `apps/web/` — Frontend, Armadilhas conhecidas, Arquitetura do backend, Backend (`apps/server/`), Como rodar, Convenções de código, Convenções do design (+13 more)

### Community 11 - "shadcn/ui"
Cohesion: 0.11
Nodes (19): Chat & Messaging → [chat.md](./rules/chat.md), CLI, Component Docs, Examples, and Usage, Component Selection, Component Structure → [composition.md](./rules/composition.md), Critical Rules, Current Project Context, Detailed References (+11 more)

### Community 12 - "PersonController.java"
Cohesion: 0.23
Nodes (13): ApiResponses, GetMapping, Operation, PostMapping, PreAuthorize, RequestMapping, ResponseEntity, RestController (+5 more)

### Community 13 - "Commands"
Cohesion: 0.12
Nodes (17): `add` — Add components, `apply` — Apply a preset to an existing project, `build` — Build a custom registry, Commands, Contents, `diff` — Check for updates, `docs` — Get component documentation URLs, Dry-Run Mode (+9 more)

### Community 14 - "SecurityConfig.java"
Cohesion: 0.12
Nodes (20): JWTUserData, AuthenticationManager, Bean, Configuration, PasswordEncoder, SecurityConfig, Component, Override (+12 more)

### Community 15 - "Gerenciador de Ambientes UEPB"
Cohesion: 0.18
Nodes (11): API, Autenticação, Design, Estado atual, Estrutura, Gerenciador de Ambientes UEPB, Pontos de atenção, Regras de negócio (reservas) (+3 more)

### Community 16 - "Find Skills"
Cohesion: 0.14
Nodes (13): Common Skill Categories, Find Skills, How to Help Users Find Skills, Step 1: Understand What They Need, Step 2: Check the Leaderboard First, Step 3: Search for Skills, Step 4: Verify Quality Before Recommending, Step 5: Present Options to the User (+5 more)

### Community 17 - "Customization & Theming"
Cohesion: 0.14
Nodes (14): 1. Built-in variants, 2. Tailwind classes via `className`, 3. Add a new variant, 4. Wrapper components, Adding Custom Colors, Border Radius, Changing the Theme, Checking for Updates (+6 more)

### Community 18 - "Component Composition"
Cohesion: 0.15
Nodes (13): Avatar always needs AvatarFallback, Button has no isPending or isLoading prop, Callouts use Alert, Card structure, Choosing between overlay components, Component Composition, Contents, Dialog, Sheet, and Drawer always need a Title (+5 more)

### Community 19 - "Styling & Customization"
Cohesion: 0.15
Nodes (13): Built-in variants first, className for layout only, Contents, No manual dark: color overrides, No manual z-index on overlay components, No raw color values for status/state indicators, No space-x-* / space-y-*, Prefer size-* over w-* h-* when equal (+5 more)

### Community 20 - "input-group.tsx"
Cohesion: 0.24
Nodes (9): InputGroup(), InputGroupAddon(), inputGroupAddonVariants, InputGroupButton(), inputGroupButtonVariants, InputGroupInput(), InputGroupText(), InputGroupTextarea() (+1 more)

### Community 21 - "Tools"
Cohesion: 0.17
Nodes (11): Configuring Registries, Setup, `shadcn:get_add_command_for_items`, `shadcn:get_audit_checklist`, `shadcn:get_item_examples_from_registries`, `shadcn:get_project_registries`, `shadcn:list_items_in_registries`, shadcn MCP Server (+3 more)

### Community 22 - "mvnw"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 23 - "package.json"
Cohesion: 0.20
Nodes (9): concurrently, devDependencies, concurrently, private, scripts, dev, dev:server, dev:web (+1 more)

### Community 25 - "Registry Authoring and Addresses"
Cohesion: 0.22
Nodes (9): Address Schemes, Build and Verify, GitHub Registries, Include, Item Definitions, Mental Model, Registry Authoring and Addresses, Registry Dependencies (+1 more)

### Community 26 - "Base vs Radix"
Cohesion: 0.22
Nodes (9): Accordion, Base vs Radix, Button / trigger as non-button element (base only), Composition: asChild (radix) vs render (base), Contents, Select, Select — multiple selection and object values (base only), Slider (+1 more)

### Community 27 - "Chat & Messaging"
Cohesion: 0.22
Nodes (9): Attachments use Attachment, Chat & Messaging, Contents, Escape hatch: the scroller hooks, Message rows use Message, Message surfaces use Bubble, Scrollable threads use MessageScroller, Streaming, anchoring, and jump-to-latest are built in (+1 more)

### Community 28 - "Forms & Inputs"
Cohesion: 0.25
Nodes (8): Buttons inside inputs use InputGroup + InputGroupAddon, Contents, Field validation and disabled states, FieldSet + FieldLegend for grouping related fields, Forms & Inputs, Forms use FieldGroup + Field, InputGroup requires InputGroupInput/InputGroupTextarea, Option sets (2–7 choices) use ToggleGroup

### Community 29 - "MongoConfig.java"
Cohesion: 0.46
Nodes (5): Bean, Configuration, MongoConfig, MongoClient, MongoTemplate

### Community 30 - "Frontend Design"
Cohesion: 0.29
Nodes (6): Design principles, Frontend Design, Ground it in the subject, More on writing in design, Process: brainstorm, explore, plan, critique, build, critique again, Restraint and self-critique

### Community 31 - "CorsConfig.java"
Cohesion: 0.43
Nodes (5): CorsConfig, Configuration, Override, CorsRegistry, WebMvcConfigurer

### Community 32 - "SpringDocConfig.java"
Cohesion: 0.53
Nodes (4): Bean, Configuration, SpringDocConfig, OpenAPI

### Community 33 - "Icons"
Cohesion: 0.40
Nodes (4): Icons, Icons in Button use data-icon attribute, No sizing classes on icons inside components, Pass icons as component objects, not string keys

### Community 34 - "AmbientesApplication"
Cohesion: 0.60
Nodes (3): AmbientesApplication, EnableMongoAuditing, SpringBootApplication

### Community 35 - "AmbientesApplicationTests.java"
Cohesion: 0.60
Nodes (3): AmbientesApplicationTests, Test, SpringBootTest

### Community 36 - "auth-context.ts"
Cohesion: 0.21
Nodes (12): useCurrentUser(), useUsers(), CurrentUser, RegisterUserRequest, UserSummary, api, customInstance(), TOKEN_KEY (+4 more)

### Community 37 - "reserve.tsx"
Cohesion: 0.14
Nodes (21): Field(), FieldContent(), FieldDescription(), FieldError(), FieldGroup(), FieldLabel(), FieldLegend(), FieldSeparator() (+13 more)

### Community 38 - "status-badge.tsx"
Cohesion: 0.20
Nodes (11): ReserveStatus, UserRole, dotVariants, ROLE_TONE, RoleBadge(), STATUS_LABEL, STATUS_TONE, StatusBadge() (+3 more)

### Community 39 - "tsconfig.json"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 41 - "opencode.json"
Cohesion: 0.33
Nodes (5): instructions, plugin, $schema, .opencode/instructions.md, .opencode/plugins/graphify.js

### Community 47 - "approvals.tsx"
Cohesion: 0.20
Nodes (17): useApproveReserve(), useDashboard(), useInvalidateReserves(), useRejectReserve(), useReserves(), PageHeader(), Panel(), Avatar() (+9 more)

### Community 48 - "environments.tsx"
Cohesion: 0.13
Nodes (23): useCreateEnvironment(), Button(), buttonVariants, Dialog(), DialogClose(), DialogContent(), DialogDescription(), DialogFooter() (+15 more)

### Community 49 - "my-reserves.tsx"
Cohesion: 0.10
Nodes (22): useCancelReserve(), ADMIN_NAV, Brand(), MAIN_NAV, NavItem, NavItemLink(), TabBar(), StatCard() (+14 more)

### Community 50 - "users.tsx"
Cohesion: 0.18
Nodes (14): useCreateAdministrator(), Skeleton(), Table(), TableBody(), TableCaption(), TableCell(), TableFooter(), TableHead() (+6 more)

### Community 51 - "PersonServiceTest.java"
Cohesion: 0.22
Nodes (8): Schema, RegisterUserRequest, BeforeEach, ExtendWith, PasswordEncoder, RegisterUserRequest, Test, PersonServiceTest

### Community 52 - "format.ts"
Cohesion: 0.26
Nodes (14): useMyReserves(), addDays(), formatDayMonthYear(), formatLongDate(), formatRelative(), formatShortDate(), formatSlotRange(), parseIsoDate() (+6 more)

### Community 53 - "PersonService"
Cohesion: 0.29
Nodes (5): Schema, UserSummaryResponse, PasswordEncoder, Service, PersonService

### Community 54 - "User"
Cohesion: 0.23
Nodes (8): Document, Getter, Override, Schema, Setter, User, GrantedAuthority, UserDetails

### Community 55 - "Gerenciador de Ambientes UEPB — Web"
Cohesion: 0.22
Nodes (9): Client da API (orval), Componentes shadcn, Design system, Dois tropeços conhecidos, Estrutura, Gerenciador de Ambientes UEPB — Web, Rodando, Scripts (+1 more)

### Community 56 - "App.tsx"
Cohesion: 0.22
Nodes (12): useEnvironments(), useAllAvailability(), RequireAdmin(), RequireAuth(), AppShell(), useAuth(), categoryOf(), EnvironmentsPage() (+4 more)

### Community 57 - "🛠️ Como Executar"
Cohesion: 0.25
Nodes (8): 1. Clonar o repositório, 2. Configurar variáveis de ambiente, 3. Executar a aplicação, 4. Executar os testes, 5. Build do JAR, 6. Acessar, 🛠️ Como Executar, Pré-requisitos

### Community 58 - "UserRepository"
Cohesion: 0.24
Nodes (8): AuthConfig, Override, Service, UserDetails, Repository, UserDetails, UserRepository, UserDetailsService

### Community 59 - "Endpoints disponíveis"
Cohesion: 0.33
Nodes (6): Ambientes (`/environments`), Autenticação (`/auth`), 📖 Documentação da API (Swagger UI), Endpoints disponíveis, Reservas (`/reserves`), Usuários (`/person`)

### Community 60 - "Como rodar"
Cohesion: 0.33
Nodes (6): 1. Variáveis de ambiente, 2. Backend, 3. Frontend, 4. Os dois juntos, 5. Testes, Como rodar

### Community 61 - "🧪 Exemplo de uso rápido"
Cohesion: 0.40
Nodes (5): 1. Registrar usuário, 2. Fazer login, 3. Consultar slots disponíveis, 4. Criar reserva, 🧪 Exemplo de uso rápido

### Community 63 - "formatTime"
Cohesion: 0.67
Nodes (3): MiniAgenda(), formatTime(), ScheduleRow()

### Community 64 - "types.ts"
Cohesion: 0.14
Nodes (16): ENVIRONMENTS_KEY, useEnvironment(), useAvailability(), useCreateReserve(), CreateReserveRequest, Dashboard, Environment, EnvironmentAvailability (+8 more)

### Community 65 - "TestDataSeeder.java"
Cohesion: 0.25
Nodes (8): Component, ContextRefreshedEvent, Environment, Override, PasswordEncoder, Slot, TestDataSeeder, Profile

### Community 68 - "cn"
Cohesion: 0.07
Nodes (42): AvatarBadge(), AvatarGroup(), AvatarGroupCount(), AvatarImage(), Card(), CardAction(), CardContent(), CardDescription() (+34 more)

### Community 72 - "main.tsx"
Cohesion: 0.47
Nodes (3): App(), Toaster(), queryClient

### Community 74 - "alert.tsx"
Cohesion: 0.40
Nodes (5): Alert(), AlertAction(), AlertDescription(), AlertTitle(), alertVariants

### Community 75 - "utils.ts"
Cohesion: 0.27
Nodes (7): Badge(), badgeVariants, ToggleGroup(), ToggleGroupContext, ToggleGroupItem(), Toggle(), toggleVariants

## Knowledge Gaps
- **330 isolated node(s):** `$schema`, `.opencode/instructions.md`, `.opencode/plugins/graphify.js`, `dev.uepb.gereciador:ambientes`, `APPROVED` (+325 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `dependencies` connect `dependencies` to `devDependencies`?**
  _High betweenness centrality (0.063) - this node is a cross-community bridge._
- **Why does `cn()` connect `cn` to `reserve.tsx`, `status-badge.tsx`, `alert.tsx`, `utils.ts`, `approvals.tsx`, `environments.tsx`, `my-reserves.tsx`, `users.tsx`, `input-group.tsx`, `format.ts`, `formatTime`?**
  _High betweenness centrality (0.061) - this node is a cross-community bridge._
- **Why does `ToggleGroupItem()` connect `utils.ts` to `dependencies`, `cn`, `environments.tsx`, `my-reserves.tsx`, `users.tsx`?**
  _High betweenness centrality (0.057) - this node is a cross-community bridge._
- **What connects `$schema`, `.opencode/instructions.md`, `.opencode/plugins/graphify.js` to the rest of the system?**
  _330 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Environment` be split into smaller, more focused modules?**
  _Cohesion score 0.08701298701298701 - nodes in this community are weakly interconnected._
- **Should `ReserveController.java` be split into smaller, more focused modules?**
  _Cohesion score 0.0533028745478774 - nodes in this community are weakly interconnected._
- **Should `devDependencies` be split into smaller, more focused modules?**
  _Cohesion score 0.044444444444444446 - nodes in this community are weakly interconnected._