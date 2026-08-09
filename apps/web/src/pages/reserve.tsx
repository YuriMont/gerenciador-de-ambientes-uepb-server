import {
  ArrowLeft,
  Check,
  ChevronRight,
  Hourglass,
  MapPin,
  Send,
  Timer,
  Users,
  X,
} from "lucide-react";
import { useMemo, useState, type FormEvent } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { toast } from "sonner";
import { useEnvironment } from "@/api/environments";
import { useAvailability, useCreateReserve } from "@/api/reserves";
import type { SlotAvailability } from "@/api/types";
import { PageHeader } from "@/components/app-shell";
import { Panel } from "@/components/panel";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import { Spinner } from "@/components/ui/spinner";
import { Textarea } from "@/components/ui/textarea";
import { apiErrorMessage } from "@/lib/api";
import { addDays, formatLongDate, formatTime, toIsoDate } from "@/lib/format";
import { cn } from "@/lib/utils";

const RULES = [
  "Cada slot dura exatamente 1 hora e começa em hora cheia.",
  "Só é possível reservar hoje ou datas futuras.",
  "Horários que já passaram no dia de hoje ficam indisponíveis.",
  "Um horário já confirmado não aceita nova solicitação.",
];

/** Faixa com os próximos sete dias, a partir de hoje. */
function DayStrip({
  value,
  onChange,
}: {
  value: string;
  onChange: (date: string) => void;
}) {
  const days = useMemo(() => {
    const today = new Date();
    return Array.from({ length: 7 }, (_, index) => addDays(today, index));
  }, []);

  return (
    <div className="flex gap-2 overflow-x-auto pb-1">
      {days.map((day) => {
        const iso = toIsoDate(day);
        const isSelected = iso === value;

        return (
          <button
            key={iso}
            type="button"
            onClick={() => onChange(iso)}
            aria-pressed={isSelected}
            className={cn(
              "flex min-w-14 flex-col items-center gap-0.5 rounded-lg border px-3 py-2 transition-colors",
              isSelected
                ? "border-primary bg-primary text-primary-foreground"
                : "border-border bg-card text-muted-foreground hover:border-primary/40",
            )}
          >
            <span className="text-[10px] font-semibold tracking-wider uppercase">
              {day
                .toLocaleDateString("pt-BR", { weekday: "short" })
                .replace(".", "")}
            </span>
            <span className="text-sm font-semibold">{day.getDate()}</span>
          </button>
        );
      })}
    </div>
  );
}

/** Legenda dos quatro estados possíveis de um horário. */
function SlotLegend() {
  const items = [
    { label: "Livre", className: "bg-card border border-border" },
    { label: "Selecionado", className: "bg-primary" },
    {
      label: "Reservado",
      className: "bg-accent-soft border border-primary/30",
    },
    { label: "Encerrado", className: "bg-muted" },
  ];

  return (
    <div className="flex flex-wrap items-center gap-3">
      {items.map((item) => (
        <span
          key={item.label}
          className="flex items-center gap-1.5 text-xs text-muted-foreground"
        >
          <span
            className={cn("size-3 rounded-sm", item.className)}
            aria-hidden="true"
          />
          {item.label}
        </span>
      ))}
    </div>
  );
}

/** Botão de um horário de 1 hora. */
function SlotButton({
  slot,
  isSelected,
  onToggle,
}: {
  slot: SlotAvailability;
  isSelected: boolean;
  onToggle: () => void;
}) {
  const isDisabled = slot.status !== "AVAILABLE";
  const caption =
    slot.status === "RESERVED"
      ? "reservado"
      : slot.status === "CLOSED"
        ? "encerrado"
        : isSelected
          ? "selecionado"
          : `${formatTime(slot.startTime).slice(0, 2)}–${formatTime(slot.endTime).slice(0, 2)}h`;

  return (
    <button
      type="button"
      disabled={isDisabled}
      aria-pressed={isSelected}
      onClick={onToggle}
      className={cn(
        "flex flex-col items-center gap-0.5 rounded-lg border px-2 py-2.5 text-xs transition-colors",
        isDisabled &&
          slot.status === "RESERVED" &&
          "border-primary/30 bg-accent-soft text-accent-strong",
        isDisabled &&
          slot.status === "CLOSED" &&
          "border-transparent bg-muted text-subtle",
        !isDisabled &&
          isSelected &&
          "border-primary bg-primary text-primary-foreground",
        !isDisabled &&
          !isSelected &&
          "border-border bg-card text-foreground hover:border-primary/40 hover:bg-accent-soft",
      )}
    >
      <span className="font-semibold">{formatTime(slot.startTime)}</span>
      <span className="text-[10px] opacity-80">{caption}</span>
    </button>
  );
}

