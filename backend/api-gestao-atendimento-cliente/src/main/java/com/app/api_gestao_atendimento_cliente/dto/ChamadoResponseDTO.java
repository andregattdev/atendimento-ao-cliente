package com.app.api_gestao_atendimento_cliente.dto;

import com.app.api_gestao_atendimento_cliente.entity.Prioridade;
import com.app.api_gestao_atendimento_cliente.entity.StatusChamado;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChamadoResponseDTO(

        UUID id,

        UUID clienteId,

        String clienteNome,

        String assunto,

        String descricao,

        Prioridade prioridade,

        StatusChamado status,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataAbertura,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataInicioAtendimento,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataResolucao,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataFechamento,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") LocalDateTime dataAtualizacao

) {
}