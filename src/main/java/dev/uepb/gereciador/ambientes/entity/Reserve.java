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
import lombok.Getter;
import lombok.Setter;

@Document(collection = "reserves")
@Getter
@Setter
public class Reserve {

    @Id
    private String id;

    private String userId;

    private String environmentId;

    private String justification;

    private Integer numberOfParticipants;

    private LocalDate date;

    private List<Slot> slots;

    @Field(targetType = FieldType.STRING)
    private ReserveStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Getter
    @Setter
    public static class Slot {
        private LocalTime startTime;
        private LocalTime endTime;

        public Slot(ReserveSlot reserveSlot) {
            this.startTime = reserveSlot.startTime();
            this.endTime = reserveSlot.endTime();
        }
    }
}
