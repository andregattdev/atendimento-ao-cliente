package com.app.api_gestao_atendimento_cliente.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponseDTO(

        UUID id,

        String nome,

        String email,

        String telefone,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataCadastro,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataAtualizacao,

        boolean ativo

) {
}
