package dev.uepb.gereciador.ambientes.entity;

import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import dev.uepb.gereciador.ambientes.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa um papel (perfil de acesso) de usuário no sistema.
 *
 * <p>Os papéis são pré-carregados na inicialização da aplicação pelo {@code RoleSeeder}
 * e armazenados na coleção {@code roles} do MongoDB. Os valores possíveis são definidos
 * pelo enum {@link UserRole}:</p>
 * <ul>
 *   <li>{@code USER} — usuário comum, pode realizar reservas</li>
 *   <li>{@code ADMIN} — administrador, pode gerenciar ambientes e usuários</li>
 *   <li>{@code OWNER} — proprietário do sistema, possui acesso total</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see UserRole
 */
@Document(collection = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Papel/perfil de acesso do usuário no sistema")
public class Role {

    /** Identificador único do papel (gerado pelo MongoDB). */
    @Id
    @Schema(description = "Identificador único do papel", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    /** Nome do papel, único no banco de dados. */
    @Indexed(unique = true)
    @Schema(description = "Nome do papel", example = "USER")
    private UserRole name;

    /** Data e hora de criação do registro. */
    @CreatedDate
    @Schema(description = "Data/hora de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createAt;

    /** Data e hora da última modificação. */
    @LastModifiedDate
    @Schema(description = "Data/hora da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updateAt;
}
