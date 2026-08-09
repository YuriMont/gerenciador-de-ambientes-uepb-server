import { Check, Info, Mail, Search, ShieldCheck, Users, X } from "lucide-react";
import { useMemo, useState } from "react";
import { toast } from "sonner";
import { useApprove, useFindAll, useReject } from "@/generated/api/reserves/reserves";
import type { ReserveResponse } from "@/generated/models/reserveResponse";
import { PageHeader } from "@/components/app-shell";
import { Panel } from "@/components/panel";
import { StatusBadge } from "@/components/status-badge";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from "@/components/ui/input-group";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { apiErrorMessage } from "@/lib/api";
import {
  formatDayMonthYear,
  formatLongDate,
  formatRelative,
  formatShortDate,
  formatSlotRange,
  formatTime,
  initials,
} from "@/lib/format";
import { useInvalidateReserves } from "@/lib/queries";
import { cn } from "@/lib/utils";

/** Cartão de um pedido na fila, à esquerda. */
function QueueItem({
  reserve,
  isSelected,
  onSelect,
}: {
  reserve: ReserveResponse;
  isSelected: boolean;
  onSelect: () => void;
}) {
  return (
    <button
      type="button"
      onClick={onSelect}
      aria-current={isSelected}
      className={cn(
        "flex w-full gap-3 border-b border-border p-4 text-left transition-colors last:border-0",
        isSelected ? "bg-accent-soft/60" : "hover:bg-muted/60",
      )}
    >
      <Avatar className="size-9 shrink-0">
        <AvatarFallback className="bg-accent-soft text-xs font-semibold text-accent-strong">
          {initials(reserve.userName)}
        </AvatarFallback>
      </Avatar>

      <div className="flex min-w-0 flex-1 flex-col gap-1">
        <div className="flex items-baseline justify-between gap-2">
          <span className="truncate text-sm font-medium text-foreground">
            {reserve.userName ?? "Usuário removido"}
          </span>
          <span className="shrink-0 text-xs text-subtle">
            {formatRelative(reserve.createdAt)}
          </span>
        </div>

        <span className="truncate text-xs text-muted-foreground">
          {reserve.environmentName ?? "Ambiente removido"} ·{" "}
          {formatShortDate(reserve.date)} · {formatSlotRange(reserve.slots)}
        </span>

        <span className="line-clamp-1 text-xs text-subtle">
          {reserve.justification}
        </span>

        <span className="flex flex-wrap gap-x-4 gap-y-1 pt-0.5 text-xs text-subtle">
          <span className="flex items-center gap-1.5">
            <Users className="size-3.5" />
            {reserve.numberOfParticipants} pessoas
          </span>
          <span className="flex min-w-0 items-center gap-1.5">
            <Mail className="size-3.5 shrink-0" />
            <span className="truncate">{reserve.userEmail ?? "—"}</span>
          </span>
        </span>
      </div>
    </button>
  );
}

