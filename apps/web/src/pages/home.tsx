import {
  ArrowRight,
  Building2,
  CalendarCheck,
  CalendarDays,
  Check,
  CircleCheck,
  Hourglass,
  Plus,
  TrendingUp,
  X,
} from "lucide-react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import {
  useApprove,
  useDashboard,
  useFindMine,
  useReject,
} from "@/generated/api/reserves/reserves";
import type { EnvironmentUsageResponse } from "@/generated/models/environmentUsageResponse";
import type { ReserveResponse } from "@/generated/models/reserveResponse";
import { PageHeader } from "@/components/app-shell";
import { Panel } from "@/components/panel";
import { StatCard } from "@/components/stat-card";
import { StatusBadge } from "@/components/status-badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty";
import { Skeleton } from "@/components/ui/skeleton";
import { apiErrorMessage } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import {
  firstName,
  formatLongDate,
  formatShortDate,
  formatSlotRange,
  formatTime,
  greeting,
  initials,
  todayIso,
} from "@/lib/format";
import { useInvalidateReserves } from "@/lib/queries";

/** Linha da agenda do dia: faixa de horário, ambiente, solicitante e status. */
function ScheduleRow({ reserve }: { reserve: ReserveResponse }) {
  const slots = reserve.slots ?? [];
  const start = slots.reduce(
    (min, slot) => ((slot.startTime ?? min) < min ? slot.startTime ?? min : min),
    slots[0]?.startTime ?? "08:00:00",
  );
  const end = slots.reduce(
    (max, slot) => ((slot.endTime ?? max) > max ? slot.endTime ?? max : max),
    slots[0]?.endTime ?? "09:00:00",
  );

  return (
    <li className="flex items-center gap-3.5 border-b border-border py-3 last:border-0">
      <div className="flex w-12 shrink-0 flex-col text-xs">
        <span className="font-semibold text-foreground">
          {formatTime(start)}
        </span>
        <span className="text-subtle">{formatTime(end)}</span>
      </div>
      <span
        className="h-9 w-0.5 shrink-0 rounded-full bg-primary"
        aria-hidden="true"
      />
      <div className="flex min-w-0 flex-1 flex-col">
        <span className="truncate text-sm font-medium text-foreground">
          {reserve.environmentName ?? "Ambiente removido"}
        </span>
        <span className="truncate text-xs text-muted-foreground">
          {reserve.userName ?? "—"} · {reserve.numberOfParticipants} pessoas
        </span>
      </div>
      <StatusBadge status={reserve.status} />
    </li>
  );
}

/** Item da fila de aprovação exibido no painel lateral. */
function PendingRow({ reserve }: { reserve: ReserveResponse }) {
  const slots = reserve.slots ?? [];
  const invalidateReserves = useInvalidateReserves();
  const approve = useApprove();
  const reject = useReject();
  const isBusy = approve.isPending || reject.isPending;

  function handleApprove() {
    approve.mutate(
      { reserveId: reserve.id ?? "" },
      {
        onSuccess: () => {
          invalidateReserves();
          toast.success("Reserva aprovada.");
        },
        onError: (error) =>
          toast.error(apiErrorMessage(error, "Não foi possível aprovar.")),
      },
    );
  }

  function handleReject() {
    reject.mutate(
      { reserveId: reserve.id ?? "" },
      {
        onSuccess: () => {
          invalidateReserves();
          toast.success("Reserva recusada. O horário voltou a ficar livre.");
        },
        onError: (error) =>
          toast.error(apiErrorMessage(error, "Não foi possível recusar.")),
      },
    );
  }

  return (
    <li className="flex items-center gap-3 border-b border-border py-3 last:border-0">
      <Avatar className="size-9 shrink-0">
        <AvatarFallback className="bg-accent-soft text-xs font-semibold text-accent-strong">
          {initials(reserve.userName)}
        </AvatarFallback>
      </Avatar>

      <div className="flex min-w-0 flex-1 flex-col">
        <span className="truncate text-sm font-medium text-foreground">
          {reserve.environmentName ?? "Ambiente removido"}
        </span>
        <span className="truncate text-xs text-muted-foreground">
          {reserve.userName ?? "—"} · {formatShortDate(reserve.date ?? "")} ·{" "}
          {formatSlotRange(slots)}
        </span>
      </div>

      <div className="flex shrink-0 gap-1">
        <Button
          variant="ghost"
          size="icon-sm"
          aria-label="Aprovar"
          disabled={isBusy}
          onClick={handleApprove}
          className="text-success"
        >
          <Check />
        </Button>
        <Button
          variant="ghost"
          size="icon-sm"
          aria-label="Recusar"
          disabled={isBusy}
          onClick={handleReject}
          className="text-danger"
        >
          <X />
        </Button>
      </div>
    </li>
  );
}

