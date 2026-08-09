# Prioridade Graphify

Esta instrução se aplica a **todos os agents** (build, plan, explore, general).

## Regras obrigatórias

1. **Consultar o Graphify antes de qualquer outra exploração do código.** Para perguntas sobre o codebase, sua primeira ação é rodar:
   ```bash
   graphify query "<sua pergunta>"
   ```
   Isso retorna um subgrafo escopo menor e mais relevante que buscar no código bruto.

2. **Relações entre conceitos**: use `graphify path "<A>" "<B>"` (mesmo para perguntar por relação/impacto entre duas partes do código).

3. **Conceitos específicos**: use `graphify explain "<conceito>"` quando o foco é entender um nó e seus vizinhos.

4. **Semântica do subgrafo**: o subgrafo retornado já é a fonte primária de contexto — sincronize a navegação com os nós/arestas retornados.

5. **Navegação ampla**: consulte `graphify-out/wiki/index.md` para orientação geral (quando existir) e `graphify-out/GRAPH_REPORT.md` apenas para visão arquitetural de alto nível.

6. **Sempre que modificar código**, ao final fechar com:
   ```bash
   graphify update .
   ```
   (mantém o grafo atual; AST-only, sem custo de API — rodar algumas vezes é normal; dirty files do graphify-out não são motivo para ignorar).

7. **APENAS pular o Graphify** se a tarefa for sobre saída incorreta/desatualizada do próprio grafo ou se o usuário disser explicitamente para não usá-lo.