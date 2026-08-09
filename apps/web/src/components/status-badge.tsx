import { cva, type VariantProps } from "class-variance-authority";
import type { ReserveStatus } from "@/generated/models/reserveStatus";
import type { RoleName } from "@/generated/models/roleName";
import { cn } from "@/lib/utils";

/**
 * Etiqueta de estado com fundo suave, texto forte e ponto colorido — o padrão do
 * design system para status de reserva e perfil de acesso.
 */
const toneBadgeVariants = cva(
  "inline-flex w-fit shrink-0 items-center gap-1.5 rounded-4xl px-2.5 py-1 text-[11px] font-semibold tracking-wide whitespace-nowrap uppercase",
  {
    variants: {
      tone: {
        neutral: "bg-muted text-muted-foreground",
        accent: "bg-accent-soft text-accent-strong",
        success: "bg-success-soft text-success",
        warning: "bg-warning-soft text-warning",
        danger: "bg-danger-soft text-danger",
      },
    },
    defaultVariants: { tone: "neutral" },
  },
);

const dotVariants = cva("size-1.5 rounded-full", {
  variants: {
    tone: {
      neutral: "bg-subtle",
      accent: "bg-primary",
      success: "bg-success",
      warning: "bg-warning",
      danger: "bg-danger",
    },
  },
  defaultVariants: { tone: "neutral" },
});

type Tone = NonNullable<VariantProps<typeof toneBadgeVariants>["tone"]>;

/** Etiqueta genérica com ponto colorido. */
export function ToneBadge({
  tone,
  children,
  className,
}: {
  tone: Tone;
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <span className={cn(toneBadgeVariants({ tone }), className)}>
      <span className={dotVariants({ tone })} aria-hidden="true" />
      {children}
    </span>
  );
}

const STATUS_LABEL: Record<ReserveStatus, string> = {
  PENDING: "Pendente",
  APPROVED: "Aprovada",
  REJECTED: "Rejeitada",
};

const STATUS_TONE: Record<ReserveStatus, Tone> = {
  PENDING: "warning",
  APPROVED: "success",
  REJECTED: "danger",
};

/** Etiqueta do status de uma reserva. */
export function StatusBadge({
  status,
  className,
}: {
  status: ReserveStatus | undefined;
  className?: string;
}) {
  const resolved = status ?? "PENDING";
  return (
    <ToneBadge tone={STATUS_TONE[resolved]} className={className}>
      {STATUS_LABEL[resolved]}
    </ToneBadge>
  );
}

const ROLE_TONE: Record<RoleName, Tone> = {
  USER: "neutral",
  ADMIN: "accent",
  OWNER: "success",
};

/** Etiqueta do perfil de acesso de uma pessoa. */
export function RoleBadge({
  role,
  className,
}: {
  role: RoleName | undefined;
  className?: string;
}) {
  const resolved = role ?? "USER";
  return (
    <ToneBadge tone={ROLE_TONE[resolved]} className={className}>
      {resolved}
    </ToneBadge>
  );
}

export { STATUS_LABEL };
