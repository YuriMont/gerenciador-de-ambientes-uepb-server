package dev.uepb.gereciador.ambientes.dto.resquest;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GetReservesPerDayRequest(@NotNull LocalDate date, @NotBlank String environmentId) {

}
