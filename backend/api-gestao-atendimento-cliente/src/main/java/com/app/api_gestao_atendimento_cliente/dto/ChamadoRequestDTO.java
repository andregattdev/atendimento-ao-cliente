package com.app.api_gestao_atendimento_cliente.dto;

import com.app.api_gestao_atendimento_cliente.entity.Prioridade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChamadoRequestDTO(

        @NotNull(message = "O cliente é obrigatório.")
        UUID clienteId,

        @NotBlank(message = "O assunto é obrigatório.")
        @Size(max = 150, message = "O assunto deve ter no máximo 150 caracteres.")
        String assunto,

        @NotBlank(message = "A descrição é obrigatória.")
        String descricao,

        @NotNull(message = "A prioridade é obrigatória.")
        Prioridade prioridade

) {
}