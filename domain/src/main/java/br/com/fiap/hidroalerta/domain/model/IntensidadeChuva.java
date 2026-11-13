package br.com.fiap.hidroalerta.domain.model;

import java.math.BigDecimal;

public enum IntensidadeChuva {
    FRACA,
    MODERADA,
    FORTE,
    TORRENCIAL;

    private static final BigDecimal LIMITE_FRACA = new BigDecimal("5.00");
    private static final BigDecimal LIMITE_MODERADA = new BigDecimal("25.00");
    private static final BigDecimal LIMITE_FORTE = new BigDecimal("50.00");

    public static IntensidadeChuva classificar(BigDecimal precipitacaoMm) {
        if (precipitacaoMm == null || precipitacaoMm.compareTo(LIMITE_FRACA) <= 0) {
            return FRACA;
        }
        if (precipitacaoMm.compareTo(LIMITE_MODERADA) <= 0) {
            return MODERADA;
        }
        if (precipitacaoMm.compareTo(LIMITE_FORTE) <= 0) {
            return FORTE;
        }
        return TORRENCIAL;
    }
}

