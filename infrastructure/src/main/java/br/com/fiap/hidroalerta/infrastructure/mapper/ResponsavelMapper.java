package br.com.fiap.hidroalerta.infrastructure.mapper;

import br.com.fiap.hidroalerta.domain.model.Responsavel;
import br.com.fiap.hidroalerta.infrastructure.entity.ResponsavelEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponsavelMapper {

    public Responsavel toDomain(ResponsavelEntity entity) {
        if (entity == null) {
            return null;
        }
        return Responsavel.builder()
                .idResponsavel(entity.getIdResponsavel())
                .nomeResponsavel(entity.getNomeResponsavel())
                .emailLogin(entity.getEmailLogin())
                .senhaHash(entity.getSenhaHash())
                .cargo(entity.getCargo())
                .ativo(entity.getAtivo())
                .dataCadastro(entity.getDataCadastro())
                .build();
    }

    public ResponsavelEntity toEntity(Responsavel domain) {
        if (domain == null) {
            return null;
        }
        return ResponsavelEntity.builder()
                .idResponsavel(domain.getIdResponsavel())
                .nomeResponsavel(domain.getNomeResponsavel())
                .emailLogin(domain.getEmailLogin())
                .senhaHash(domain.getSenhaHash())
                .cargo(domain.getCargo())
                .ativo(domain.getAtivo())
                .dataCadastro(domain.getDataCadastro())
                .build();
    }
}
