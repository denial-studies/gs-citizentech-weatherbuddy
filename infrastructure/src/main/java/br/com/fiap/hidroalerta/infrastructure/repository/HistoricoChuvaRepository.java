package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.infrastructure.entity.HistoricoChuvaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoChuvaRepository extends JpaRepository<HistoricoChuvaEntity, Long> {

    @Query("SELECT COALESCE(MAX(h.idChuva), 0) + 1 FROM HistoricoChuvaEntity h")
    Long proximoId();

    List<HistoricoChuvaEntity> findAllByOrderByDataMedicaoDesc();

    List<HistoricoChuvaEntity> findByRegiaoIdRegiaoOrderByDataMedicaoDesc(Long idRegiao);
}
