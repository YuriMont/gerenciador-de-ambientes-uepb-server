package dev.uepb.gereciador.ambientes.entity;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa um usuário cadastrado no sistema.
 *
 * <p>Implementa {@link UserDetails} para integração com o Spring Security,
 * permitindo que o framework gerencie autenticação e autorização com base
 * nos dados desta entidade.</p>
 *
 * <p>O e-mail é usado como identificador de login ({@code username}) e possui
 * índice único no banco de dados. Os documentos são armazenados na coleção
 * {@code users} do MongoDB.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 * @see Role
 * @see UserDetails
 */
@Getter
@Setter
@Document(collection = "users")
@Schema(description = "Usuário do sistema")
public class User implements UserDetails {

    /** Identificador único do usuário (gerado pelo MongoDB). */
    @Id
    @Schema(description = "Identificador único do usuário", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    /** Nome completo do usuário. */
    @Schema(description = "Nome completo", example = "João da Silva")
    private String name;

    /** E-mail do usuário, utilizado como login. Deve ser único no sistema. */
    @Indexed(unique = true)
    @Schema(description = "E-mail do usuário (usado como login)", example = "joao.silva@uepb.edu.br")
    private String email;

    /** Senha do usuário armazenada com hash BCrypt. */
    @Schema(description = "Senha (armazenada com hash BCrypt)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    /** Papel/perfil de acesso do usuário. */
    @Schema(description = "Papel de acesso do usuário")
    private Role role;

    /** Data e hora de criação do cadastro. */
    @CreatedDate
    @Schema(description = "Data/hora de criação do cadastro", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    /** Data e hora da última atualização do cadastro. */
    @LastModifiedDate
    @Schema(description = "Data/hora da última atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    /**
     * Retorna as autoridades (roles) concedidas ao usuário.
     *
     * <p>Converte o {@link Role} do usuário em uma {@link SimpleGrantedAuthority}
     * com o prefixo {@code ROLE_} exigido pelo Spring Security
     * (ex.: {@code ROLE_USER}, {@code ROLE_ADMIN}, {@code ROLE_OWNER}).</p>
     *
     * @return coleção com a autoridade do usuário
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName());
        return List.of(authority);
    }

    /**
     * Retorna o e-mail como nome de usuário para o Spring Security.
     *
     * @return o e-mail do usuário
     */
    @Override
    public String getUsername() {
        return email;
    }
}
