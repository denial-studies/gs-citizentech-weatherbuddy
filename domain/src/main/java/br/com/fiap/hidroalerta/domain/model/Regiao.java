package br.com.fiap.hidroalerta.domain.model;

import br.com.fiap.hidroalerta.domain.valueobject.CoordenadaGeografica;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Regiao {

    private Long idRegiao;
    private String nomeRegiao;
    private String bairro;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal limiteChuvaMm;
    private NivelRisco nivelRisco;

    public CoordenadaGeografica obterCoordenadas() {
        return new CoordenadaGeografica(latitude, longitude);
    }

    public boolean ultrapassouLimiteSeguranca(BigDecimal precipitacaoMm) {
        if (precipitacaoMm == null || limiteChuvaMm == null) {
            return false;
        }
        return precipitacaoMm.compareTo(limiteChuvaMm) >= 0;
    }

    public SeveridadeAlerta determinarSeveridade(BigDecimal precipitacaoMm) {
        return SeveridadeAlerta.classificar(precipitacaoMm, limiteChuvaMm);
    }

    public String gerarMensagemAlerta(BigDecimal precipitacaoMm, SeveridadeAlerta severidade) {
        return String.format(
                "[WEATHER BUDDY | %s] %s (%s): Precipitação atual de %s mm atingiu o limiar de segurança (%s mm). "
                        + "Risco %s de alagamento. Siga as orientações da Defesa Civil e busque um ponto seguro.",
                severidade.name(),
                nomeRegiao,
                bairro,
                precipitacaoMm.toPlainString(),
                limiteChuvaMm.toPlainString(),
                nivelRisco != null ? nivelRisco.name() : "ALTO"
        );
    }
}

