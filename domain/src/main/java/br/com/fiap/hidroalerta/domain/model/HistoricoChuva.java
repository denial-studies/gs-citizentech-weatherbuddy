package br.com.fiap.hidroalerta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoChuva {

    private Long idChuva;
    private Long idRegiao;
    private String nomeRegiao;
    private String bairroRegiao;
    private BigDecimal precipitacaoMm;
    private IntensidadeChuva intensidade;
    private LocalDateTime dataMedicao;

    public static HistoricoChuva registrarMedicao(Regiao regiao, BigDecimal precipitacaoMm) {
        BigDecimal valorSeguro = precipitacaoMm != null ? precipitacaoMm : BigDecimal.ZERO;
        return HistoricoChuva.builder()
                .idRegiao(regiao.getIdRegiao())
                .nomeRegiao(regiao.getNomeRegiao())
                .bairroRegiao(regiao.getBairro())
                .precipitacaoMm(valorSeguro)
                .intensidade(IntensidadeChuva.classificar(valorSeguro))
                .dataMedicao(LocalDateTime.now())
                .build();
    }
}

