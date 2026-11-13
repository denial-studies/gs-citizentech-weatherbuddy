package br.com.fiap.hidroalerta.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O e-mail de login é obrigatório.")
        @Email(message = "Informe um endereço de e-mail válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {
}

