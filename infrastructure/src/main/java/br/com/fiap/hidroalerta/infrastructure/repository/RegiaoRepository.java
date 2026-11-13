package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.infrastructure.entity.RegiaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegiaoRepository extends JpaRepository<RegiaoEntity, Long> {

    @Query("SELECT COALESCE(MAX(r.idRegiao), 0) + 1 FROM RegiaoEntity r")
    Long proximoId();
}

