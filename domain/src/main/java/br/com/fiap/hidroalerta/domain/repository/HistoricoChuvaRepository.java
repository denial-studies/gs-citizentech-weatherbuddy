package br.com.fiap.hidroalerta.domain.repository;

import br.com.fiap.hidroalerta.domain.model.HistoricoChuva;

import java.util.List;
import java.util.Optional;

public interface HistoricoChuvaRepository {

    HistoricoChuva salvar(HistoricoChuva historicoChuva);

    Optional<HistoricoChuva> buscarPorId(Long idChuva);

    List<HistoricoChuva> listarTodosOrdenadosPorData();

    List<HistoricoChuva> listarPorRegiao(Long idRegiao);

    void deletarPorId(Long idChuva);
}

