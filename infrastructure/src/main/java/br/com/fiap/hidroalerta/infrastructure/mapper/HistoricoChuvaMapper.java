package br.com.fiap.hidroalerta.infrastructure.mapper;

import br.com.fiap.hidroalerta.domain.model.HistoricoChuva;
import br.com.fiap.hidroalerta.infrastructure.entity.HistoricoChuvaEntity;
import org.springframework.stereotype.Component;

@Component
public class HistoricoChuvaMapper {

    public HistoricoChuva toDomain(HistoricoChuvaEntity entity) {
        if (entity == null) {
            return null;
        }
        return HistoricoChuva.builder()
                .idChuva(entity.getIdChuva())
                .idRegiao(entity.getRegiao() != null ? entity.getRegiao().getIdRegiao() : null)
                .nomeRegiao(entity.getRegiao() != null ? entity.getRegiao().getNomeRegiao() : null)
                .bairroRegiao(entity.getRegiao() != null ? entity.getRegiao().getBairro() : null)
                .precipitacaoMm(entity.getPrecipitacaoMm())
                .intensidade(entity.getIntensidade())
                .dataMedicao(entity.getDataMedicao())
                .build();
    }

    public HistoricoChuvaEntity toEntity(HistoricoChuva domain) {
        if (domain == null) {
            return null;
        }
        return HistoricoChuvaEntity.builder()
                .idChuva(domain.getIdChuva())
                .precipitacaoMm(domain.getPrecipitacaoMm())
                .intensidade(domain.getIntensidade())
                .dataMedicao(domain.getDataMedicao())
                .build();
    }
}
