package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.domain.model.HistoricoChuva;
import br.com.fiap.hidroalerta.infrastructure.entity.HistoricoChuvaEntity;
import br.com.fiap.hidroalerta.infrastructure.entity.RegiaoEntity;
import br.com.fiap.hidroalerta.infrastructure.mapper.HistoricoChuvaMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HistoricoChuvaRepositoryImpl implements br.com.fiap.hidroalerta.domain.repository.HistoricoChuvaRepository {

    private final HistoricoChuvaRepository jpaRepository;
    private final RegiaoRepository regiaoJpaRepository;
    private final HistoricoChuvaMapper mapper;

    public HistoricoChuvaRepositoryImpl(
            @Lazy HistoricoChuvaRepository jpaRepository,
            @Lazy RegiaoRepository regiaoJpaRepository,
            HistoricoChuvaMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.regiaoJpaRepository = regiaoJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public HistoricoChuva salvar(HistoricoChuva historicoChuva) {
        HistoricoChuvaEntity entity = mapper.toEntity(historicoChuva);
        if (entity.getIdChuva() == null) {
            entity.setIdChuva(jpaRepository.proximoId());
        }
        RegiaoEntity regiaoEntity = regiaoJpaRepository.findById(historicoChuva.getIdRegiao())
                .orElseThrow(() -> new IllegalArgumentException("Região não encontrada: " + historicoChuva.getIdRegiao()));
        entity.setRegiao(regiaoEntity);
        HistoricoChuvaEntity saved = jpaRepository.save(entity);
        saved.setRegiao(regiaoEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<HistoricoChuva> buscarPorId(Long idChuva) {
        return jpaRepository.findById(idChuva).map(mapper::toDomain);
    }

    @Override
    public List<HistoricoChuva> listarTodosOrdenadosPorData() {
        return jpaRepository.findAllByOrderByDataMedicaoDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<HistoricoChuva> listarPorRegiao(Long idRegiao) {
        return jpaRepository.findByRegiaoIdRegiaoOrderByDataMedicaoDesc(idRegiao).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long idChuva) {
        jpaRepository.deleteById(idChuva);
    }
}
