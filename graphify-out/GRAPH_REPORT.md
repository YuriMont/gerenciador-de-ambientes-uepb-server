# Graph Report - gerenciador-de-ambientes-uepb  (2026-08-09)

## Corpus Check
- 80 files · ~32,220 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 685 nodes · 1003 edges · 47 communities (44 shown, 3 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 33 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e8dd93d3`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- User
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
- SecurityFilter.java
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
- main.tsx
- button.tsx
- api.ts
- tsconfig.json
- React + TypeScript + Vite
- opencode.json
- graphify.js
- dev.uepb.gereciador:ambientes

## God Nodes (most connected - your core abstractions)
1. `User` - 25 edges
2. `Environment` - 22 edges
3. `Role` - 19 edges
4. `compilerOptions` - 19 edges
5. `compilerOptions` - 15 edges
6. `Reserve` - 14 edges
7. `UserRepository` - 14 edges
8. `SaveEnvironmentRequest` - 13 edges
9. `RoleRepository` - 13 edges
10. `Component Composition` - 13 edges

## Surprising Connections (you probably didn't know these)
- `AuthConfig` --references--> `UserRepository`  [EXTRACTED]
  apps/server/src/main/java/dev/uepb/gereciador/ambientes/config/AuthConfig.java → apps/server/src/main/java/dev/uepb/gereciador/ambientes/repository/UserRepository.java
- `SecurityFilter` --references--> `AuthConfig`  [EXTRACTED]
  apps/server/src/main/java/dev/uepb/gereciador/ambientes/config/SecurityFilter.java → apps/server/src/main/java/dev/uepb/gereciador/ambientes/config/AuthConfig.java
- `PersonController` --references--> `AuthConfig`  [EXTRACTED]
  apps/server/src/main/java/dev/uepb/gereciador/ambientes/controller/PersonController.java → apps/server/src/main/java/dev/uepb/gereciador/ambientes/config/AuthConfig.java
- `SecurityConfig` --references--> `SecurityFilter`  [EXTRACTED]
  apps/server/src/main/java/dev/uepb/gereciador/ambientes/config/SecurityConfig.java → apps/server/src/main/java/dev/uepb/gereciador/ambientes/config/SecurityFilter.java
- `SecurityFilter` --references--> `TokenConfig`  [EXTRACTED]
  apps/server/src/main/java/dev/uepb/gereciador/ambientes/config/SecurityFilter.java → apps/server/src/main/java/dev/uepb/gereciador/ambientes/config/TokenConfig.java

## Import Cycles
- None detected.

## Communities (47 total, 3 thin omitted)

### Community 0 - "User"
Cohesion: 0.08
Nodes (39): AllArgsConstructor, ApplicationListener, Schema, RegisterUserRequest, Document, Getter, NoArgsConstructor, Schema (+31 more)

### Community 1 - "Environment"
Cohesion: 0.10
Nodes (30): EnvironmentController, ApiResponses, GetMapping, Operation, PostMapping, RequestMapping, ResponseEntity, RestController (+22 more)

### Community 2 - "ReserveController.java"
Cohesion: 0.08
Nodes (35): AuthConfig, Override, Service, UserDetails, ApiResponses, GetMapping, Operation, PostMapping (+27 more)

### Community 3 - "devDependencies"
Cohesion: 0.05
Nodes (42): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, orval, @tanstack/eslint-plugin-query (+34 more)

### Community 4 - "dependencies"
Cohesion: 0.06
Nodes (35): dependencies, axios, class-variance-authority, clsx, @fontsource-variable/inter, jotai, lucide-react, radix-ui (+27 more)

### Community 5 - "Gerenciador de Ambientes UEPB — Server"
Cohesion: 0.07
Nodes (26): 1. Clonar o repositório, 1. Registrar usuário, 2. Configurar variáveis de ambiente, 2. Fazer login, 3. Criar reserva, 3. Executar a aplicação, 4. Consultar slots disponíveis, 4. Executar os testes (+18 more)

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
Cohesion: 0.11
Nodes (17): `apps/server/` — Backend Spring Boot, `apps/web/` — Frontend (Vite + React + TypeScript), Arquitetura do backend (estrutura de diretórios), Backend (apps/server/), Como rodar, Convensões de código, Estrutura do monorepo, Frontend (apps/web/) (+9 more)

### Community 11 - "shadcn/ui"
Cohesion: 0.11
Nodes (19): Chat & Messaging → [chat.md](./rules/chat.md), CLI, Component Docs, Examples, and Usage, Component Selection, Component Structure → [composition.md](./rules/composition.md), Critical Rules, Current Project Context, Detailed References (+11 more)

### Community 12 - "PersonController.java"
Cohesion: 0.25
Nodes (13): ApiResponses, GetMapping, Operation, PostMapping, RequestMapping, ResponseEntity, RestController, SecurityRequirement (+5 more)

### Community 13 - "Commands"
Cohesion: 0.12
Nodes (17): `add` — Add components, `apply` — Apply a preset to an existing project, `build` — Build a custom registry, Commands, Contents, `diff` — Check for updates, `docs` — Get component documentation URLs, Dry-Run Mode (+9 more)

### Community 14 - "SecurityConfig.java"
Cohesion: 0.25
Nodes (11): AuthenticationManager, Bean, Configuration, PasswordEncoder, SecurityConfig, AuthenticationConfiguration, EnableMethodSecurity, EnableWebSecurity (+3 more)

### Community 15 - "Gerenciador de Ambientes UEPB"
Cohesion: 0.13
Nodes (14): 1. Configure o ambiente, 2. Backend (server), 3. Testes, 4. Frontend (web), Autenticação, Como rodar, Documentação da API, Endpoints principais (+6 more)

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

### Community 20 - "SecurityFilter.java"
Cohesion: 0.23
Nodes (9): JWTUserData, Component, Override, SecurityFilter, Builder, FilterChain, HttpServletRequest, HttpServletResponse (+1 more)

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

### Community 37 - "button.tsx"
Cohesion: 0.70
Nodes (3): Button(), buttonVariants, cn()

### Community 38 - "api.ts"
Cohesion: 0.50
Nodes (4): api, BodyType, customInstance(), ErrorType

### Community 39 - "tsconfig.json"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 40 - "React + TypeScript + Vite"
Cohesion: 0.50
Nodes (3): Expanding the ESLint configuration, React Compiler, React + TypeScript + Vite

### Community 41 - "opencode.json"
Cohesion: 0.50
Nodes (3): plugin, $schema, .opencode/plugins/graphify.js

## Knowledge Gaps
- **291 isolated node(s):** `$schema`, `.opencode/plugins/graphify.js`, `dev.uepb.gereciador:ambientes`, `APPROVED`, `REJECTED` (+286 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `User` connect `User` to `ReserveController.java`, `PersonController.java`, `AuthController.java`?**
  _High betweenness centrality (0.028) - this node is a cross-community bridge._
- **Why does `UserRepository` connect `User` to `Environment`, `ReserveController.java`, `AuthController.java`?**
  _High betweenness centrality (0.023) - this node is a cross-community bridge._
- **What connects `$schema`, `.opencode/plugins/graphify.js`, `dev.uepb.gereciador:ambientes` to the rest of the system?**
  _291 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `User` be split into smaller, more focused modules?**
  _Cohesion score 0.07570621468926554 - nodes in this community are weakly interconnected._
- **Should `Environment` be split into smaller, more focused modules?**
  _Cohesion score 0.09728506787330317 - nodes in this community are weakly interconnected._
- **Should `ReserveController.java` be split into smaller, more focused modules?**
  _Cohesion score 0.08470588235294117 - nodes in this community are weakly interconnected._
- **Should `devDependencies` be split into smaller, more focused modules?**
  _Cohesion score 0.046511627906976744 - nodes in this community are weakly interconnected._