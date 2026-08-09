/** Formatação de datas, horários e nomes em pt-BR. */

const LOCALE = "pt-BR";

/**
 * Converte `yyyy-MM-dd` em uma data local, sem o deslocamento de fuso que
 * `new Date("2026-04-16")` introduz ao interpretar a string como UTC.
 */
export function parseIsoDate(date: string): Date {
  const [year, month, day] = date.split("-").map(Number);
  return new Date(year, month - 1, day);
}

/** Devolve a data de hoje no formato `yyyy-MM-dd`. */
export function todayIso(): string {
  return toIsoDate(new Date());
}

/** Converte uma data em `yyyy-MM-dd`. */
export function toIsoDate(date: Date): string {
  const month = `${date.getMonth() + 1}`.padStart(2, "0");
  const day = `${date.getDate()}`.padStart(2, "0");
  return `${date.getFullYear()}-${month}-${day}`;
}

/** Soma dias a uma data, devolvendo uma nova instância. */
export function addDays(date: Date, days: number): Date {
  const next = new Date(date);
  next.setDate(next.getDate() + days);
  return next;
}

/** "quarta, 16 de abril de 2026" */
export function formatLongDate(date: string | Date): string {
  const value = typeof date === "string" ? parseIsoDate(date) : date;
  return value.toLocaleDateString(LOCALE, {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  });
}

/** "qua, 16 abr" */
export function formatShortDate(
  date: string | Date | null | undefined,
): string {
  if (!date) return "—";
  const value = typeof date === "string" ? parseIsoDate(date) : date;
  return value
    .toLocaleDateString(LOCALE, {
      weekday: "short",
      day: "2-digit",
      month: "short",
    })
    .replace(/\./g, "");
}

/** "12 mar 2024" */
export function formatDayMonthYear(
  date: string | Date | null | undefined,
): string {
  if (!date) return "—";
  const value = typeof date === "string" ? new Date(date) : date;
  return value
    .toLocaleDateString(LOCALE, {
      day: "2-digit",
      month: "short",
      year: "numeric",
    })
    .replace(/\./g, "");
}

/** Recorta `HH:mm:ss` para `HH:mm`. */
export function formatTime(time: string | undefined): string {
  return (time ?? "00:00:00").slice(0, 5);
}

/** Iniciais do nome, para o avatar: "Maria Rodrigues" → "MR". */
export function initials(name: string | null | undefined): string {
  if (!name) return "?";
  const parts = name.trim().split(/\s+/);
  const first = parts[0]?.[0] ?? "";
  const last = parts.length > 1 ? (parts[parts.length - 1][0] ?? "") : "";
  return (first + last).toUpperCase();
}

/** Primeiro nome, usado na saudação da tela de início. */
export function firstName(name: string | null | undefined): string {
  return name?.trim().split(/\s+/)[0] ?? "";
}

/** "Bom dia" / "Boa tarde" / "Boa noite" conforme a hora atual. */
export function greeting(now = new Date()): string {
  const hour = now.getHours();
  if (hour < 12) return "Bom dia";
  return hour < 18 ? "Boa tarde" : "Boa noite";
}

/**
 * Intervalo coberto pelos slots de uma reserva: "14:00 – 16:00".
 * Os slots são contíguos na prática, então basta o menor início e o maior fim.
 */
export function formatSlotRange(
  slots?: { startTime?: string; endTime?: string }[],
): string {
  if (!slots || slots.length === 0) return "—";
  const start = slots.reduce(
    (min, s) => (s.startTime ?? min) < min ? s.startTime ?? min : min,
    slots[0].startTime ?? "08:00:00",
  );
  const end = slots.reduce(
    (max, s) => (s.endTime ?? max) > max ? s.endTime ?? max : max,
    slots[0].endTime ?? "09:00:00",
  );
  return `${formatTime(start)} – ${formatTime(end)}`;
}

/** "há 2 h", "ontem", "há 5 min" — usado na fila de aprovação. */
export function formatRelative(instant: string | null): string {
  if (!instant) return "—";

  const diffMs = Date.now() - new Date(instant).getTime();
  const minutes = Math.round(diffMs / 60000);

  if (minutes < 1) return "agora";
  if (minutes < 60) return `há ${minutes} min`;

  const hours = Math.round(minutes / 60);
  if (hours < 24) return `há ${hours} h`;
  if (hours < 48) return "ontem";

  return `há ${Math.round(hours / 24)} dias`;
}
