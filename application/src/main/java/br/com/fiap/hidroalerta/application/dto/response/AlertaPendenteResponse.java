package br.com.fiap.hidroalerta.application.dto.response;

import br.com.fiap.hidroalerta.domain.model.IntensidadeChuva;
import br.com.fiap.hidroalerta.domain.model.NivelRisco;
import br.com.fiap.hidroalerta.domain.model.SeveridadeAlerta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AlertaPendenteResponse(
        Long idRegiao,
        String nomeRegiao,
        String bairro,
        NivelRisco nivelRisco,
        BigDecimal precipitacaoMm,
        BigDecimal limiteChuvaMm,
        IntensidadeChuva intensidade,
        SeveridadeAlerta severidadeSugerida,
        String mensagemSugerida,
        long totalMoradoresAtivos,
        LocalDateTime dataMedicao
) {
}