/** Barra do ranking de ambientes mais reservados no mês. */
function UsageBar({ usage, max }: { usage: EnvironmentUsageResponse; max: number }) {
  const hours = usage.hours ?? 0;
  const percentage = max === 0 ? 0 : Math.round((hours / max) * 100);

  return (
    <li className="flex flex-col gap-1.5">
      <div className="flex items-baseline justify-between gap-3">
        <span className="truncate text-xs font-medium text-foreground">
          {usage.name ?? "Ambiente removido"}
        </span>
        <span className="shrink-0 text-xs text-subtle">{hours} h</span>
      </div>
      <div className="h-1.5 overflow-hidden rounded-full bg-muted">
        <div
          className="h-full rounded-full bg-primary"
          style={{ width: `${percentage}%` }}
        />
      </div>
    </li>
  );
}

/** Painel de quem administra: contadores do dia, agenda, fila e ranking do mês. */
function AdminHome() {
  const { data, isLoading } = useDashboard();

  const topMax =
    data?.data?.topEnvironments?.reduce(
      (max, item) => Math.max(max, item.hours ?? 0),
      0,
    ) ?? 0;

  return (
    <>
      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        {isLoading ? (
          Array.from({ length: 4 }, (_, index) => (
            <Skeleton key={index} className="h-[68px]" />
          ))
        ) : (
          <>
            <StatCard
              icon={Hourglass}
              value={data?.data?.pendingCount ?? 0}
              label="Aguardando sua aprovação"
            />
            <StatCard
              icon={CalendarCheck}
              value={data?.data?.approvedToday ?? 0}
              label="Confirmadas para hoje"
            />
            <StatCard
              icon={Building2}
              value={data?.data?.environmentCount ?? 0}
              label="Ambientes ativos"
            />
            <StatCard
              icon={TrendingUp}
              value={`${data?.data?.weeklyOccupancyRate ?? 0}%`}
              label="Ocupação média da semana"
            />
          </>
        )}
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1.6fr)_minmax(0,1fr)]">
        <Panel
          title="Agenda de hoje"
          description={`${data?.data?.approvedToday ?? 0} reservas confirmadas · 08:00 às 22:00`}
        >
          {isLoading ? (
            <Skeleton className="h-56" />
          ) : data?.data && (data.data.todaySchedule?.length ?? 0) > 0 ? (
            <ul className="flex flex-col">
              {(data.data.todaySchedule ?? []).map((reserve) => (
                <ScheduleRow key={reserve.id} reserve={reserve} />
              ))}
            </ul>
          ) : (
            <Empty className="py-8">
              <EmptyHeader>
                <EmptyMedia variant="icon">
                  <CalendarDays />
                </EmptyMedia>
                <EmptyTitle>Nenhuma reserva confirmada hoje</EmptyTitle>
                <EmptyDescription>
                  As reservas aprovadas para o dia aparecem aqui em ordem de
                  horário.
                </EmptyDescription>
              </EmptyHeader>
            </Empty>
          )}
        </Panel>

        <div className="flex min-w-0 flex-col gap-4">
          <Panel
            title="Aguardando você"
            description={`${data?.data?.pendingCount ?? 0} pedidos na fila`}
            action={
              <Button variant="ghost" size="sm" asChild>
                <Link to="/aprovacoes">
                  Abrir fila
                  <ArrowRight data-icon="inline-end" />
                </Link>
              </Button>
            }
          >
            {isLoading ? (
              <Skeleton className="h-40" />
            ) : data?.data && (data.data.pendingQueue?.length ?? 0) > 0 ? (
              <ul className="flex flex-col">
                {(data.data.pendingQueue ?? []).slice(0, 3).map((reserve) => (
                  <PendingRow key={reserve.id} reserve={reserve} />
                ))}
              </ul>
            ) : (
              <p className="py-4 text-sm text-muted-foreground">
                Nada na fila. Novos pedidos aparecem aqui assim que chegam.
              </p>
            )}
          </Panel>

          <Panel title="Mais reservados no mês">
            {isLoading ? (
              <Skeleton className="h-32" />
            ) : data?.data && (data.data.topEnvironments?.length ?? 0) > 0 ? (
              <ul className="flex flex-col gap-3.5">
                {(data.data.topEnvironments ?? []).map((usage) => (
                  <UsageBar
                    key={usage.environmentId}
                    usage={usage}
                    max={topMax}
                  />
                ))}
              </ul>
            ) : (
              <p className="text-sm text-muted-foreground">
                Ainda não há reservas confirmadas neste mês.
              </p>
            )}
          </Panel>
        </div>
      </div>
    </>
  );
}

