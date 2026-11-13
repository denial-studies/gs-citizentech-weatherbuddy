package br.com.fiap.hidroalerta.application.port;

import java.math.BigDecimal;

public interface ClimaTelemetryPort {

    BigDecimal obterPrecipitacaoAtualMm(BigDecimal latitude, BigDecimal longitude);
}

