package br.com.fiap.hidroalerta.application.dto.response;

import br.com.fiap.hidroalerta.domain.model.NivelRisco;

import java.math.BigDecimal;

public record RegiaoResponse(
        Long idRegiao,
        String nomeRegiao,
        String bairro,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal limiteChuvaMm,
        NivelRisco nivelRisco
) {
}

