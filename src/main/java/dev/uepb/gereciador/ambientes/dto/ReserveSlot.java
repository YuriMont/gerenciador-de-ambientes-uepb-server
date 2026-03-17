package dev.uepb.gereciador.ambientes.dto;

import java.time.LocalTime;
import jakarta.validation.constraints.NotEmpty;

public record ReserveSlot(@NotEmpty LocalTime startTime, @NotEmpty LocalTime endTime) {
}