/** Painel de detalhe do pedido selecionado, com as duas ações de decisão. */
function RequestDetail({ reserve }: { reserve: ReserveResponse }) {
  const invalidateReserves = useInvalidateReserves();
  const approve = useApprove();
  const reject = useReject();
  const isBusy = approve.isPending || reject.isPending;
  const slots = reserve.slots ?? [];

  function handleApprove() {
    approve.mutate(
      { reserveId: reserve.id ?? "" },
      {
        onSuccess: () => {
          invalidateReserves();
          toast.success("Reserva aprovada. Os horários foram bloqueados.");
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
    <div className="flex flex-col gap-4">
      <Panel contentClassName="gap-4">
        <div className="flex flex-col gap-1.5">
          <StatusBadge status={reserve.status} />
          <h2 className="text-base font-semibold text-foreground">
            {reserve.environmentName ?? "Ambiente removido"}
          </h2>
          <p className="text-xs text-muted-foreground">
            {formatLongDate(reserve.date)}
            {reserve.environmentBlock ? ` · ${reserve.environmentBlock}` : ""}
          </p>
        </div>

        <Separator />

        <dl className="flex flex-col gap-3 text-sm">
          <div className="flex justify-between gap-4">
            <dt className="text-xs text-subtle">Solicitante</dt>
            <dd className="flex flex-col items-end text-right">
              <span className="font-medium text-foreground">
                {reserve.userName ?? "—"}
              </span>
              <span className="text-xs text-muted-foreground">
                {reserve.userEmail ?? "—"}
              </span>
            </dd>
          </div>
          <div className="flex justify-between gap-4">
            <dt className="text-xs text-subtle">Participantes</dt>
            <dd className="font-medium text-foreground">
              {reserve.environmentCapacity != null
                ? `${reserve.numberOfParticipants} de ${reserve.environmentCapacity} lugares`
                : reserve.numberOfParticipants}
            </dd>
          </div>
          <div className="flex justify-between gap-4">
            <dt className="text-xs text-subtle">Enviada em</dt>
            <dd className="font-medium text-foreground">
              {formatDayMonthYear(reserve.createdAt)}
            </dd>
          </div>
        </dl>

        <div className="flex flex-col gap-2">
          <span className="text-xs text-subtle">Horários solicitados</span>
          <div className="flex flex-wrap gap-1.5">
            {slots.map((slot) => (
              <span
                key={slot.startTime}
                className="rounded-4xl bg-accent-soft px-2.5 py-1 text-xs font-medium text-accent-strong"
              >
                {formatTime(slot.startTime)} – {formatTime(slot.endTime)}
              </span>
            ))}
          </div>
        </div>

        <div className="flex flex-col gap-2">
          <span className="text-xs text-subtle">Justificativa</span>
          <p className="rounded-lg bg-muted p-3 text-sm leading-relaxed text-foreground">
            {reserve.justification}
          </p>
        </div>

        <Alert>
          <ShieldCheck />
          <AlertDescription>
            Aprovar bloqueia {slots.length}{" "}
            {slots.length === 1 ? "horário" : "horários"} neste ambiente e data.
          </AlertDescription>
        </Alert>

        <div className="flex gap-2">
          <Button className="flex-1" disabled={isBusy} onClick={handleApprove}>
            <Check data-icon="inline-start" />
            Aprovar
          </Button>
          <Button
            variant="destructive"
            className="flex-1"
            disabled={isBusy}
            onClick={handleReject}
          >
            <X data-icon="inline-start" />
            Recusar
          </Button>
        </div>
      </Panel>

      <Alert>
        <Info />
        <AlertDescription>
          Ao recusar, o horário volta a ficar livre para outras solicitações.
        </AlertDescription>
      </Alert>
    </div>
  );
}

export default function ApprovalsPage() {
  const { data, isLoading } = useFindAll({ status: "PENDING" });
  const [search, setSearch] = useState("");
  const [selectedId, setSelectedId] = useState<string | null>(null);

  const queue = useMemo(() => {
    const reserves = data?.data ?? [];
    const term = search.trim().toLocaleLowerCase("pt-BR");
    if (!term) return reserves;

    return reserves.filter(
      (reserve) =>
        (reserve.userName ?? "").toLocaleLowerCase("pt-BR").includes(term) ||
        (reserve.environmentName ?? "")
          .toLocaleLowerCase("pt-BR")
          .includes(term),
    );
  }, [data, search]);

  // O detalhe segue a escolha da pessoa e cai no primeiro da fila quando o pedido
  // aberto sai da lista — o que acontece assim que ele é aprovado ou recusado.
  const selected =
    queue.find((reserve) => reserve.id === selectedId) ?? queue[0];
  const pendingCount = data?.data?.length ?? 0;

  return (
    <>
      <PageHeader
        title="Aprovar reservas"
        description={`${pendingCount} ${
          pendingCount === 1
            ? "solicitação aguardando"
            : "solicitações aguardando"
        } análise`}
      />

      <main className="flex flex-1 flex-col gap-4 bg-canvas p-5 lg:p-8">
        <div className="grid gap-4 xl:grid-cols-[minmax(0,1.4fr)_minmax(0,1fr)]">
          <Panel
            title="Fila de aprovação"
            description="Mais antigas primeiro"
            contentClassName="gap-0"
            action={
              <InputGroup className="w-full sm:w-64">
                <InputGroupAddon>
                  <Search />
                </InputGroupAddon>
                <InputGroupInput
                  placeholder="Buscar por pessoa ou ambiente"
                  aria-label="Buscar na fila"
                  value={search}
                  onChange={(event) => setSearch(event.target.value)}
                />
              </InputGroup>
            }
          >
            {isLoading ? (
              <Skeleton className="h-64" />
            ) : queue.length > 0 ? (
              <div className="-mx-5 -mb-5 flex flex-col border-t border-border">
                {queue.map((reserve) => (
                  <QueueItem
                    key={reserve.id}
                    reserve={reserve}
                    isSelected={reserve.id === selected?.id}
                    onSelect={() => setSelectedId(reserve.id ?? null)}
                  />
                ))}
              </div>
            ) : (
              <Empty className="py-10">
                <EmptyHeader>
                  <EmptyMedia variant="icon">
                    <ShieldCheck />
                  </EmptyMedia>
                  <EmptyTitle>Fila vazia</EmptyTitle>
                  <EmptyDescription>
                    {pendingCount
                      ? "Nenhum pedido bate com essa busca."
                      : "Todos os pedidos foram respondidos. Novos chegam aqui automaticamente."}
                  </EmptyDescription>
                </EmptyHeader>
              </Empty>
            )}
          </Panel>

          {selected ? (
            <RequestDetail reserve={selected} />
          ) : (
            <Panel contentClassName="items-center gap-2 py-10 text-center">
              <Users className="size-6 text-subtle" />
              <p className="text-sm font-medium text-foreground">
                Nenhum pedido selecionado
              </p>
              <p className="text-xs text-muted-foreground">
                Escolha uma solicitação na fila para ver os detalhes e decidir.
              </p>
            </Panel>
          )}
        </div>
      </main>
    </>
  );
}
