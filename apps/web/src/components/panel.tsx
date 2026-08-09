import { cn } from "@/lib/utils";

/**
 * Bloco branco com cabeçalho e conteúdo — a unidade de agrupamento das telas internas.
 * Um cabeçalho opcional evita a moldura vazia quando o conteúdo já se explica.
 */
export function Panel({
  title,
  description,
  action,
  children,
  className,
  contentClassName,
}: {
  title?: string;
  description?: string;
  /** Ação secundária alinhada à direita do título. */
  action?: React.ReactNode;
  children: React.ReactNode;
  className?: string;
  contentClassName?: string;
}) {
  return (
    <section
      className={cn(
        "flex min-w-0 flex-col gap-4 rounded-xl border border-border bg-card p-5",
        className,
      )}
    >
      {(title || action) && (
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div className="flex min-w-0 flex-col">
            {title && (
              <h2 className="text-sm font-semibold text-foreground">{title}</h2>
            )}
            {description && (
              <p className="text-xs text-muted-foreground">{description}</p>
            )}
          </div>
          {action}
        </div>
      )}
      <div className={cn("flex min-w-0 flex-col", contentClassName)}>
        {children}
      </div>
    </section>
  );
}
