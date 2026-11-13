package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.domain.model.Responsavel;
import br.com.fiap.hidroalerta.infrastructure.entity.ResponsavelEntity;
import br.com.fiap.hidroalerta.infrastructure.mapper.ResponsavelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ResponsavelRepositoryImpl implements br.com.fiap.hidroalerta.domain.repository.ResponsavelRepository {

    private final ResponsavelRepository jpaRepository;
    private final ResponsavelMapper mapper;

    public ResponsavelRepositoryImpl(@Lazy ResponsavelRepository jpaRepository, ResponsavelMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Responsavel salvar(Responsavel responsavel) {
        ResponsavelEntity entity = mapper.toEntity(responsavel);
        if (entity.getIdResponsavel() == null) {
            entity.setIdResponsavel(jpaRepository.proximoId());
        }
        ResponsavelEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Responsavel> buscarPorId(Long idResponsavel) {
        return jpaRepository.findById(idResponsavel).map(mapper::toDomain);
    }

    @Override
    public Optional<Responsavel> buscarPorEmailLogin(String emailLogin) {
        return jpaRepository.findByEmailLoginIgnoreCase(emailLogin).map(mapper::toDomain);
    }

    @Override
    public Optional<Responsavel> buscarPrimeiroAtivo() {
        return jpaRepository.findFirstByAtivoIgnoreCaseOrderByIdResponsavelAsc("S")
                .map(mapper::toDomain);
    }

    @Override
    public List<Responsavel> listarTodos() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deletarPorId(Long idResponsavel) {
        jpaRepository.deleteById(idResponsavel);
    }
}
