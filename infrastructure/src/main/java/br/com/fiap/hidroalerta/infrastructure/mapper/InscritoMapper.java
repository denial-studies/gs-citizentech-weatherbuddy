package br.com.fiap.hidroalerta.infrastructure.mapper;

import br.com.fiap.hidroalerta.domain.model.Inscrito;
import br.com.fiap.hidroalerta.infrastructure.entity.InscritoEntity;
import org.springframework.stereotype.Component;

@Component
public class InscritoMapper {

    public Inscrito toDomain(InscritoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Inscrito.builder()
                .idInscrito(entity.getIdInscrito())
                .idRegiao(entity.getRegiao() != null ? entity.getRegiao().getIdRegiao() : null)
                .nomeRegiao(entity.getRegiao() != null ? entity.getRegiao().getNomeRegiao() : null)
                .bairroRegiao(entity.getRegiao() != null ? entity.getRegiao().getBairro() : null)
                .nomeInscrito(entity.getNomeInscrito())
                .telefone(entity.getTelefone())
                .ativo(entity.getAtivo())
                .dataInscricao(entity.getDataInscricao())
                .build();
    }

    public InscritoEntity toEntity(Inscrito domain) {
        if (domain == null) {
            return null;
        }
        return InscritoEntity.builder()
                .idInscrito(domain.getIdInscrito())
                .nomeInscrito(domain.getNomeInscrito())
                .telefone(domain.getTelefone())
                .ativo(domain.getAtivo())
                .dataInscricao(domain.getDataInscricao())
                .build();
    }
}
