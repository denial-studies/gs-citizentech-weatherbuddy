package br.com.fiap.hidroalerta.domain.repository;

import br.com.fiap.hidroalerta.domain.model.Responsavel;

import java.util.List;
import java.util.Optional;

public interface ResponsavelRepository {

    Responsavel salvar(Responsavel responsavel);

    Optional<Responsavel> buscarPorId(Long idResponsavel);

    Optional<Responsavel> buscarPorEmailLogin(String emailLogin);

    Optional<Responsavel> buscarPrimeiroAtivo();

    List<Responsavel> listarTodos();

    void deletarPorId(Long idResponsavel);
}
