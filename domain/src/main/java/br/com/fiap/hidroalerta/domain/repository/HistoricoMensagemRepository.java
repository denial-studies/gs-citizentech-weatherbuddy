package br.com.fiap.hidroalerta.domain.repository;

import br.com.fiap.hidroalerta.domain.model.HistoricoMensagem;

import java.util.List;
import java.util.Optional;

public interface HistoricoMensagemRepository {

    HistoricoMensagem salvar(HistoricoMensagem historicoMensagem);

    Optional<HistoricoMensagem> buscarPorId(Long idMensagem);

    List<HistoricoMensagem> listarTodosOrdenadosPorData();

    List<HistoricoMensagem> listarPorRegiao(Long idRegiao);

    void deletarPorId(Long idMensagem);

    long contarTotal();
}