export default function ReservePage() {
  const { environmentId } = useParams<{ environmentId: string }>();
  const navigate = useNavigate();

  const [date, setDate] = useState(() => toIsoDate(new Date()));
  const [selected, setSelected] = useState<string[]>([]);
  const [participants, setParticipants] = useState("");
  const [justification, setJustification] = useState("");
  const [error, setError] = useState<string | null>(null);

  const { data: environment, isLoading: isLoadingEnvironment } =
    useEnvironment(environmentId);
  const { data: slots, isLoading: isLoadingSlots } = useAvailability(
    environmentId,
    date,
  );
  const createReserve = useCreateReserve();

  const selectedSlots = useMemo(
    () => (slots ?? []).filter((slot) => selected.includes(slot.startTime)),
    [selected, slots],
  );

  function toggleSlot(startTime: string) {
    setSelected((current) =>
      current.includes(startTime)
        ? current.filter((item) => item !== startTime)
        : [...current, startTime],
    );
  }

  function handleDateChange(next: string) {
    setDate(next);
    setSelected([]);
  }

  function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setError(null);

    if (selectedSlots.length === 0) {
      setError("Escolha ao menos um horário na agenda.");
      return;
    }

    const numberOfParticipants = Number(participants);
    if (!Number.isFinite(numberOfParticipants) || numberOfParticipants < 1) {
      setError("Informe quantas pessoas vão usar o espaço.");
      return;
    }

    if (
      environment?.capacity != null &&
      numberOfParticipants > environment.capacity
    ) {
      setError(`O ambiente tem ${environment.capacity} lugares.`);
      return;
    }

    if (!justification.trim()) {
      setError(
        "A justificativa é obrigatória — é o que o administrador lê ao avaliar.",
      );
      return;
    }

    createReserve.mutate(
      {
        date,
        environmentId: environmentId!,
        numberOfParticipants,
        justification: justification.trim(),
        slots: selectedSlots.map((slot) => ({
          startTime: slot.startTime,
          endTime: slot.endTime,
        })),
      },
      {
        onSuccess: () => {
          toast.success(
            "Solicitação enviada. Ela fica pendente até um administrador responder.",
          );
          navigate("/minhas-reservas");
        },
        onError: (mutationError) =>
          setError(
            apiErrorMessage(
              mutationError,
              "Não foi possível enviar a solicitação.",
            ),
          ),
      },
    );
  }

  return (
    <>
      <PageHeader
        title={environment?.name ?? "Reservar ambiente"}
        description={formatLongDate(date)}
        leading={
          <Button variant="ghost" size="icon" aria-label="Voltar" asChild>
            <Link to="/ambientes">
              <ArrowLeft />
            </Link>
          </Button>
        }
      />

      <main className="flex flex-1 flex-col gap-4 bg-canvas p-5 lg:p-8">
        <nav
          className="flex items-center gap-1 text-xs text-subtle"
          aria-label="Trilha"
        >
          <Link to="/ambientes" className="hover:text-foreground">
            Ambientes
          </Link>
          <ChevronRight className="size-3" />
          {environment?.block && (
            <>
              <span>{environment.block}</span>
              <ChevronRight className="size-3" />
            </>
          )}
          <span className="text-muted-foreground">
            {environment?.name ?? "—"}
          </span>
        </nav>

        <div className="grid gap-4 xl:grid-cols-[minmax(0,1.55fr)_minmax(0,1fr)]">
          <div className="flex min-w-0 flex-col gap-4">
            <Panel
              title="Escolha os horários"
              description="Slots de 1 hora, das 08:00 às 22:00"
              action={<SlotLegend />}
              contentClassName="gap-4"
            >
              <DayStrip value={date} onChange={handleDateChange} />

              {isLoadingSlots ? (
                <Skeleton className="h-28" />
              ) : (
                <div className="grid grid-cols-4 gap-2 sm:grid-cols-7">
                  {(slots ?? []).map((slot) => (
                    <SlotButton
                      key={slot.startTime}
                      slot={slot}
                      isSelected={selected.includes(slot.startTime)}
                      onToggle={() => toggleSlot(slot.startTime)}
                    />
                  ))}
                </div>
              )}
            </Panel>

            <Panel title="Sobre o ambiente" contentClassName="gap-3">
              {isLoadingEnvironment ? (
                <Skeleton className="h-16" />
              ) : (
                <p className="text-sm leading-relaxed text-muted-foreground">
                  {environment?.description ?? "Sem descrição cadastrada."}
                </p>
              )}

              <div className="flex flex-wrap gap-4 text-xs text-muted-foreground">
                {environment?.capacity != null && (
                  <span className="flex items-center gap-1.5">
                    <Users className="size-3.5" />
                    {environment.capacity} lugares
                  </span>
                )}
                <span className="flex items-center gap-1.5">
                  <MapPin className="size-3.5" />
                  {environment?.block ?? "Bloco não informado"}
                </span>
                <span className="flex items-center gap-1.5">
                  <Timer className="size-3.5" />
                  08:00 – 22:00
                </span>
              </div>
            </Panel>
          </div>

          <div className="flex min-w-0 flex-col gap-4">
            <Panel title="Solicitar reserva" contentClassName="gap-4">
              <form onSubmit={handleSubmit} noValidate>
                <FieldGroup>
                  <Field>
                    <FieldLabel>Horários selecionados</FieldLabel>
                    {selectedSlots.length > 0 ? (
                      <>
                        <div className="flex flex-wrap gap-1.5">
                          {selectedSlots.map((slot) => (
                            <button
                              key={slot.startTime}
                              type="button"
                              onClick={() => toggleSlot(slot.startTime)}
                              className="flex items-center gap-1 rounded-4xl bg-accent-soft px-2.5 py-1 text-xs font-medium text-accent-strong hover:bg-accent-soft/70"
                            >
                              {formatTime(slot.startTime)} –{" "}
                              {formatTime(slot.endTime)}
                              <X className="size-3" />
                            </button>
                          ))}
                        </div>
                        <FieldDescription>
                          {selectedSlots.length} h no total ·{" "}
                          {formatLongDate(date)}
                        </FieldDescription>
                      </>
                    ) : (
                      <FieldDescription>
                        Nenhum horário escolhido ainda. Toque nos slots livres
                        da agenda.
                      </FieldDescription>
                    )}
                  </Field>

                  <Field>
                    <FieldLabel htmlFor="participants">
                      Número de participantes
                    </FieldLabel>
                    <Input
                      id="participants"
                      max={environment?.capacity ?? undefined}
                      type="number"
                      min={1}
                      required
                      placeholder="30"
                      value={participants}
                      onChange={(event) => setParticipants(event.target.value)}
                    />
                    {environment?.capacity != null && (
                      <FieldDescription>
                        O ambiente comporta até {environment.capacity} pessoas.
                      </FieldDescription>
                    )}
                  </Field>

                  <Field data-invalid={error ? true : undefined}>
                    <FieldLabel htmlFor="justification">
                      Justificativa
                    </FieldLabel>
                    <Textarea
                      id="justification"
                      required
                      rows={4}
                      placeholder="Defesa de dissertação do PPGCC com banca externa."
                      value={justification}
                      onChange={(event) => setJustification(event.target.value)}
                    />
                    <FieldDescription>
                      Obrigatória — é o que o administrador lê ao avaliar.
                    </FieldDescription>
                    {error && <FieldError>{error}</FieldError>}
                  </Field>

                  <Alert>
                    <Hourglass />
                    <AlertDescription>
                      Ao enviar, a reserva fica pendente até um administrador
                      aprovar. O horário só é bloqueado depois disso.
                    </AlertDescription>
                  </Alert>

                  <Field>
                    <Button
                      type="submit"
                      size="lg"
                      disabled={createReserve.isPending}
                    >
                      {createReserve.isPending ? (
                        <Spinner data-icon="inline-start" />
                      ) : (
                        <Send data-icon="inline-start" />
                      )}
                      Enviar solicitação
                    </Button>
                  </Field>
                </FieldGroup>
              </form>
            </Panel>

            <Panel title="Como funciona" contentClassName="gap-2.5">
              {RULES.map((rule) => (
                <p
                  key={rule}
                  className="flex gap-2 text-xs leading-relaxed text-muted-foreground"
                >
                  <Check className="mt-0.5 size-3.5 shrink-0 text-success" />
                  {rule}
                </p>
              ))}
            </Panel>
          </div>
        </div>
      </main>
    </>
  );
}
