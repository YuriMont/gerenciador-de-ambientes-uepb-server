import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import type { Environment, SaveEnvironmentRequest } from "./types";

const ENVIRONMENTS_KEY = ["environments"];

/** Lista todos os ambientes cadastrados. */
export function useEnvironments() {
  return useQuery({
    queryKey: ENVIRONMENTS_KEY,
    queryFn: async () => (await api.get<Environment[]>("/environments")).data,
  });
}

/** Busca um ambiente pelo id. */
export function useEnvironment(environmentId: string | undefined) {
  return useQuery({
    queryKey: ["environments", environmentId],
    queryFn: async () =>
      (await api.get<Environment>(`/environments/${environmentId}`)).data,
    enabled: Boolean(environmentId),
  });
}

/** Cria um ambiente. Requer perfil ADMIN ou OWNER. */
export function useCreateEnvironment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (body: SaveEnvironmentRequest) =>
      (await api.post<Environment>("/environments", body)).data,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ENVIRONMENTS_KEY });
      void queryClient.invalidateQueries({ queryKey: ["availability"] });
    },
  });
}

/** Atualiza um ambiente. Requer perfil ADMIN ou OWNER. */
export function useUpdateEnvironment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      id,
      ...body
    }: SaveEnvironmentRequest & { id: string }) =>
      (await api.put<Environment>(`/environments/${id}`, body)).data,
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ENVIRONMENTS_KEY });
    },
  });
}

/** Exclui um ambiente. Requer perfil ADMIN ou OWNER. */
export function useDeleteEnvironment() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (environmentId: string) => {
      await api.delete(`/environments/${environmentId}`);
    },
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ENVIRONMENTS_KEY });
      void queryClient.invalidateQueries({ queryKey: ["availability"] });
    },
  });
}
