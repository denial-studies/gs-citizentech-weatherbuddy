package br.com.fiap.hidroalerta.application.service;

import br.com.fiap.hidroalerta.application.dto.request.AtualizarInscritoRequest;
import br.com.fiap.hidroalerta.application.dto.request.CriarInscritoRequest;
import br.com.fiap.hidroalerta.application.dto.response.InscritoResponse;
import br.com.fiap.hidroalerta.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.hidroalerta.application.exception.RegraNegocioException;
import br.com.fiap.hidroalerta.domain.model.Inscrito;
import br.com.fiap.hidroalerta.domain.model.Regiao;
import br.com.fiap.hidroalerta.domain.repository.InscritoRepository;
import br.com.fiap.hidroalerta.domain.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscritoService {

    private final InscritoRepository inscritoRepository;
    private final RegiaoRepository regiaoRepository;

    @Transactional
    public InscritoResponse inscrever(CriarInscritoRequest request) {
        Regiao regiao = regiaoRepository.buscarPorId(request.idRegiao())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada para o ID: " + request.idRegiao()));

        String telefoneNormalizado = request.telefone().trim();
        inscritoRepository.buscarPorTelefone(telefoneNormalizado).ifPresent(existente -> {
            throw new RegraNegocioException("Já existe um morador inscrito com o número de telefone informado: " + telefoneNormalizado);
        });

        Inscrito novoInscrito = Inscrito.builder()
                .idRegiao(regiao.getIdRegiao())
                .nomeRegiao(regiao.getNomeRegiao())
                .bairroRegiao(regiao.getBairro())
                .nomeInscrito(request.nomeInscrito().trim())
                .telefone(telefoneNormalizado)
                .ativo("S")
                .dataInscricao(LocalDate.now())
                .build();

        Inscrito salvo = inscritoRepository.salvar(novoInscrito);
        return toResponse(salvo);
    }

    @Transactional
    public InscritoResponse atualizar(Long id, AtualizarInscritoRequest request) {
        Inscrito inscrito = inscritoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrito não encontrado com ID: " + id));

        Regiao regiao = regiaoRepository.buscarPorId(request.idRegiao())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada para o ID: " + request.idRegiao()));

        String telefoneNormalizado = request.telefone().trim();
        inscritoRepository.buscarPorTelefone(telefoneNormalizado).ifPresent(outro -> {
            if (!outro.getIdInscrito().equals(id)) {
                throw new RegraNegocioException("O telefone informado já pertence a outro cadastro.");
            }
        });

        inscrito.setNomeInscrito(request.nomeInscrito().trim());
        inscrito.setTelefone(telefoneNormalizado);
        inscrito.setIdRegiao(regiao.getIdRegiao());
        inscrito.setNomeRegiao(regiao.getNomeRegiao());
        inscrito.setBairroRegiao(regiao.getBairro());
        if (request.ativo() != null && !request.ativo().isBlank()) {
            inscrito.setAtivo(request.ativo().toUpperCase());
        }

        return toResponse(inscritoRepository.salvar(inscrito));
    }

    @Transactional
    public InscritoResponse cancelar(Long id) {
        Inscrito inscrito = inscritoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrito não encontrado com ID: " + id));
        inscrito.cancelarInscricao();
        return toResponse(inscritoRepository.salvar(inscrito));
    }

    @Transactional
    public InscritoResponse alternarStatus(Long id) {
        Inscrito inscrito = inscritoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrito não encontrado com ID: " + id));
        if (inscrito.isAtivo()) {
            inscrito.cancelarInscricao();
        } else {
            inscrito.setAtivo("S");
        }
        return toResponse(inscritoRepository.salvar(inscrito));
    }

    @Transactional
    public void deletar(Long id) {
        if (inscritoRepository.buscarPorId(id).isEmpty()) {
            throw new RecursoNaoEncontradoException("Inscrito não encontrado com ID: " + id);
        }
        inscritoRepository.deletarPorId(id);
    }

    @Transactional(readOnly = true)
    public InscritoResponse buscarPorId(Long id) {
        return inscritoRepository.buscarPorId(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrito não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<InscritoResponse> listarTodos() {
        return inscritoRepository.listarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InscritoResponse> listarAtivosPorRegiao(Long idRegiao) {
        return inscritoRepository.listarAtivosPorRegiao(idRegiao).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long contarAtivos() {
        return inscritoRepository.contarAtivos();
    }

    private InscritoResponse toResponse(Inscrito inscrito) {
        return new InscritoResponse(
                inscrito.getIdInscrito(),
                inscrito.getIdRegiao(),
                inscrito.getNomeRegiao(),
                inscrito.getBairroRegiao(),
                inscrito.getNomeInscrito(),
                inscrito.getTelefone(),
                inscrito.getAtivo(),
                inscrito.getDataInscricao()
        );
    }
}
