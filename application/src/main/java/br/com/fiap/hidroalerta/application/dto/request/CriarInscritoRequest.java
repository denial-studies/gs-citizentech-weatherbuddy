package br.com.fiap.hidroalerta.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarInscritoRequest(
        @NotBlank(message = "O nome do morador é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nomeInscrito,

        @NotBlank(message = "O número de telefone é obrigatório.")
        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone,

        @NotNull(message = "A seleção da região/bairro é obrigatória.")
        Long idRegiao
) {
}

