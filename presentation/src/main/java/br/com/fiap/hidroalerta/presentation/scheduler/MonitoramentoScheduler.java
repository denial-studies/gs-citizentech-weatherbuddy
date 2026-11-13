package br.com.fiap.hidroalerta.presentation.scheduler;

import br.com.fiap.hidroalerta.application.dto.response.RegiaoResponse;
import br.com.fiap.hidroalerta.application.dto.response.ResultadoMonitoramentoResponse;
import br.com.fiap.hidroalerta.application.service.MonitoramentoService;
import br.com.fiap.hidroalerta.application.service.RegiaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoramentoScheduler {

    private final RegiaoService regiaoService;
    private final MonitoramentoService monitoramentoService;

    @Scheduled(fixedRate = 3600000, initialDelay = 60000)
    public void executarMonitoramentoHorario() {
        log.info("[Scheduler] Iniciando ciclo automático de monitoramento meteorológico das regiões...");
        try {
            List<RegiaoResponse> regioes = regiaoService.listarTodas();
            if (regioes.isEmpty()) {
                log.info("[Scheduler] Nenhuma região cadastrada para monitoramento no momento.");
                return;
            }

            for (RegiaoResponse regiao : regioes) {
                try {
                    ResultadoMonitoramentoResponse resultado =
                            monitoramentoService.verificarClimaRegiao(regiao.idRegiao(), null);
                    log.info("[Scheduler] Região '{}' verificada -> {}", regiao.nomeRegiao(), resultado.mensagemResumo());
                } catch (Exception exRegiao) {
                    log.error("[Scheduler] Erro ao verificar clima da região ID {}: {}",
                            regiao.idRegiao(), exRegiao.getMessage());
                }
            }
        } catch (Exception ex) {
            log.warn("[Scheduler] Não foi possível concluir o ciclo agendado: {}", ex.getMessage());
        }
    }
}

