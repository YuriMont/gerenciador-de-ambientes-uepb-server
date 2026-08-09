import {
  Building2,
  CalendarCheck,
  Check,
  Hourglass,
  Info,
  MapPin,
  Plus,
  Search,
  Users,
} from "lucide-react";
import { useMemo, useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import { useCreateEnvironment, useEnvironments } from "@/api/environments";
import { useAllAvailability, useDashboard } from "@/api/reserves";
import type { Environment, EnvironmentAvailability } from "@/api/types";
import { PageHeader } from "@/components/app-shell";
import { MiniAgenda } from "@/components/mini-agenda";
import { StatCard } from "@/components/stat-card";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Empty,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty";
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import {
  InputGroup,
  InputGroupAddon,
  InputGroupInput,
} from "@/components/ui/input-group";
import { Skeleton } from "@/components/ui/skeleton";
import { Spinner } from "@/components/ui/spinner";
import { Textarea } from "@/components/ui/textarea";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { apiErrorMessage } from "@/lib/api";
import { useAuth } from "@/lib/auth-context";
import { todayIso } from "@/lib/format";

type Category = "todos" | "salas" | "laboratorios" | "auditorios";

const CATEGORIES: { value: Category; label: string }[] = [
  { value: "todos", label: "Todos" },
  { value: "salas", label: "Salas de aula" },
  { value: "laboratorios", label: "Laboratórios" },
  { value: "auditorios", label: "Auditórios" },
];

/**
 * Classifica o ambiente pelo nome. O cadastro não tem campo de tipo, então os filtros
 * da tela usam a mesma leitura que uma pessoa faria ao ler a lista.
 */
function categoryOf(environment: Environment): Category {
  const name = environment.name.toLocaleLowerCase("pt-BR");
  if (name.includes("laborat")) return "laboratorios";
  if (name.includes("auditó") || name.includes("audito")) return "auditorios";
  if (name.includes("sala")) return "salas";
  return "todos";
}

/** Formulário de criação de ambiente, aberto a partir do botão do cabeçalho. */
function NewEnvironmentDialog() {
  const createEnvironment = useCreateEnvironment();

  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [capacity, setCapacity] = useState("");
  const [block, setBlock] = useState("");
  const [error, setError] = useState<string | null>(null);

  const isIncomplete =
    !name.trim() || !description.trim() || !capacity.trim() || !block.trim();

  function handleOpenChange(next: boolean) {
    setOpen(next);
    if (!next) {
      setName("");
      setDescription("");
      setCapacity("");
      setBlock("");
      setError(null);
    }
  }

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);

    const seats = Number(capacity);
    if (!Number.isInteger(seats) || seats < 1) {
      setError(
        "A capacidade precisa ser um número inteiro de pelo menos 1 lugar.",
      );
      return;
    }

    createEnvironment.mutate(
      {
        name: name.trim(),
        description: description.trim(),
        capacity: seats,
        block: block.trim(),
      },
      {
        onSuccess: () => {
          toast.success("Ambiente criado. Já está disponível para reserva.");
          handleOpenChange(false);
        },
        onError: (mutationError) =>
          setError(
            apiErrorMessage(
              mutationError,
              "Não foi possível criar o ambiente.",
            ),
          ),
      },
    );
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        <Button>
          <Plus data-icon="inline-start" />
          Novo ambiente
        </Button>
      </DialogTrigger>

      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Novo ambiente</DialogTitle>
          <DialogDescription>
            Fica disponível para reserva assim que for salvo.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} id="new-environment" noValidate>
          <FieldGroup>
            <Field data-invalid={error ? true : undefined}>
              <FieldLabel htmlFor="environment-name">
                Nome do ambiente
              </FieldLabel>
              <Input
                id="environment-name"
                required
                placeholder="Laboratório de Química Analítica"
                value={name}
                onChange={(event) => setName(event.target.value)}
              />
            </Field>

            <div className="grid gap-4 sm:grid-cols-2">
              <Field data-invalid={error ? true : undefined}>
                <FieldLabel htmlFor="environment-capacity">
                  Capacidade
                </FieldLabel>
                <Input
                  id="environment-capacity"
                  type="number"
                  min={1}
                  required
                  placeholder="28"
                  value={capacity}
                  onChange={(event) => setCapacity(event.target.value)}
                />
                <FieldDescription>
                  Lugares disponíveis no espaço.
                </FieldDescription>
              </Field>

              <Field>
                <FieldLabel htmlFor="environment-block">Bloco</FieldLabel>
                <Input
                  id="environment-block"
                  required
                  placeholder="Bloco C · Térreo"
                  value={block}
                  onChange={(event) => setBlock(event.target.value)}
                />
                <FieldDescription>
                  Onde encontrar o ambiente no campus.
                </FieldDescription>
              </Field>
            </div>

            <Field>
              <FieldLabel htmlFor="environment-description">
                Descrição
              </FieldLabel>
              <Textarea
                id="environment-description"
                required
                rows={4}
                placeholder="28 bancadas, capela de exaustão e armário de reagentes."
                value={description}
                onChange={(event) => setDescription(event.target.value)}
              />
              <FieldDescription>
                Aparece no card do ambiente e ajuda quem está escolhendo onde
                reservar.
              </FieldDescription>
              {error && <FieldError>{error}</FieldError>}
            </Field>

            <Alert>
              <Info />
              <AlertDescription>
                Todo ambiente novo abre com os 14 horários de 1 hora entre 08:00
                e 22:00.
              </AlertDescription>
            </Alert>
          </FieldGroup>
        </form>

        <DialogFooter>
          <DialogClose asChild>
            <Button variant="outline">Cancelar</Button>
          </DialogClose>
          <Button
            type="submit"
            form="new-environment"
            disabled={createEnvironment.isPending || isIncomplete}
          >
            {createEnvironment.isPending ? (
              <Spinner data-icon="inline-start" />
            ) : (
              <Check data-icon="inline-start" />
            )}
            Criar ambiente
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

