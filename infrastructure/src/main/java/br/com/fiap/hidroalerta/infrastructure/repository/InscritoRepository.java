package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.infrastructure.entity.InscritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscritoRepository extends JpaRepository<InscritoEntity, Long> {

    @Query("SELECT COALESCE(MAX(i.idInscrito), 0) + 1 FROM InscritoEntity i")
    Long proximoId();

    Optional<InscritoEntity> findByTelefone(String telefone);

    List<InscritoEntity> findByRegiaoIdRegiaoAndAtivoIgnoreCase(Long idRegiao, String ativo);

    long countByAtivoIgnoreCase(String ativo);
}
