package br.com.fiap.hidroalerta.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record DisparoMonitoramentoRequest(
        @NotNull(message = "O ID da região é obrigatório.")
        Long idRegiao,

        Long idResponsavel
) {
}

