package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.infrastructure.entity.ResponsavelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<ResponsavelEntity, Long> {

    @Query("SELECT COALESCE(MAX(r.idResponsavel), 0) + 1 FROM ResponsavelEntity r")
    Long proximoId();

    Optional<ResponsavelEntity> findByEmailLoginIgnoreCase(String emailLogin);

    Optional<ResponsavelEntity> findFirstByAtivoIgnoreCaseOrderByIdResponsavelAsc(String ativo);
}

