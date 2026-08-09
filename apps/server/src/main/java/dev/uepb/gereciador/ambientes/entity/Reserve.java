package dev.uepb.gereciador.ambientes.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import dev.uepb.gereciador.ambientes.dto.ReserveSlot;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa uma reserva de ambiente realizada por um usuário.
 *
 * <p>Uma reserva associa um usuário a um ambiente em uma data específica,
 * com um ou mais slots de horário de 1 hora cada. As reservas são armazenadas
 * na coleção {@code reserves} do MongoDB.</p>
 *
 * <p><strong>Regras de negócio:</strong></p>
 * <ul>
 *   <li>Cada slot tem duração exata de 1 hora e deve começar em hora cheia</li>
 *   <li>Horários permitidos: 08:00 às 22:00</li>
 *   <li>Não é permitida reserva em datas passadas</li>
 *   <li>Não pode haver conflito de horário para o mesmo ambiente e data</li>
 *   <li>Status inicial é sempre {@link ReserveStatus#PENDING}</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see ReserveStatus
 * @see Slot
 */
@Document(collection = "reserves")
@Getter
@Setter
@Schema(description = "Reserva de ambiente realizada por um usuário")
public class Reserve {

    /** Identificador único da reserva (gerado pelo MongoDB). */
    @Id
    @Schema(description = "Identificador único da reserva", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    /** ID do usuário que realizou a reserva. */
    @Schema(description = "ID do usuário que realizou a reserva")
    private String userId;

    /** ID do ambiente reservado. */
    @Schema(description = "ID do ambiente reservado")
    private String environmentId;

    /** Justificativa informada pelo usuário para a reserva. */
    @Schema(description = "Justificativa da reserva", example = "Aula prática de Redes de Computadores")
    private String justification;

    /** Número de participantes esperados para o evento/aula. */
    @Schema(description = "Número de participantes", example = "25")
    private Integer numberOfParticipants;

    /** Data em que a reserva foi solicitada. */
    @Schema(description = "Data da reserva", example = "2026-04-15")
    private LocalDate date;

    /** Lista de slots de horário reservados. */
    @Schema(description = "Slots de horário reservados")
    private List<Slot> slots;

    /** Status atual da reserva (PENDING, APPROVED ou REJECTED). */
    @Field(targetType = FieldType.STRING)
    @Schema(description = "Status da reserva", example = "PENDING")
    private ReserveStatus status;

    /** Data e hora de criação do registro. */
    @CreatedDate
    @Schema(description = "Data/hora de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    /** Data e hora da última modificação. */
    @LastModifiedDate
    @Schema(description = "Data/hora da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    /**
     * Representa um slot de horário dentro de uma reserva.
     *
     * <p>Cada slot corresponde a um intervalo de 1 hora (ex.: 09:00 – 10:00).
     * Pode ser construído a partir de um {@link ReserveSlot} recebido na requisição.</p>
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "Slot de horário de uma reserva")
    public static class Slot {

        /** Horário de início do slot (formato HH:mm). */
        @Schema(description = "Horário de início", example = "09:00", type = "string")
        private LocalTime startTime;

        /** Horário de fim do slot (formato HH:mm). */
        @Schema(description = "Horário de fim", example = "10:00", type = "string")
        private LocalTime endTime;

        /**
         * Construtor que converte um {@link ReserveSlot} (DTO de requisição) em um {@link Slot} de entidade.
         *
         * @param reserveSlot o DTO contendo os horários de início e fim
         */
        public Slot(ReserveSlot reserveSlot) {
            this.startTime = reserveSlot.startTime();
            this.endTime = reserveSlot.endTime();
        }
    }
}
