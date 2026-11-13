package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.domain.model.HistoricoMensagem;
import br.com.fiap.hidroalerta.infrastructure.entity.HistoricoMensagemEntity;
import br.com.fiap.hidroalerta.infrastructure.entity.RegiaoEntity;
import br.com.fiap.hidroalerta.infrastructure.entity.ResponsavelEntity;
import br.com.fiap.hidroalerta.infrastructure.mapper.HistoricoMensagemMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HistoricoMensagemRepositoryImpl implements br.com.fiap.hidroalerta.domain.repository.HistoricoMensagemRepository {

    private final HistoricoMensagemRepository jpaRepository;
    private final RegiaoRepository regiaoJpaRepository;
    private final ResponsavelRepository responsavelJpaRepository;
    private final HistoricoMensagemMapper mapper;

    public HistoricoMensagemRepositoryImpl(
            @Lazy HistoricoMensagemRepository jpaRepository,
            @Lazy RegiaoRepository regiaoJpaRepository,
            @Lazy ResponsavelRepository responsavelJpaRepository,
            HistoricoMensagemMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.regiaoJpaRepository = regiaoJpaRepository;
        this.responsavelJpaRepository = responsavelJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public HistoricoMensagem salvar(HistoricoMensagem historicoMensagem) {
        HistoricoMensagemEntity entity = mapper.toEntity(historicoMensagem);
        if (entity.getIdMensagem() == null) {
            entity.setIdMensagem(jpaRepository.proximoId());
        }
        RegiaoEntity regiaoEntity = regiaoJpaRepository.findById(historicoMensagem.getIdRegiao())
                .orElseThrow(() -> new IllegalArgumentException("Região não encontrada: " + historicoMensagem.getIdRegiao()));
        entity.setRegiao(regiaoEntity);

        ResponsavelEntity responsavelEntity = responsavelJpaRepository.findById(historicoMensagem.getIdResponsavel())
                .orElseThrow(() -> new IllegalArgumentException("Responsável não encontrado: " + historicoMensagem.getIdResponsavel()));
        entity.setResponsavel(responsavelEntity);

        HistoricoMensagemEntity saved = jpaRepository.save(entity);
        saved.setRegiao(regiaoEntity);
        saved.setResponsavel(responsavelEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<HistoricoMensagem> buscarPorId(Long idMensagem) {
        return jpaRepository.findById(idMensagem).map(mapper::toDomain);
    }

    @Override
    public List<HistoricoMensagem> listarTodosOrdenadosPorData() {
        return jpaRepository.findAllByOrderByDataEnvioDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<HistoricoMensagem> listarPorRegiao(Long idRegiao) {
        return jpaRepository.findByRegiaoIdRegiaoOrderByDataEnvioDesc(idRegiao).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long idMensagem) {
        jpaRepository.deleteById(idMensagem);
    }

    @Override
    public long contarTotal() {
        return jpaRepository.count();
    }
}
