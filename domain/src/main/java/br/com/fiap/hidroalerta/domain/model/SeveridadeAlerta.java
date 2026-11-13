package br.com.fiap.hidroalerta.domain.model;

import java.math.BigDecimal;

public enum SeveridadeAlerta {
    ATENCAO,
    ALERTA,
    EMERGENCIA;

    private static final BigDecimal LIMIAR_EMERGENCIA = new BigDecimal("50.00");

    public static SeveridadeAlerta classificar(BigDecimal precipitacaoMm, BigDecimal limiteRegiaoMm) {
        if (precipitacaoMm == null || limiteRegiaoMm == null) {
            return ATENCAO;
        }
        if (precipitacaoMm.compareTo(LIMIAR_EMERGENCIA) >= 0) {
            return EMERGENCIA;
        }
        if (precipitacaoMm.compareTo(limiteRegiaoMm) >= 0) {
            return ALERTA;
        }
        return ATENCAO;
    }
}

