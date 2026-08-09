import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import type {
  CreateReserveRequest,
  Dashboard,
  EnvironmentAvailability,
  Reserve,
  ReserveStatus,
  SlotAvailability,
} from "./types";

/** Invalida tudo que muda quando uma reserva é criada, aprovada, recusada ou cancelada. */
function useInvalidateReserves() {
  const queryClient = useQueryClient();

  return () => {
    void queryClient.invalidateQueries({ queryKey: ["reserves"] });
    void queryClient.invalidateQueries({ queryKey: ["availability"] });
    void queryClient.invalidateQueries({ queryKey: ["dashboard"] });
  };
}

/** Reservas do usuário autenticado, das mais recentes para as mais antigas. */
export function useMyReserves() {
  return useQuery({
    queryKey: ["reserves", "mine"],
    queryFn: async () => (await api.get<Reserve[]>("/reserves/mine")).data,
  });
}

/** Reservas de todas as pessoas. Requer perfil ADMIN ou OWNER. */
export function useReserves(params: {
  status?: ReserveStatus;
  date?: string;
  environmentId?: string;
}) {
  return useQuery({
    queryKey: ["reserves", "all", params],
    queryFn: async () =>
      (await api.get<Reserve[]>("/reserves", { params })).data,
  });
}

/** Indicadores da tela de início. Requer perfil ADMIN ou OWNER. */
export function useDashboard(enabled = true) {
  return useQuery({
    queryKey: ["dashboard"],
    queryFn: async () => (await api.get<Dashboard>("/reserves/dashboard")).data,
    enabled,
  });
}

/** Agenda do dia de todos os ambientes, para a mini-agenda dos cards. */
export function useAllAvailability(date: string) {
  return useQuery({
    queryKey: ["availability", "all", date],
    queryFn: async () =>
      (
        await api.get<EnvironmentAvailability[]>("/reserves/availability", {
          params: { date },
        })
      ).data,
  });
}

/** Os 14 horários de um ambiente em uma data, com o motivo de cada indisponibilidade. */
export function useAvailability(
  environmentId: string | undefined,
  date: string,
) {
  return useQuery({
    queryKey: ["availability", environmentId, date],
    queryFn: async () =>
      (
        await api.get<SlotAvailability[]>(
          `/reserves/${environmentId}/availability`,
          {
            params: { date },
          },
        )
      ).data,
    enabled: Boolean(environmentId),
  });
}

/** Solicita uma reserva. Nasce com status PENDING. */
export function useCreateReserve() {
  const invalidate = useInvalidateReserves();

  return useMutation({
    mutationFn: async (body: CreateReserveRequest) =>
      (await api.post("/reserves", body)).data,
    onSuccess: invalidate,
  });
}

/** Aprova uma reserva pendente. Requer perfil ADMIN ou OWNER. */
export function useApproveReserve() {
  const invalidate = useInvalidateReserves();

  return useMutation({
    mutationFn: async (reserveId: string) =>
      (await api.patch<Reserve>(`/reserves/${reserveId}/approve`)).data,
    onSuccess: invalidate,
  });
}

/** Recusa uma reserva pendente. Requer perfil ADMIN ou OWNER. */
export function useRejectReserve() {
  const invalidate = useInvalidateReserves();

  return useMutation({
    mutationFn: async (reserveId: string) =>
      (await api.patch<Reserve>(`/reserves/${reserveId}/reject`)).data,
    onSuccess: invalidate,
  });
}

/** Cancela uma reserva. */
export function useCancelReserve() {
  const invalidate = useInvalidateReserves();

  return useMutation({
    mutationFn: async (reserveId: string) => {
      await api.delete(`/reserves/${reserveId}`);
    },
    onSuccess: invalidate,
  });
}
