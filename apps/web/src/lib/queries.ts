import { useQueryClient } from "@tanstack/react-query";

/**
 * Os hooks do client gerado não invalidam o cache ao mutar; a renovação fica
 * por conta de quem chama. Estes `useInvalidate*` restauram o comportamento que
 * os módulos manuais de `@/api` tinham, apontando para as chaves geradas pelo orval
 * (todas começam com o caminho relativo do endpoint).
 */
function firstKey(queryKey: unknown): string {
  const head = Array.isArray(queryKey) ? queryKey[0] : undefined;
  return typeof head === "string" ? head : "";
}

/** Invalida as consultas de reserva — mudam a cada criação, aprovação, recusa ou cancelamento. */
export function useInvalidateReserves() {
  const queryClient = useQueryClient();

  return () => {
    void queryClient.invalidateQueries({
      predicate: (query) => firstKey(query.queryKey).startsWith("/reserves"),
    });
  };
}

/** Invalida os ambientes e a agenda geral — mudam ao criar, editar ou excluir um ambiente. */
export function useInvalidateEnvironments() {
  const queryClient = useQueryClient();

  return () => {
    void queryClient.invalidateQueries({
      predicate: (query) =>
        firstKey(query.queryKey).startsWith("/environments") ||
        firstKey(query.queryKey).startsWith("/reserves/availability"),
    });
  };
}

/** Invalida as consultas de pessoas — mudam ao criar um administrador. */
export function useInvalidatePersons() {
  const queryClient = useQueryClient();

  return () => {
    void queryClient.invalidateQueries({
      predicate: (query) => firstKey(query.queryKey).startsWith("/person"),
    });
  };
}