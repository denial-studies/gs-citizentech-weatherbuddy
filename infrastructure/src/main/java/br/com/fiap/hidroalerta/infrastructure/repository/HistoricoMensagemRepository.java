package br.com.fiap.hidroalerta.infrastructure.repository;

import br.com.fiap.hidroalerta.infrastructure.entity.HistoricoMensagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoMensagemRepository extends JpaRepository<HistoricoMensagemEntity, Long> {

    @Query("SELECT COALESCE(MAX(m.idMensagem), 0) + 1 FROM HistoricoMensagemEntity m")
    Long proximoId();

    List<HistoricoMensagemEntity> findAllByOrderByDataEnvioDesc();

    List<HistoricoMensagemEntity> findByRegiaoIdRegiaoOrderByDataEnvioDesc(Long idRegiao);
}
