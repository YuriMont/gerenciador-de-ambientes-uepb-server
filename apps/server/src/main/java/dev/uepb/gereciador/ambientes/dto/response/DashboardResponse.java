package dev.uepb.gereciador.ambientes.dto.response;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO com os indicadores e listas exibidos na tela de início.
 *
 * <p>Reúne em uma única requisição tudo que o painel mostra: os quatro indicadores do topo,
 * a agenda confirmada do dia, a fila de pedidos aguardando resposta e o ranking de ambientes
 * mais reservados no mês.</p>
 *
 * @param pendingCount        pedidos aguardando resposta
 * @param approvedToday       reservas confirmadas para hoje
 * @param environmentCount    ambientes cadastrados
 * @param weeklyOccupancyRate ocupação média da semana, de 0 a 100
 * @param todaySchedule       agenda confirmada de hoje, em ordem cronológica
 * @param pendingQueue        pedidos pendentes, dos mais antigos para os mais recentes
 * @param topEnvironments     ambientes mais reservados no mês corrente
 *
 * @author Gerenciador de Ambientes UEPB
 * @since 1.0
 */
@Schema(description = "Indicadores e listas da tela de início")
public record DashboardResponse(
    @Schema(description = "Pedidos aguardando resposta", example = "4")
    long pendingCount,

    @Schema(description = "Reservas confirmadas para hoje", example = "11")
    long approvedToday,

    @Schema(description = "Ambientes cadastrados", example = "24")
    long environmentCount,

    @Schema(description = "Ocupação média da semana, de 0 a 100", example = "78")
    int weeklyOccupancyRate,

    @Schema(description = "Agenda confirmada de hoje")
    List<ReserveResponse> todaySchedule,

    @Schema(description = "Pedidos aguardando resposta, dos mais antigos para os mais recentes")
    List<ReserveResponse> pendingQueue,

    @Schema(description = "Ambientes mais reservados no mês")
    List<EnvironmentUsageResponse> topEnvironments
) {}
