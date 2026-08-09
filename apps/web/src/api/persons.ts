import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import type { CurrentUser, RegisterUserRequest, UserSummary } from "./types";

/** Dados do usuário autenticado. */
export function useCurrentUser(enabled = true) {
  return useQuery({
    queryKey: ["person", "me"],
    queryFn: async () => (await api.get<CurrentUser>("/person/me")).data,
    enabled,
    retry: false,
    staleTime: 5 * 60 * 1000,
  });
}

/** Todas as pessoas cadastradas. Requer perfil ADMIN ou OWNER. */
export function useUsers(enabled = true) {
  return useQuery({
    queryKey: ["person", "list"],
    queryFn: async () => (await api.get<UserSummary[]>("/person/list")).data,
    enabled,
  });
}

/** Cria uma conta já com perfil ADMIN. Exclusivo para OWNER. */
export function useCreateAdministrator() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (body: RegisterUserRequest) =>
      (await api.post("/person/create-admin", body)).data,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["person", "list"] });
    },
  });
}
