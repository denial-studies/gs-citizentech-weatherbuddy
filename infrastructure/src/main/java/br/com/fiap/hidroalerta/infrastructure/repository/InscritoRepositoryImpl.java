package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.domain.model.Inscrito;
import br.com.fiap.hidroalerta.infrastructure.entity.InscritoEntity;
import br.com.fiap.hidroalerta.infrastructure.entity.RegiaoEntity;
import br.com.fiap.hidroalerta.infrastructure.mapper.InscritoMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InscritoRepositoryImpl implements br.com.fiap.hidroalerta.domain.repository.InscritoRepository {

    private final InscritoRepository jpaRepository;
    private final RegiaoRepository regiaoJpaRepository;
    private final InscritoMapper mapper;

    public InscritoRepositoryImpl(
            @Lazy InscritoRepository jpaRepository,
            @Lazy RegiaoRepository regiaoJpaRepository,
            InscritoMapper mapper
    ) {
        this.jpaRepository = jpaRepository;
        this.regiaoJpaRepository = regiaoJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Inscrito salvar(Inscrito inscrito) {
        InscritoEntity entity = mapper.toEntity(inscrito);
        if (entity.getIdInscrito() == null) {
            entity.setIdInscrito(jpaRepository.proximoId());
        }
        RegiaoEntity regiaoEntity = regiaoJpaRepository.findById(inscrito.getIdRegiao())
                .orElseThrow(() -> new IllegalArgumentException("Região não encontrada: " + inscrito.getIdRegiao()));
        entity.setRegiao(regiaoEntity);
        InscritoEntity saved = jpaRepository.save(entity);
        saved.setRegiao(regiaoEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Inscrito> buscarPorId(Long idInscrito) {
        return jpaRepository.findById(idInscrito).map(mapper::toDomain);
    }

    @Override
    public Optional<Inscrito> buscarPorTelefone(String telefone) {
        return jpaRepository.findByTelefone(telefone).map(mapper::toDomain);
    }

    @Override
    public List<Inscrito> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Inscrito> listarAtivosPorRegiao(Long idRegiao) {
        return jpaRepository.findByRegiaoIdRegiaoAndAtivoIgnoreCase(idRegiao, "S").stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long idInscrito) {
        jpaRepository.deleteById(idInscrito);
    }

    @Override
    public long contarAtivos() {
        return jpaRepository.countByAtivoIgnoreCase("S");
    }
}
