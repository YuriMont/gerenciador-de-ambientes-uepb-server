import type { SlotAvailabilityResponse } from "@/generated/models/slotAvailabilityResponse";
import { formatTime } from "@/lib/format";
import { cn } from "@/lib/utils";

/**
 * Mini-agenda de um dia: uma barra por hora de funcionamento, livre em cinza claro
 * e ocupada em azul. Resume a agenda no card de um ambiente.
 */
export function MiniAgenda({
  slots,
  className,
}: {
  slots: SlotAvailabilityResponse[] | undefined;
  className?: string;
}) {
  return (
    <div
      className={cn("flex h-8 items-end gap-1", className)}
      aria-hidden="true"
    >
      {(slots ?? []).map((slot) => (
        <span
          key={slot.startTime}
          title={`${formatTime(slot.startTime ?? "")} – ${formatTime(slot.endTime ?? "")}`}
          className={cn(
            "flex-1 rounded-xs transition-all",
            slot.status === "AVAILABLE" && "h-2.5 bg-muted",
            slot.status === "RESERVED" && "h-2.5 bg-primary",
            slot.status === "CLOSED" && "h-2.5 bg-border",
          )}
        />
      ))}
    </div>
  );
}