/** Card de um ambiente, com a agenda de hoje resumida em 14 barras. */
function EnvironmentCard({
  environment,
  availability,
}: {
  environment: Environment;
  availability?: EnvironmentAvailability;
}) {
  return (
    <Link
      to={`/ambientes/${environment.id}`}
      className="group flex flex-col overflow-hidden rounded-xl border border-border bg-card transition-colors hover:border-primary/40"
    >
      <div className="flex h-28 items-center justify-center bg-accent-soft">
        {environment.imageUrl ? (
          <img
            src={environment.imageUrl}
            alt=""
            className="size-full object-cover"
            loading="lazy"
          />
        ) : (
          <Building2 className="size-7 text-accent-strong/60" />
        )}
      </div>

      <div className="flex flex-1 flex-col gap-3.5 p-4">
        <div className="flex flex-col gap-1">
          <h3 className="text-sm font-semibold text-foreground group-hover:text-primary">
            {environment.name}
          </h3>
          <p className="line-clamp-2 text-xs leading-relaxed text-muted-foreground">
            {environment.description}
          </p>
        </div>

        <div className="flex flex-wrap gap-1.5">
          {environment.capacity !== null && (
            <span className="flex items-center gap-1 rounded-4xl bg-muted px-2 py-1 text-[11px] font-medium text-muted-foreground">
              <Users className="size-3" />
              {environment.capacity} lugares
            </span>
          )}
          {environment.block && (
            <span className="flex items-center gap-1 rounded-4xl bg-muted px-2 py-1 text-[11px] font-medium text-muted-foreground">
              <MapPin className="size-3" />
              {environment.block}
            </span>
          )}
        </div>

        <div className="mt-auto flex flex-col gap-2">
          {availability ? (
            <>
              <MiniAgenda slots={availability.slots} />
              <div className="flex items-baseline justify-between gap-3">
                <span className="text-xs text-muted-foreground">
                  {availability.freeSlots} de {availability.totalSlots} horários
                  livres hoje
                </span>
                <span className="text-xs font-medium text-subtle">08h–22h</span>
              </div>
            </>
          ) : (
            <Skeleton className="h-8" />
          )}
        </div>
      </div>
    </Link>
  );
}

