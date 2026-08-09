import type { LucideIcon } from "lucide-react";
import { cn } from "@/lib/utils";

/**
 * Indicador do topo das telas: ícone em caixa suave, valor grande e rótulo.
 */
export function StatCard({
  icon: Icon,
  value,
  label,
  className,
}: {
  icon: LucideIcon;
  value: React.ReactNode;
  label: string;
  className?: string;
}) {
  return (
    <div
      className={cn(
        "flex min-w-0 items-center gap-3 rounded-lg border border-border bg-card px-4 py-3.5",
        className,
      )}
    >
      <span className="flex size-9 shrink-0 items-center justify-center rounded-md bg-accent-soft text-accent-strong">
        <Icon className="size-4.5" />
      </span>
      <span className="flex min-w-0 flex-col">
        <span className="text-xl leading-tight font-semibold text-foreground">
          {value}
        </span>
        <span className="truncate text-xs text-muted-foreground">{label}</span>
      </span>
    </div>
  );
}
