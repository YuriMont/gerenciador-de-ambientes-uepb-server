package dev.uepb.gereciador.ambientes.enums;

/**
 * Define os possíveis estados de uma reserva de ambiente.
 *
 * <p>O ciclo de vida de uma reserva começa em {@link #PENDING} e pode ser
 * atualizado por um administrador para {@link #APPROVED} ou {@link #REJECTED}.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
public enum ReserveStatus {

    /** Reserva aprovada por um administrador. O ambiente está confirmado para o solicitante. */
    APPROVED,

    /** Reserva rejeitada por um administrador. O horário é liberado novamente. */
    REJECTED,

    /** Reserva aguardando análise de um administrador (estado inicial). */
    PENDING
}
