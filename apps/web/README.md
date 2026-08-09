# Gerenciador de Ambientes UEPB — Web

Frontend da plataforma de reserva de ambientes da UEPB. Consome a API em `apps/server/`.

> **Estado:** esqueleto. A infraestrutura está pronta (Vite, Tailwind, shadcn, axios com JWT, react-query), mas ainda não há telas — `src/App.tsx` continua sendo a página inicial do template do Vite. O que precisa ser construído está desenhado em [`design/prototype.pen`](../../design/prototype.pen).

## Stack

| Ferramenta | Uso |
| ---------- | --- |
| Vite 8 + React 19 + TypeScript 6 | Base da aplicação |
| Tailwind CSS 4 (`@tailwindcss/vite`) | Estilos |
| shadcn/ui (`components.json`, estilo `radix-nova`, base `neutral`) | Componentes |
| lucide-react | Ícones |
| axios + @tanstack/react-query | HTTP e cache de servidor |
| jotai | Estado local leve |
| zod | Validação de formulários |
| orval | Geração do client da API a partir do OpenAPI |

## Rodando

```bash
npm install
cp .env.example .env
npm run dev          # http://localhost:5173
```

O backend precisa estar no ar em `http://localhost:8080` (veja o [README da raiz](../../README.md)). O CORS do servidor já libera `localhost:5173`.

## Scripts

| Script | O que faz |
| ------ | --------- |
| `npm run dev` | Servidor de desenvolvimento |
| `npm run build` | `tsc -b` seguido de `vite build` |
| `npm run preview` | Serve o build de produção |
| `npm run typecheck` | `tsc -b --noEmit` |
| `npm run format` / `format:fix` | Prettier em modo verificação / escrita |
| `npm run lint` | ESLint |
| `npm run generate:api` | Regenera `src/generated/` via orval |

**Toda alteração precisa passar por `npm run typecheck` sem erros e `npm run format` limpo** antes de ser considerada pronta. Use `format:fix` para corrigir a formatação.

## Estrutura

```
src/
├── components/ui/     # Componentes shadcn (adicionados via CLI)
├── lib/
│   ├── api.ts         # Instância axios: injeta o Bearer token, redireciona para /login no 401
│   ├── queryClient.ts # QueryClient do react-query
│   └── utils.ts       # cn()
├── generated/         # ⚠️ Gerado pelo orval — nunca editar à mão
├── index.css          # Tokens de tema (shadcn neutral) e camada base do Tailwind
└── App.tsx
```

## Client da API (orval)

O client (`react-query` + schemas `zod`) é gerado a partir do OpenAPI do backend:

```bash
# com o backend rodando
npm run generate:api
```

A saída vai para `src/generated/api` e `src/generated/models`, e o `clean: true` **apaga e reescreve a pasta inteira** a cada execução. Nunca edite nada em `src/generated/`: ajuste a fonte (as anotações OpenAPI no backend) ou o `override`/`mutator` do `orval.config.ts`.

Todas as requisições passam pelo mutator `api` de `src/lib/api.ts`, que já anexa o token do `localStorage`.

### Dois tropeços conhecidos

1. **`npm run generate:api` retorna 404.** O `orval.config.ts` aponta para `${API_URL}/openapi.json`, mas o SpringDoc publica em `/v3/api-docs`. Alinhe os dois lados antes de gerar — ou `springdoc.api-docs.path=/openapi.json` no `application.properties` do servidor, ou a URL no `orval.config.ts`.
2. **`API_URL` não chega ao browser.** `src/lib/api.ts` lê `import.meta.env.API_URL`, mas o Vite só expõe variáveis com o prefixo `VITE_`. Hoje esse valor é `undefined` e o axios acaba usando URL relativa. A correção é renomear para `VITE_API_URL` no `.env` e no `api.ts`. No `orval.config.ts` a leitura é `process.env.API_URL` via dotenv, no Node — essa funciona como está.

## Design system

`src/index.css` define os tokens do shadcn (base *neutral*, `--radius: 0.625rem`) e carrega a Inter Variable. O protótipo em `design/prototype.pen` usa exatamente essa base — Inter, raio de 10 px, superfícies neutras, ação primária quase preta, e ações destrutivas em fundo suave em vez de botão sólido vermelho.

Convenções que o protótipo estabelece e que valem ao implementar:

- **Status da reserva** (`PENDING` / `APPROVED` / `REJECTED`) e **perfil** (`USER` / `ADMIN` / `OWNER`) são badges de fundo suave com texto forte e um ponto colorido.
- **Header da aplicação:** título da página à esquerda; à direita, ação primária, notificações e a conta (avatar com iniciais). Busca **não** fica no header — vai junto da lista que ela filtra.
- **Slots** de 1 hora têm quatro estados: livre, selecionado, reservado e encerrado (horário que já passou hoje).

## Componentes shadcn

Adicione componentes pelo CLI, não à mão — o `components.json` já está configurado (alias `@/components/ui`, ícones lucide):

```bash
npx shadcn@latest add <componente>
```