export default function EnvironmentsPage() {
  const { isAdmin } = useAuth();
  const today = todayIso();

  const { data: environments, isLoading } = useEnvironments();
  const { data: availability } = useAllAvailability(today);
  const { data: dashboard } = useDashboard(isAdmin);

  const [category, setCategory] = useState<Category>("todos");
  const [search, setSearch] = useState("");

  const availabilityById = useMemo(
    () =>
      new Map((availability ?? []).map((item) => [item.environmentId, item])),
    [availability],
  );

  const filtered = useMemo(() => {
    const term = search.trim().toLocaleLowerCase("pt-BR");

    return (environments ?? []).filter((environment) => {
      const matchesCategory =
        category === "todos" || categoryOf(environment) === category;
      const matchesSearch =
        term.length === 0 ||
        environment.name.toLocaleLowerCase("pt-BR").includes(term) ||
        environment.description.toLocaleLowerCase("pt-BR").includes(term) ||
        (environment.block ?? "").toLocaleLowerCase("pt-BR").includes(term);
      return matchesCategory && matchesSearch;
    });
  }, [category, environments, search]);

  // Só horários confirmados contam: os que já passaram saem de `freeSlots` sem ter sido reservados.
  const reservedHoursToday = (availability ?? []).reduce(
    (total, item) =>
      total + item.slots.filter((slot) => slot.status === "RESERVED").length,
    0,
  );

  return (
    <>
      <PageHeader
        title="Ambientes"
        description="Espaços disponíveis para reserva"
        action={isAdmin ? <NewEnvironmentDialog /> : undefined}
      />

      <main className="flex flex-1 flex-col gap-5 bg-canvas p-5 lg:p-8">
        <div className="grid gap-3 sm:grid-cols-3">
          <StatCard
            icon={Building2}
            value={environments?.length ?? 0}
            label="Ambientes ativos"
          />
          <StatCard
            icon={CalendarCheck}
            value={`${reservedHoursToday} h`}
            label="Reservadas hoje"
          />
          <StatCard
            icon={Hourglass}
            value={dashboard?.pendingCount ?? "—"}
            label="Aguardando aprovação"
          />
        </div>

        <div className="flex flex-wrap items-end justify-between gap-4">
          <div className="flex flex-col">
            <h2 className="text-sm font-semibold text-foreground">
              Todos os ambientes
            </h2>
            <p className="text-xs text-muted-foreground">
              Clique para ver a agenda do dia
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            <ToggleGroup
              type="single"
              value={category}
              onValueChange={(value) => value && setCategory(value as Category)}
              variant="outline"
            >
              {CATEGORIES.map((item) => (
                <ToggleGroupItem key={item.value} value={item.value}>
                  {item.label}
                </ToggleGroupItem>
              ))}
            </ToggleGroup>

            <InputGroup className="w-full sm:w-64">
              <InputGroupAddon>
                <Search />
              </InputGroupAddon>
              <InputGroupInput
                placeholder="Buscar por nome ou bloco"
                aria-label="Buscar ambiente"
                value={search}
                onChange={(event) => setSearch(event.target.value)}
              />
            </InputGroup>
          </div>
        </div>

        {isLoading ? (
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {Array.from({ length: 6 }, (_, index) => (
              <Skeleton key={index} className="h-64 rounded-xl" />
            ))}
          </div>
        ) : filtered.length > 0 ? (
          <>
            <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
              {filtered.map((environment) => (
                <EnvironmentCard
                  key={environment.id}
                  environment={environment}
                  availability={availabilityById.get(environment.id)}
                />
              ))}
            </div>
            <p className="text-xs text-subtle">
              Mostrando {filtered.length} de {environments?.length ?? 0}{" "}
              ambientes
            </p>
          </>
        ) : (
          <Empty className="rounded-xl border border-border bg-card py-12">
            <EmptyHeader>
              <EmptyMedia variant="icon">
                <Building2 />
              </EmptyMedia>
              <EmptyTitle>Nenhum ambiente encontrado</EmptyTitle>
              <EmptyDescription>
                {environments?.length
                  ? "Ajuste a busca ou escolha outro tipo de espaço."
                  : "Cadastre o primeiro ambiente para abrir a agenda de reservas."}
              </EmptyDescription>
            </EmptyHeader>
          </Empty>
        )}
      </main>
    </>
  );
}
