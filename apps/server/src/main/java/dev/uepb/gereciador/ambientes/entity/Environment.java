package dev.uepb.gereciador.ambientes.entity;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um ambiente físico da UEPB que pode ser reservado.
 *
 * <p>Exemplos de ambientes: salas de aula, laboratórios, auditórios, etc.
 * Os documentos são armazenados na coleção {@code environments} do MongoDB.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Document(collection = "environments")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Ambiente físico da UEPB disponível para reserva")
public class Environment {

    /** Identificador único do ambiente (gerado pelo MongoDB). */
    @Id
    @Schema(description = "Identificador único do ambiente", example = "664f1a2b3c4d5e6f7a8b9c0d", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    /** Nome do ambiente (ex.: "Sala 101 – Bloco A"). */
    @Schema(description = "Nome do ambiente", example = "Laboratório de Informática 02")
    private String name;

    /** Descrição detalhada do ambiente (recursos disponíveis, restrições de uso, etc.). */
    @Schema(description = "Descrição do ambiente", example = "Laboratório com 30 computadores e projetor")
    private String description;

    /** Quantidade de lugares do ambiente. Limita o número de participantes de uma reserva. */
    @Schema(description = "Quantidade de lugares", example = "220")
    private Integer capacity;

    /** Bloco onde o ambiente fica (ex.: "Bloco C · Térreo"). */
    @Schema(description = "Bloco onde o ambiente fica", example = "Bloco C")
    private String block;

    /** URL de uma imagem representativa do ambiente. */
    @Schema(description = "URL da imagem do ambiente", example = "https://storage.example.com/lab02.jpg")
    private String imageUrl;

    /** Data e hora de criação do registro (preenchida automaticamente). */
    @CreatedDate
    @Schema(description = "Data/hora de criação do registro", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    /** Data e hora da última modificação do registro (atualizada automaticamente). */
    @LastModifiedDate
    @Schema(description = "Data/hora da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;
}
