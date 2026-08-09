package dev.uepb.gereciador.ambientes.enums;

/**
 * Define a situação de um slot de 1 hora na agenda de um ambiente em uma data.
 *
 * <p>Um slot só é reservável quando está {@link #AVAILABLE}. Os demais estados
 * explicam ao usuário por que o horário não pode ser escolhido.</p>
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
public enum SlotStatus {

    /** Horário livre — aceita novas solicitações. */
    AVAILABLE,

    /** Horário já confirmado por uma reserva aprovada. */
    RESERVED,

    /** Horário já passou no dia de hoje e não aceita mais solicitações. */
    CLOSED
}
