package br.com.fiap.hidroalerta.domain.repository;

import br.com.fiap.hidroalerta.domain.model.Inscrito;

import java.util.List;
import java.util.Optional;

public interface InscritoRepository {

    Inscrito salvar(Inscrito inscrito);

    Optional<Inscrito> buscarPorId(Long idInscrito);

    Optional<Inscrito> buscarPorTelefone(String telefone);

    List<Inscrito> listarTodos();

    List<Inscrito> listarAtivosPorRegiao(Long idRegiao);

    void deletarPorId(Long idInscrito);

    long contarAtivos();
}

