package br.com.fiap.hidroalerta.application.dto.request;

import br.com.fiap.hidroalerta.domain.model.NivelRisco;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RegiaoRequest(
        @NotBlank(message = "O nome da região é obrigatório.")
        @Size(max = 100, message = "O nome da região deve ter no máximo 100 caracteres.")
        String nomeRegiao,

        @NotBlank(message = "O nome do bairro é obrigatório.")
        @Size(max = 100, message = "O nome do bairro deve ter no máximo 100 caracteres.")
        String bairro,

        @NotNull(message = "A latitude é obrigatória.")
        @DecimalMin(value = "-90.0", message = "A latitude mínima é -90.0.")
        @DecimalMax(value = "90.0", message = "A latitude máxima é 90.0.")
        BigDecimal latitude,

        @NotNull(message = "A longitude é obrigatória.")
        @DecimalMin(value = "-180.0", message = "A longitude mínima é -180.0.")
        @DecimalMax(value = "180.0", message = "A longitude máxima é 180.0.")
        BigDecimal longitude,

        @NotNull(message = "O limite de chuva em mm é obrigatório.")
        @Positive(message = "O limite de chuva deve ser maior que zero.")
        BigDecimal limiteChuvaMm,

        @NotNull(message = "O nível de risco é obrigatório.")
        NivelRisco nivelRisco
) {
}

