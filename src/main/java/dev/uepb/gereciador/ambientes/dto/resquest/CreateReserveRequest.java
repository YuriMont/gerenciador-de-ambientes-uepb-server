package dev.uepb.gereciador.ambientes.dto.resquest;

import java.time.LocalDate;
import java.util.List;
import dev.uepb.gereciador.ambientes.dto.ReserveSlot;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateReserveRequest(@NotNull LocalDate date, @NotBlank String environmentId,
        @NotNull @Min(1) Integer numberOfParticipants, @NotBlank String justification,
        @NotNull @NotEmpty List<ReserveSlot> slots) {
}
