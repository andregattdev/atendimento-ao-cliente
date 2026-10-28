package com.app.api_gestao_atendimento_cliente.dto;

public record DashboardChamadosDTO(
        long total,
        long aguardando,
        long emAtendimento,
        long aguardandoCliente,
        long resolvidos,
        long fechados,
        long cancelados
) {
}