package br.com.fiap.hidroalerta.application.dto.response;

import br.com.fiap.hidroalerta.domain.model.IntensidadeChuva;
import br.com.fiap.hidroalerta.domain.model.SeveridadeAlerta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResultadoMonitoramentoResponse(
        Long idRegiao,
        String nomeRegiao,
        String bairro,
        BigDecimal precipitacaoAtualMm,
        BigDecimal limiteRegiaoMm,
        IntensidadeChuva intensidadeChuva,
        boolean alertaDisparado,
        SeveridadeAlerta severidadeAlerta,
        int moradoresNotificados,
        String mensagemResumo,
        LocalDateTime dataHoraVerificacao
) {
}

