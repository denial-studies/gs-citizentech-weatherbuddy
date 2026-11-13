package br.com.fiap.hidroalerta.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarResponsavelRequest(
        @NotBlank(message = "O nome do responsável é obrigatório.")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
        String nomeResponsavel,

        @NotBlank(message = "O e-mail de login é obrigatório.")
        @Email(message = "E-mail inválido.")
        @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres.")
        String emailLogin,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres.")
        String senha,

        @NotBlank(message = "O cargo é obrigatório.")
        @Size(max = 60, message = "O cargo deve ter no máximo 60 caracteres.")
        String cargo
) {
}

