package dev.uepb.gereciador.ambientes.enums;

/**
 * Define os papéis/perfis de acesso dos usuários no sistema.
 *
 * <p>Os papéis controlam o nível de permissão de cada usuário e são verificados
 * pelo Spring Security via anotações {@code @PreAuthorize}.</p>
 *
 * <ul>
 *   <li>{@link #USER} — pode realizar e visualizar suas próprias reservas</li>
 *   <li>{@link #ADMIN} — pode gerenciar ambientes, visualizar todos os usuários e reservas</li>
 *   <li>{@link #OWNER} — acesso total, incluindo criação de administradores</li>
 * </ul>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
public enum UserRole {

    /** Usuário comum com permissões básicas de reserva. */
    USER,

    /** Administrador com permissões de gestão de ambientes e usuários. */
    ADMIN,

    /** Proprietário do sistema com acesso irrestrito. */
    OWNER
}
