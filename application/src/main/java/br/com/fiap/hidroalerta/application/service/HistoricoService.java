package br.com.fiap.hidroalerta.application.service;

import br.com.fiap.hidroalerta.application.dto.response.HistoricoChuvaResponse;
import br.com.fiap.hidroalerta.application.dto.response.HistoricoMensagemResponse;
import br.com.fiap.hidroalerta.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.hidroalerta.domain.model.HistoricoChuva;
import br.com.fiap.hidroalerta.domain.model.HistoricoMensagem;
import br.com.fiap.hidroalerta.domain.repository.HistoricoChuvaRepository;
import br.com.fiap.hidroalerta.domain.repository.HistoricoMensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final HistoricoChuvaRepository historicoChuvaRepository;
    private final HistoricoMensagemRepository historicoMensagemRepository;

    @Transactional(readOnly = true)
    public List<HistoricoChuvaResponse> listarHistoricoChuvas() {
        return historicoChuvaRepository.listarTodosOrdenadosPorData().stream()
                .map(this::toChuvaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoChuvaResponse> listarHistoricoChuvasPorRegiao(Long idRegiao) {
        return historicoChuvaRepository.listarPorRegiao(idRegiao).stream()
                .map(this::toChuvaResponse)
                .toList();
    }

    @Transactional
    public void deletarHistoricoChuva(Long idChuva) {
        if (historicoChuvaRepository.buscarPorId(idChuva).isEmpty()) {
            throw new RecursoNaoEncontradoException("Registro de chuva não encontrado para o ID: " + idChuva);
        }
        historicoChuvaRepository.deletarPorId(idChuva);
    }

    @Transactional(readOnly = true)
    public List<HistoricoMensagemResponse> listarHistoricoMensagens() {
        return historicoMensagemRepository.listarTodosOrdenadosPorData().stream()
                .map(this::toMensagemResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoMensagemResponse> listarHistoricoMensagensPorRegiao(Long idRegiao) {
        return historicoMensagemRepository.listarPorRegiao(idRegiao).stream()
                .map(this::toMensagemResponse)
                .toList();
    }

    @Transactional
    public void deletarHistoricoMensagem(Long idMensagem) {
        if (historicoMensagemRepository.buscarPorId(idMensagem).isEmpty()) {
            throw new RecursoNaoEncontradoException("Histórico de mensagem não encontrado para o ID: " + idMensagem);
        }
        historicoMensagemRepository.deletarPorId(idMensagem);
    }

    @Transactional(readOnly = true)
    public long contarTotalAlertasEmitidos() {
        return historicoMensagemRepository.contarTotal();
    }

    private HistoricoChuvaResponse toChuvaResponse(HistoricoChuva h) {
        return new HistoricoChuvaResponse(
                h.getIdChuva(),
                h.getIdRegiao(),
                h.getNomeRegiao(),
                h.getBairroRegiao(),
                h.getPrecipitacaoMm(),
                h.getIntensidade(),
                h.getDataMedicao()
        );
    }

    private HistoricoMensagemResponse toMensagemResponse(HistoricoMensagem m) {
        return new HistoricoMensagemResponse(
                m.getIdMensagem(),
                m.getIdRegiao(),
                m.getNomeRegiao(),
                m.getBairroRegiao(),
                m.getIdResponsavel(),
                m.getNomeResponsavel(),
                m.getSeveridade(),
                m.getConteudo(),
                m.getQuantidadeEnviosSucesso(),
                m.getDataEnvio()
        );
    }
}

