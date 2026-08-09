/**
 * Tipos das respostas e requisições da API do backend (`apps/server`).
 *
 * Enquanto o backend não estiver no ar para rodar `npm run generate:api`, estes tipos
 * substituem o que o orval geraria em `src/generated/`. Ao gerar o client, troque os imports
 * daqui pelos módulos gerados — os formatos são os mesmos.
 */

/** Perfis de acesso do portal. */
export type UserRole = "USER" | "ADMIN" | "OWNER";

/** Estados possíveis de uma solicitação de reserva. */
export type ReserveStatus = "PENDING" | "APPROVED" | "REJECTED";

/** Situação de um horário na agenda de um ambiente. */
export type SlotStatus = "AVAILABLE" | "RESERVED" | "CLOSED";

/** Intervalo de 1 hora de uma reserva. Horários no formato `HH:mm:ss`. */
export interface Slot {
  startTime: string;
  endTime: string;
}

/** Um horário da agenda com o motivo de estar indisponível. */
export interface SlotAvailability extends Slot {
  status: SlotStatus;
}

/** Ambiente físico disponível para reserva. */
export interface Environment {
  id: string;
  name: string;
  description: string;
  /** Quantidade de lugares. `null` em ambientes cadastrados antes do campo existir. */
  capacity: number | null;
  /** Bloco onde o ambiente fica. `null` em ambientes cadastrados antes do campo existir. */
  block: string | null;
  imageUrl?: string | null;
  createdAt?: string;
  updatedAt?: string;
}

/** Agenda de um dia de um ambiente, usada na mini-agenda dos cards. */
export interface EnvironmentAvailability {
  environmentId: string;
  name: string;
  date: string;
  freeSlots: number;
  totalSlots: number;
  slots: SlotAvailability[];
}

/** Reserva com o ambiente e o solicitante já resolvidos pelo servidor. */
export interface Reserve {
  id: string;
  environmentId: string;
  environmentName: string | null;
  environmentBlock: string | null;
  environmentCapacity: number | null;
  userId: string;
  userName: string | null;
  userEmail: string | null;
  date: string;
  slots: Slot[];
  numberOfParticipants: number;
  justification: string;
  status: ReserveStatus;
  createdAt: string | null;
  updatedAt: string | null;
}

/** Usuário autenticado. */
export interface CurrentUser {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  createdAt: string | null;
}

/** Usuário na listagem da administração, com o total de reservas solicitadas. */
export interface UserSummary extends CurrentUser {
  reserveCount: number;
}

/** Horas confirmadas de um ambiente no mês. */
export interface EnvironmentUsage {
  environmentId: string;
  name: string | null;
  hours: number;
}

/** Indicadores e listas da tela de início. */
export interface Dashboard {
  pendingCount: number;
  approvedToday: number;
  environmentCount: number;
  weeklyOccupancyRate: number;
  todaySchedule: Reserve[];
  pendingQueue: Reserve[];
  topEnvironments: EnvironmentUsage[];
}

/** Credenciais enviadas no login. */
export interface LoginRequest {
  email: string;
  password: string;
}

/** Dados enviados no cadastro de uma pessoa. */
export interface RegisterUserRequest {
  name: string;
  email: string;
  password: string;
}

/** Dados enviados na criação ou edição de um ambiente. */
export interface SaveEnvironmentRequest {
  name: string;
  description: string;
  capacity: number;
  block: string;
}

/** Dados enviados na solicitação de uma reserva. */
export interface CreateReserveRequest {
  date: string;
  environmentId: string;
  numberOfParticipants: number;
  justification: string;
  slots: Slot[];
}