/** Painel de quem só solicita: contadores das próprias reservas e os próximos horários. */
function UserHome() {
  const { data, isLoading } = useFindMine();

  const reserves = data?.data ?? [];
  const pending = reserves.filter((reserve) => reserve.status === "PENDING");
  const approved = reserves.filter((reserve) => reserve.status === "APPROVED");
  const hours = approved.reduce(
    (total, reserve) => total + (reserve.slots?.length ?? 0),
    0,
  );
  const upcoming = approved
    .filter((reserve) => (reserve.date ?? "") >= todayIso())
    .sort((a, b) => (a.date ?? "").localeCompare(b.date ?? ""));

  return (
    <>
      <div className="grid gap-3 sm:grid-cols-3">
        {isLoading ? (
          Array.from({ length: 3 }, (_, index) => (
            <Skeleton key={index} className="h-[68px]" />
          ))
        ) : (
          <>
            <StatCard
              icon={Hourglass}
              value={pending.length}
              label="Aguardando aprovação"
            />
            <StatCard
              icon={CircleCheck}
              value={approved.length}
              label="Aprovadas"
            />
            <StatCard
              icon={CalendarDays}
              value={`${hours} h`}
              label="Horas reservadas"
            />
          </>
        )}
      </div>

      <Panel
        title="Suas próximas reservas"
        description="Somente as confirmadas, da mais próxima para a mais distante"
        action={
          <Button variant="ghost" size="sm" asChild>
            <Link to="/minhas-reservas">
              Ver todas
              <ArrowRight data-icon="inline-end" />
            </Link>
          </Button>
        }
      >
        {isLoading ? (
          <Skeleton className="h-40" />
        ) : upcoming.length > 0 ? (
          <ul className="flex flex-col">
            {upcoming.slice(0, 6).map((reserve) => (
              <li
                key={reserve.id}
                className="flex items-center gap-3.5 border-b border-border py-3 last:border-0"
              >
                <div className="flex w-24 shrink-0 flex-col text-xs">
                  <span className="font-semibold text-foreground">
                    {formatShortDate(reserve.date ?? "")}
                  </span>
                  <span className="text-subtle">
                    {formatSlotRange(reserve.slots ?? [])}
                  </span>
                </div>
                <div className="flex min-w-0 flex-1 flex-col">
                  <span className="truncate text-sm font-medium text-foreground">
                    {reserve.environmentName ?? "Ambiente removido"}
                  </span>
                  <span className="truncate text-xs text-muted-foreground">
                    {reserve.justification}
                  </span>
                </div>
                <StatusBadge status={reserve.status} />
              </li>
            ))}
          </ul>
        ) : (
          <Empty className="py-8">
            <EmptyHeader>
              <EmptyMedia variant="icon">
                <CalendarDays />
              </EmptyMedia>
              <EmptyTitle>Nenhuma reserva confirmada</EmptyTitle>
              <EmptyDescription>
                Escolha um ambiente e um horário para enviar sua primeira
                solicitação.
              </EmptyDescription>
            </EmptyHeader>
          </Empty>
        )}
      </Panel>
    </>
  );
}

export default function HomePage() {
  const { user, isAdmin } = useAuth();
  const { data: dashboard } = useDashboard({ query: { enabled: isAdmin } });

  const pendingCount = dashboard?.data?.pendingCount ?? 0;
  const subtitle = isAdmin
    ? `${formatLongDate(new Date())} · ${pendingCount} ${
        pendingCount === 1 ? "pedido espera" : "pedidos esperam"
      } sua resposta`
    : formatLongDate(new Date());

  return (
    <>
      <PageHeader
        title={`${greeting()}, ${firstName(user?.name) || "bem-vindo"}`}
        description={subtitle}
        action={
          <Button asChild>
            <Link to="/ambientes">
              <Plus data-icon="inline-start" />
              Nova reserva
            </Link>
          </Button>
        }
      />

      <main className="flex flex-1 flex-col gap-4 bg-canvas p-5 lg:p-8">
        {isAdmin ? <AdminHome /> : <UserHome />}
      </main>
    </>
  );
}
