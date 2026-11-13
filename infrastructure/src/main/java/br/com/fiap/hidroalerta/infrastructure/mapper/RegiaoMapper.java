package br.com.fiap.hidroalerta.infrastructure.mapper;

import br.com.fiap.hidroalerta.domain.model.Regiao;
import br.com.fiap.hidroalerta.infrastructure.entity.RegiaoEntity;
import org.springframework.stereotype.Component;

@Component
public class RegiaoMapper {

    public Regiao toDomain(RegiaoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Regiao.builder()
                .idRegiao(entity.getIdRegiao())
                .nomeRegiao(entity.getNomeRegiao())
                .bairro(entity.getBairro())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .limiteChuvaMm(entity.getLimiteChuvaMm())
                .nivelRisco(entity.getNivelRisco())
                .build();
    }

    public RegiaoEntity toEntity(Regiao domain) {
        if (domain == null) {
            return null;
        }
        return RegiaoEntity.builder()
                .idRegiao(domain.getIdRegiao())
                .nomeRegiao(domain.getNomeRegiao())
                .bairro(domain.getBairro())
                .latitude(domain.getLatitude())
                .longitude(domain.getLongitude())
                .limiteChuvaMm(domain.getLimiteChuvaMm())
                .nivelRisco(domain.getNivelRisco())
                .build();
    }
}
