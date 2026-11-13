package br.com.fiap.hidroalerta.infrastructure.mapper;

import br.com.fiap.hidroalerta.domain.model.HistoricoMensagem;
import br.com.fiap.hidroalerta.infrastructure.entity.HistoricoMensagemEntity;
import org.springframework.stereotype.Component;

@Component
public class HistoricoMensagemMapper {

    public HistoricoMensagem toDomain(HistoricoMensagemEntity entity) {
        if (entity == null) {
            return null;
        }
        return HistoricoMensagem.builder()
                .idMensagem(entity.getIdMensagem())
                .idRegiao(entity.getRegiao() != null ? entity.getRegiao().getIdRegiao() : null)
                .nomeRegiao(entity.getRegiao() != null ? entity.getRegiao().getNomeRegiao() : null)
                .bairroRegiao(entity.getRegiao() != null ? entity.getRegiao().getBairro() : null)
                .idResponsavel(entity.getResponsavel() != null ? entity.getResponsavel().getIdResponsavel() : null)
                .nomeResponsavel(entity.getResponsavel() != null ? entity.getResponsavel().getNomeResponsavel() : null)
                .severidade(entity.getSeveridade())
                .conteudo(entity.getConteudo())
                .quantidadeEnviosSucesso(entity.getQuantidadeEnviosSucesso())
                .dataEnvio(entity.getDataEnvio())
                .build();
    }

    public HistoricoMensagemEntity toEntity(HistoricoMensagem domain) {
        if (domain == null) {
            return null;
        }
        return HistoricoMensagemEntity.builder()
                .idMensagem(domain.getIdMensagem())
                .severidade(domain.getSeveridade())
                .conteudo(domain.getConteudo())
                .quantidadeEnviosSucesso(domain.getQuantidadeEnviosSucesso())
                .dataEnvio(domain.getDataEnvio())
                .build();
    }
}
