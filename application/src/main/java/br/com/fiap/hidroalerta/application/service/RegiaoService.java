package br.com.fiap.hidroalerta.application.service;

import br.com.fiap.hidroalerta.application.dto.request.RegiaoRequest;
import br.com.fiap.hidroalerta.application.dto.response.RegiaoResponse;
import br.com.fiap.hidroalerta.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.hidroalerta.domain.model.Regiao;
import br.com.fiap.hidroalerta.domain.repository.RegiaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegiaoService {

    private final RegiaoRepository regiaoRepository;

    @Transactional
    public RegiaoResponse criar(RegiaoRequest request) {
        Regiao regiao = Regiao.builder()
                .nomeRegiao(request.nomeRegiao().trim())
                .bairro(request.bairro().trim())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .limiteChuvaMm(request.limiteChuvaMm())
                .nivelRisco(request.nivelRisco())
                .build();

        return toResponse(regiaoRepository.salvar(regiao));
    }

    @Transactional
    public RegiaoResponse atualizar(Long id, RegiaoRequest request) {
        Regiao existente = regiaoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada com ID: " + id));

        existente.setNomeRegiao(request.nomeRegiao().trim());
        existente.setBairro(request.bairro().trim());
        existente.setLatitude(request.latitude());
        existente.setLongitude(request.longitude());
        existente.setLimiteChuvaMm(request.limiteChuvaMm());
        existente.setNivelRisco(request.nivelRisco());

        return toResponse(regiaoRepository.salvar(existente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!regiaoRepository.existePorId(id)) {
            throw new RecursoNaoEncontradoException("Região não encontrada com ID: " + id);
        }
        regiaoRepository.deletarPorId(id);
    }

    @Transactional(readOnly = true)
    public RegiaoResponse buscarPorId(Long id) {
        return regiaoRepository.buscarPorId(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<RegiaoResponse> listarTodas() {
        return regiaoRepository.listarTodas().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long contarTotal() {
        return regiaoRepository.contarTotal();
    }

    private RegiaoResponse toResponse(Regiao regiao) {
        return new RegiaoResponse(
                regiao.getIdRegiao(),
                regiao.getNomeRegiao(),
                regiao.getBairro(),
                regiao.getLatitude(),
                regiao.getLongitude(),
                regiao.getLimiteChuvaMm(),
                regiao.getNivelRisco()
        );
    }
}

