package br.com.fiap.hidroalerta.application.service;

import br.com.fiap.hidroalerta.application.dto.response.AlertaPendenteResponse;
import br.com.fiap.hidroalerta.application.dto.response.ResultadoMonitoramentoResponse;
import br.com.fiap.hidroalerta.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.hidroalerta.application.exception.RegraNegocioException;
import br.com.fiap.hidroalerta.application.port.ClimaTelemetryPort;
import br.com.fiap.hidroalerta.application.port.NotificacaoService;
import br.com.fiap.hidroalerta.domain.model.HistoricoChuva;
import br.com.fiap.hidroalerta.domain.model.HistoricoMensagem;
import br.com.fiap.hidroalerta.domain.model.Inscrito;
import br.com.fiap.hidroalerta.domain.model.IntensidadeChuva;
import br.com.fiap.hidroalerta.domain.model.Regiao;
import br.com.fiap.hidroalerta.domain.model.Responsavel;
import br.com.fiap.hidroalerta.domain.model.SeveridadeAlerta;
import br.com.fiap.hidroalerta.domain.repository.HistoricoChuvaRepository;
import br.com.fiap.hidroalerta.domain.repository.HistoricoMensagemRepository;
import br.com.fiap.hidroalerta.domain.repository.InscritoRepository;
import br.com.fiap.hidroalerta.domain.repository.RegiaoRepository;
import br.com.fiap.hidroalerta.domain.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoramentoService {

    private final RegiaoRepository regiaoRepository;
    private final InscritoRepository inscritoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final HistoricoChuvaRepository historicoChuvaRepository;
    private final HistoricoMensagemRepository historicoMensagemRepository;
    private final ClimaTelemetryPort climaTelemetryPort;
    private final NotificacaoService notificacaoService;

    /**
     * Consulta a API Open-Meteo e registra a leitura no histórico de chuvas.
     * IMPORTANTE: Nunca dispara alertas sozinho! Caso o clima esteja ruim (acima do limite),
     * o alerta fica pendente aguardando a confirmação manual do operador.
     */
    @Transactional
    public ResultadoMonitoramentoResponse verificarClimaRegiao(Long idRegiao, Long idResponsavel) {
        Regiao regiao = regiaoRepository.buscarPorId(idRegiao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada para o ID: " + idRegiao));

        BigDecimal precipitacaoAtualMm = climaTelemetryPort.obterPrecipitacaoAtualMm(
                regiao.getLatitude(),
                regiao.getLongitude()
        );

        return processarLeituraClimatica(regiao, precipitacaoAtualMm);
    }

    /**
     * Registra uma leitura simulada de chuva forte para que o operador possa testar
     * o fluxo de "Clima Ruim -> Confirmar Envio" a qualquer momento.
     * Também nunca envia alertas sozinho.
     */
    @Transactional
    public ResultadoMonitoramentoResponse simularClimaRuimRegiao(Long idRegiao, BigDecimal precipitacaoSimuladaMm) {
        Regiao regiao = regiaoRepository.buscarPorId(idRegiao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada para o ID: " + idRegiao));

        BigDecimal volume = precipitacaoSimuladaMm != null
                ? precipitacaoSimuladaMm
                : regiao.getLimiteChuvaMm().add(new BigDecimal("10.00"));

        return processarLeituraClimatica(regiao, volume);
    }

    private ResultadoMonitoramentoResponse processarLeituraClimatica(Regiao regiao, BigDecimal precipitacaoMm) {
        HistoricoChuva medicao = HistoricoChuva.registrarMedicao(regiao, precipitacaoMm);
        HistoricoChuva medicaoSalva = historicoChuvaRepository.salvar(medicao);

        boolean climaRuim = regiao.ultrapassouLimiteSeguranca(precipitacaoMm);
        SeveridadeAlerta severidade = regiao.determinarSeveridade(precipitacaoMm);

        String textoResumo;
        if (climaRuim) {
            textoResumo = String.format(
                    "CLIMA RUIM DETECTADO em %s (%s): %s mm medidos (limite: %s mm | severidade: %s). "
                            + "Nenhum alerta foi enviado automaticamente. Confirme o disparo manualmente para notificar os moradores!",
                    regiao.getNomeRegiao(),
                    regiao.getBairro(),
                    precipitacaoMm.toPlainString(),
                    regiao.getLimiteChuvaMm().toPlainString(),
                    severidade.name()
            );
        } else {
            textoResumo = String.format(
                    "Medição concluída em %s (%s): %s mm (%s). Limiar de %s mm não atingido.",
                    regiao.getNomeRegiao(),
                    regiao.getBairro(),
                    precipitacaoMm.toPlainString(),
                    medicaoSalva.getIntensidade().name(),
                    regiao.getLimiteChuvaMm().toPlainString()
            );
        }

        return new ResultadoMonitoramentoResponse(
                regiao.getIdRegiao(),
                regiao.getNomeRegiao(),
                regiao.getBairro(),
                precipitacaoMm,
                regiao.getLimiteChuvaMm(),
                medicaoSalva.getIntensidade(),
                climaRuim,
                severidade,
                0,
                textoResumo,
                medicaoSalva.getDataMedicao()
        );
    }

    /**
     * Confirma e executa manualmente o envio de alerta para os moradores ativos de uma região.
     */
    @Transactional
    public ResultadoMonitoramentoResponse confirmarEnvioAlerta(
            Long idRegiao,
            Long idResponsavel,
            SeveridadeAlerta severidadeInformada,
            String mensagemCustomizada
    ) {
        Regiao regiao = regiaoRepository.buscarPorId(idRegiao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada para o ID: " + idRegiao));

        List<HistoricoChuva> leituras = historicoChuvaRepository.listarPorRegiao(idRegiao);
        BigDecimal precipitacaoRef = leituras.isEmpty()
                ? regiao.getLimiteChuvaMm()
                : leituras.get(0).getPrecipitacaoMm();
        IntensidadeChuva intensidadeRef = leituras.isEmpty()
                ? IntensidadeChuva.classificar(precipitacaoRef)
                : leituras.get(0).getIntensidade();

        SeveridadeAlerta severidade = severidadeInformada != null
                ? severidadeInformada
                : regiao.determinarSeveridade(precipitacaoRef);

        String conteudoAlerta = (mensagemCustomizada != null && !mensagemCustomizada.isBlank())
                ? mensagemCustomizada.trim()
                : regiao.gerarMensagemAlerta(precipitacaoRef, severidade);

        List<Inscrito> moradoresAtivos = inscritoRepository.listarAtivosPorRegiao(idRegiao);
        int enviosComSucesso = 0;

        for (Inscrito morador : moradoresAtivos) {
            boolean enviado = notificacaoService.enviarAlerta(
                    morador.getTelefone(),
                    morador.getNomeInscrito(),
                    regiao.getNomeRegiao(),
                    conteudoAlerta
            );
            if (enviado) {
                enviosComSucesso++;
            }
        }

        Long responsavelEfetivoId = resolverResponsavelId(idResponsavel);

        HistoricoMensagem registroMensagem = HistoricoMensagem.builder()
                .idRegiao(regiao.getIdRegiao())
                .nomeRegiao(regiao.getNomeRegiao())
                .bairroRegiao(regiao.getBairro())
                .idResponsavel(responsavelEfetivoId)
                .severidade(severidade)
                .conteudo(conteudoAlerta)
                .quantidadeEnviosSucesso(enviosComSucesso)
                .dataEnvio(LocalDateTime.now())
                .build();

        historicoMensagemRepository.salvar(registroMensagem);

        String textoResumo = String.format(
                "Envio CONFIRMADO! Alerta %s disparado para %s (%s): %d número(s) ativo(s) notificado(s) com sucesso.",
                severidade.name(),
                regiao.getNomeRegiao(),
                regiao.getBairro(),
                enviosComSucesso
        );

        return new ResultadoMonitoramentoResponse(
                regiao.getIdRegiao(),
                regiao.getNomeRegiao(),
                regiao.getBairro(),
                precipitacaoRef,
                regiao.getLimiteChuvaMm(),
                intensidadeRef,
                true,
                severidade,
                enviosComSucesso,
                textoResumo,
                LocalDateTime.now()
        );
    }

    /**
     * Testa o envio de mensagem preventiva para um número específico (sem enviar para toda a região).
     */
    public String testarEnvioNumeroEspecifico(
            String telefone,
            String nomeDestinatario,
            Long idRegiao,
            String mensagem
    ) {
        if (telefone == null || telefone.isBlank()) {
            throw new RegraNegocioException("Informe um número de telefone para realizar o teste de envio.");
        }

        String nome = (nomeDestinatario != null && !nomeDestinatario.isBlank())
                ? nomeDestinatario.trim()
                : "Morador de Teste";

        String nomeRegiao = "Região de Teste";
        if (idRegiao != null) {
            nomeRegiao = regiaoRepository.buscarPorId(idRegiao)
                    .map(r -> r.getNomeRegiao() + " (" + r.getBairro() + ")")
                    .orElse("Região ID " + idRegiao);
        }

        String texto = (mensagem != null && !mensagem.isBlank())
                ? mensagem.trim()
                : "[TESTE WEATHER BUDDY] Mensagem de verificação do sistema de alerta preventivo para " + nomeRegiao + ".";

        boolean enviado = notificacaoService.enviarAlerta(telefone.trim(), nome, nomeRegiao, texto);
        if (!enviado) {
            throw new RegraNegocioException("Falha ao simular envio para o número " + telefone);
        }

        return String.format(
                "Teste de envio realizado com sucesso para o número %s (%s) na região %s! Mensagem: \"%s\"",
                telefone.trim(),
                nome,
                nomeRegiao,
                texto
        );
    }

    /**
     * Lista todas as regiões cuja última leitura pluviométrica indicou clima ruim (acima do limite)
     * e que ainda aguardam confirmação manual de envio pelo operador.
     */
    @Transactional(readOnly = true)
    public List<AlertaPendenteResponse> listarAlertasPendentesConfirmacao() {
        List<AlertaPendenteResponse> pendentes = new ArrayList<>();
        List<Regiao> regioes = regiaoRepository.listarTodas();

        for (Regiao regiao : regioes) {
            List<HistoricoChuva> chuvas = historicoChuvaRepository.listarPorRegiao(regiao.getIdRegiao());
            if (chuvas.isEmpty()) {
                continue;
            }

            HistoricoChuva ultimaChuva = chuvas.get(0);
            if (!regiao.ultrapassouLimiteSeguranca(ultimaChuva.getPrecipitacaoMm())) {
                continue;
            }

            List<HistoricoMensagem> mensagens = historicoMensagemRepository.listarPorRegiao(regiao.getIdRegiao());
            boolean jaConfirmadoAposLeitura = !mensagens.isEmpty()
                    && mensagens.get(0).getDataEnvio() != null
                    && ultimaChuva.getDataMedicao() != null
                    && !mensagens.get(0).getDataEnvio().isBefore(ultimaChuva.getDataMedicao());

            if (!jaConfirmadoAposLeitura) {
                SeveridadeAlerta severidade = regiao.determinarSeveridade(ultimaChuva.getPrecipitacaoMm());
                String mensagemSugerida = regiao.gerarMensagemAlerta(ultimaChuva.getPrecipitacaoMm(), severidade);
                long moradoresAtivos = inscritoRepository.listarAtivosPorRegiao(regiao.getIdRegiao()).size();

                pendentes.add(new AlertaPendenteResponse(
                        regiao.getIdRegiao(),
                        regiao.getNomeRegiao(),
                        regiao.getBairro(),
                        regiao.getNivelRisco(),
                        ultimaChuva.getPrecipitacaoMm(),
                        regiao.getLimiteChuvaMm(),
                        ultimaChuva.getIntensidade(),
                        severidade,
                        mensagemSugerida,
                        moradoresAtivos,
                        ultimaChuva.getDataMedicao()
                ));
            }
        }

        return pendentes;
    }

    @Transactional
    public List<ResultadoMonitoramentoResponse> verificarClimaTodasRegioes(Long idResponsavel) {
        return regiaoRepository.listarTodas().stream()
                .map(regiao -> verificarClimaRegiao(regiao.getIdRegiao(), idResponsavel))
                .toList();
    }

    private Long resolverResponsavelId(Long idResponsavelInformado) {
        if (idResponsavelInformado != null && responsavelRepository.buscarPorId(idResponsavelInformado).isPresent()) {
            return idResponsavelInformado;
        }
        return responsavelRepository.buscarPrimeiroAtivo()
                .map(Responsavel::getIdResponsavel)
                .or(() -> responsavelRepository.listarTodos().stream().findFirst().map(Responsavel::getIdResponsavel))
                .orElseThrow(() -> new RegraNegocioException(
                        "Não há responsável cadastrado em tb_responsavel para vincular ao histórico de mensagem."
                ));
    }
}
