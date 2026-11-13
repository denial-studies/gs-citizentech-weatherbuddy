package br.com.fiap.hidroalerta.application.dto.response;

import br.com.fiap.hidroalerta.domain.model.IntensidadeChuva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoricoChuvaResponse(
        Long idChuva,
        Long idRegiao,
        String nomeRegiao,
        String bairroRegiao,
        BigDecimal precipitacaoMm,
        IntensidadeChuva intensidade,
        LocalDateTime dataMedicao
) {
}

