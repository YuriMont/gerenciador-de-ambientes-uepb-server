package dev.uepb.gereciador.ambientes.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import dev.uepb.gereciador.ambientes.enums.ReserveStatus;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "reserves")
@Getter
@Setter
public class Reserve {

    @Id
    private String id;

    private Long userId;

    private Long environmentId;

    private String justification;

    private Integer numberOfParticipants;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Field(targetType = FieldType.STRING)
    private ReserveStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
