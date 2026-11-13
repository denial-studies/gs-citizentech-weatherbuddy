package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.domain.model.Regiao;
import br.com.fiap.hidroalerta.infrastructure.entity.RegiaoEntity;
import br.com.fiap.hidroalerta.infrastructure.mapper.RegiaoMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RegiaoRepositoryImpl implements br.com.fiap.hidroalerta.domain.repository.RegiaoRepository {

    private final RegiaoRepository jpaRepository;
    private final RegiaoMapper mapper;

    public RegiaoRepositoryImpl(@Lazy RegiaoRepository jpaRepository, RegiaoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Regiao salvar(Regiao regiao) {
        RegiaoEntity entity = mapper.toEntity(regiao);
        if (entity.getIdRegiao() == null) {
            entity.setIdRegiao(jpaRepository.proximoId());
        }
        RegiaoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Regiao> buscarPorId(Long idRegiao) {
        return jpaRepository.findById(idRegiao).map(mapper::toDomain);
    }

    @Override
    public List<Regiao> listarTodas() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long idRegiao) {
        jpaRepository.deleteById(idRegiao);
    }

    @Override
    public boolean existePorId(Long idRegiao) {
        return jpaRepository.existsById(idRegiao);
    }

    @Override
    public long contarTotal() {
        return jpaRepository.count();
    }
}
