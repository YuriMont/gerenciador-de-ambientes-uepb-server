import {
  CalendarCheck,
  CalendarDays,
  CircleCheck,
  CircleX,
  Hourglass,
  Info,
  MoreHorizontal,
  Plus,
  Search,
} from "lucide-react";
import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import { useCancel, useFindMine } from "@/generated/api/reserves/reserves";
import type { ReserveResponse } from "@/generated/models/reserveResponse";
import type { ReserveStatus } from "@/generated/models/reserveStatus";
import { PageHeader } from "@/components/app-shell";
import { StatCard } from "@/components/stat-card";
import { StatusBadge } from "@/components/status-badge";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
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
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { apiErrorMessage } from "@/lib/api";
import {
  formatLongDate,
  formatShortDate,
  formatSlotRange,
  parseIsoDate,
  todayIso,
} from "@/lib/format";
import { useInvalidateReserves } from "@/lib/queries";

type Filter = "todas" | ReserveStatus;

/** Ação por linha: abrir o ambiente ou cancelar a solicitação. */
function ReserveActions({ reserve }: { reserve: ReserveResponse }) {
  const invalidateReserves = useInvalidateReserves();
  const cancelReserve = useCancel();

  function handleCancel() {
    cancelReserve.mutate(
      { reserveId: reserve.id ?? "" },
      {
        onSuccess: () => {
          invalidateReserves();
          toast.success("Reserva cancelada.");
        },
        onError: (error) =>
          toast.error(apiErrorMessage(error, "Não foi possível cancelar.")),
      },
    );
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon-sm" aria-label="Ações da reserva">
          <MoreHorizontal />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuGroup>
          <DropdownMenuItem asChild>
            <Link to={`/ambientes/${reserve.environmentId}`}>
              Ver agenda do ambiente
            </Link>
          </DropdownMenuItem>
          <DropdownMenuItem
            variant="destructive"
            disabled={cancelReserve.isPending}
            onSelect={handleCancel}
          >
            Cancelar reserva
          </DropdownMenuItem>
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

export default function MyReservesPage() {
  const { data, isLoading } = useFindMine();

  const [filter, setFilter] = useState<Filter>("todas");
  const [search, setSearch] = useState("");

  const reserves = useMemo(() => data?.data ?? [], [data]);

  const counts = useMemo(
    () => ({
      todas: reserves.length,
      PENDING: reserves.filter((reserve) => reserve.status === "PENDING")
        .length,
      APPROVED: reserves.filter((reserve) => reserve.status === "APPROVED")
        .length,
      REJECTED: reserves.filter((reserve) => reserve.status === "REJECTED")
        .length,
    }),
    [reserves],
  );

  const hours = reserves
    .filter((reserve) => reserve.status === "APPROVED")
    .reduce((total, reserve) => total + (reserve.slots?.length ?? 0), 0);

  const filtered = useMemo(() => {
    const term = search.trim().toLocaleLowerCase("pt-BR");

    return reserves.filter((reserve) => {
      const matchesStatus = filter === "todas" || reserve.status === filter;
      const matchesSearch =
        term.length === 0 ||
        (reserve.environmentName ?? "")
          .toLocaleLowerCase("pt-BR")
          .includes(term);
      return matchesStatus && matchesSearch;
    });
  }, [filter, reserves, search]);

  const nextApproved = useMemo(() => {
    const today = todayIso();
    return reserves
      .filter(
        (reserve) =>
          reserve.status === "APPROVED" && (reserve.date ?? "") >= today,
      )
      .sort((a, b) => (a.date ?? "").localeCompare(b.date ?? ""))[0];
  }, [reserves]);

  const filters: { value: Filter; label: string }[] = [
    { value: "todas", label: `Todas · ${counts.todas}` },
    { value: "PENDING", label: `Pendentes · ${counts.PENDING}` },
    { value: "APPROVED", label: `Aprovadas · ${counts.APPROVED}` },
    { value: "REJECTED", label: `Rejeitadas · ${counts.REJECTED}` },
  ];

  return (
    <>
      <PageHeader
        title="Minhas reservas"
        description="Acompanhe o status de cada solicitação"
        action={
          <Button asChild>
            <Link to="/ambientes">
              <Plus data-icon="inline-start" />
              Nova reserva
            </Link>
          </Button>
        }
      />

      <main className="flex flex-1 flex-col gap-5 bg-canvas p-5 lg:p-8">
        <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
          <StatCard
            icon={Hourglass}
            value={counts.PENDING}
            label="Aguardando aprovação"
          />
          <StatCard
            icon={CircleCheck}
            value={counts.APPROVED}
            label="Aprovadas"
          />
          <StatCard icon={CircleX} value={counts.REJECTED} label="Rejeitadas" />
          <StatCard
            icon={CalendarDays}
            value={`${hours} h`}
            label="Horas confirmadas"
          />
        </div>

        <div className="flex flex-wrap items-end justify-between gap-4">
          <div className="flex flex-col">
            <h2 className="text-sm font-semibold text-foreground">
              Solicitações
            </h2>
            <p className="text-xs text-muted-foreground">
              Da mais recente para a mais antiga
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <ToggleGroup
              type="single"
              value={filter}
              onValueChange={(value) => value && setFilter(value as Filter)}
              variant="outline"
            >
              {filters.map((item) => (
                <ToggleGroupItem key={item.value} value={item.value}>
                  {item.label}
                </ToggleGroupItem>
              ))}
            </ToggleGroup>

            <InputGroup className="w-full sm:w-56">
              <InputGroupAddon>
                <Search />
              </InputGroupAddon>
              <InputGroupInput
                placeholder="Buscar por ambiente"
                aria-label="Buscar por ambiente"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
              />
            </InputGroup>
          </div>
        </div>

        {isLoading ? (
          <Skeleton className="h-72 rounded-xl" />
        ) : filtered.length > 0 ? (
          <div className="overflow-x-auto rounded-xl border border-border bg-card">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Ambiente</TableHead>
                  <TableHead>Data</TableHead>
                  <TableHead>Horários</TableHead>
                  <TableHead>Pessoas</TableHead>
                  <TableHead className="min-w-56">Justificativa</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="w-10" />
                </TableRow>
              </TableHeader>
              <TableBody>
                {filtered.map((reserve) => (
                  <TableRow key={reserve.id}>
                    <TableCell>
                      <span className="flex flex-col">
                        <span className="font-medium text-foreground">
                          {reserve.environmentName ?? "Ambiente removido"}
                        </span>
                        {reserve.environmentBlock && (
                          <span className="text-xs text-subtle">
                            {reserve.environmentBlock}
                          </span>
                        )}
                      </span>
                    </TableCell>
                    <TableCell>
                      <span className="flex flex-col">
                        <span>{formatShortDate(reserve.date)}</span>
                        <span className="text-xs text-subtle">
                          {reserve.date
                            ? parseIsoDate(reserve.date).getFullYear()
                            : "—"}
                        </span>
                      </span>
                    </TableCell>
                    <TableCell>
                      <span className="flex flex-col">
                        <span>{formatSlotRange(reserve.slots)}</span>
                        <span className="text-xs text-subtle">
                          {reserve.slots?.length ?? 0}{" "}
                          {reserve.slots?.length === 1 ? "slot" : "slots"}
                        </span>
                      </span>
                    </TableCell>
                    <TableCell>{reserve.numberOfParticipants}</TableCell>
                    <TableCell className="max-w-72 truncate text-muted-foreground">
                      {reserve.justification}
                    </TableCell>
                    <TableCell>
                      <StatusBadge status={reserve.status} />
                    </TableCell>
                    <TableCell>
                      <ReserveActions reserve={reserve} />
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        ) : (
          <Empty className="rounded-xl border border-border bg-card py-12">
            <EmptyHeader>
              <EmptyMedia variant="icon">
                <CalendarDays />
              </EmptyMedia>
              <EmptyTitle>Nenhuma solicitação por aqui</EmptyTitle>
              <EmptyDescription>
                {reserves.length
                  ? "Nenhuma reserva bate com esse filtro."
                  : "Escolha um ambiente e envie sua primeira solicitação."}
              </EmptyDescription>
            </EmptyHeader>
          </Empty>
        )}

        {nextApproved && (
          <div className="flex flex-wrap items-center gap-3 rounded-xl border border-border bg-card p-4">
            <span className="flex size-9 shrink-0 items-center justify-center rounded-md bg-success-soft text-success">
              <CalendarCheck className="size-4.5" />
            </span>
            <div className="flex min-w-0 flex-1 flex-col">
              <span className="truncate text-sm font-medium text-foreground">
                Próxima confirmada ·{" "}
                {nextApproved.environmentName ?? "Ambiente removido"}
              </span>
              <span className="truncate text-xs text-muted-foreground">
                {formatLongDate(nextApproved.date)} ·{" "}
                {formatSlotRange(nextApproved.slots)} ·{" "}
                {nextApproved.numberOfParticipants} participantes
              </span>
            </div>
            <Button variant="outline" size="sm" asChild>
              <Link to={`/ambientes/${nextApproved.environmentId}`}>
                Ver agenda
              </Link>
            </Button>
          </div>
        )}

        <Alert>
          <Info />
          <AlertDescription>
            Enquanto está pendente, o horário segue disponível para outras
            pessoas — a confirmação só acontece na aprovação.
          </AlertDescription>
        </Alert>
      </main>
    </>
  );
}
