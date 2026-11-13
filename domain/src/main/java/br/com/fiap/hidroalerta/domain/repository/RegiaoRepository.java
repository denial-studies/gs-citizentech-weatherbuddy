package br.com.fiap.hidroalerta.domain.repository;

import br.com.fiap.hidroalerta.domain.model.Regiao;

import java.util.List;
import java.util.Optional;

public interface RegiaoRepository {

    Regiao salvar(Regiao regiao);

    Optional<Regiao> buscarPorId(Long idRegiao);

    List<Regiao> listarTodas();

    void deletarPorId(Long idRegiao);

    boolean existePorId(Long idRegiao);

    long contarTotal();
}

