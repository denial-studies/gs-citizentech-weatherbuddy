package br.com.fiap.hidroalerta.presentation.controller.rest;

import br.com.fiap.hidroalerta.application.dto.request.DisparoMonitoramentoRequest;
import br.com.fiap.hidroalerta.application.dto.response.AlertaPendenteResponse;
import br.com.fiap.hidroalerta.application.dto.response.ResultadoMonitoramentoResponse;
import br.com.fiap.hidroalerta.application.service.MonitoramentoService;
import br.com.fiap.hidroalerta.domain.model.SeveridadeAlerta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoramento")
@RequiredArgsConstructor
@Tag(name = "Monitoramento Climático e Envios", description = "Verificação na API Open-Meteo, confirmação manual de alertas e teste de envio individual")
public class MonitoramentoRestController {

    private final MonitoramentoService monitoramentoService;

    @PostMapping("/verificar")
    @Operation(summary = "Verifica o clima de uma região na Open-Meteo (não envia alertas automaticamente)")
    public ResponseEntity<ResultadoMonitoramentoResponse> verificarRegiao(
            @Valid @RequestBody DisparoMonitoramentoRequest request
    ) {
        ResultadoMonitoramentoResponse resultado =
                monitoramentoService.verificarClimaRegiao(request.idRegiao(), request.idResponsavel());
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/regiao/{idRegiao}")
    @Operation(summary = "Verifica em tempo real o clima de uma região específica na Open-Meteo (sem envio automático)")
    public ResponseEntity<ResultadoMonitoramentoResponse> verificarPorIdRegiao(
            @PathVariable Long idRegiao,
            @RequestParam(required = false) Long idResponsavel
    ) {
        ResultadoMonitoramentoResponse resultado =
                monitoramentoService.verificarClimaRegiao(idRegiao, idResponsavel);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/pendentes")
    @Operation(summary = "Lista regiões com clima ruim detectado que aguardam confirmação manual de envio")
    public ResponseEntity<List<AlertaPendenteResponse>> listarPendentes() {
        return ResponseEntity.ok(monitoramentoService.listarAlertasPendentesConfirmacao());
    }

    @PostMapping("/confirmar/{idRegiao}")
    @Operation(summary = "Confirma manualmente o envio de alerta para os moradores ativos de uma região")
    public ResponseEntity<ResultadoMonitoramentoResponse> confirmarEnvio(
            @PathVariable Long idRegiao,
            @RequestParam(required = false) Long idResponsavel,
            @RequestParam(required = false) SeveridadeAlerta severidade,
            @RequestParam(required = false) String mensagem
    ) {
        return ResponseEntity.ok(
                monitoramentoService.confirmarEnvioAlerta(idRegiao, idResponsavel, severidade, mensagem)
        );
    }

    @PostMapping("/testar-numero")
    @Operation(summary = "Testa o envio de alerta para um número específico")
    public ResponseEntity<Map<String, Object>> testarNumeroEspecifico(
            @RequestParam String telefone,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long idRegiao,
            @RequestParam(required = false) String mensagem
    ) {
        String resultado = monitoramentoService.testarEnvioNumeroEspecifico(telefone, nome, idRegiao, mensagem);
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "telefone", telefone,
                "detalhe", resultado
        ));
    }

    @PostMapping("/simular/{idRegiao}")
    @Operation(summary = "Simula leitura de chuva intensa para testar a confirmação manual de envio")
    public ResponseEntity<ResultadoMonitoramentoResponse> simularClimaRuim(
            @PathVariable Long idRegiao,
            @RequestParam(required = false) BigDecimal precipitacaoMm
    ) {
        return ResponseEntity.ok(monitoramentoService.simularClimaRuimRegiao(idRegiao, precipitacaoMm));
    }

    @PostMapping("/todas")
    @Operation(summary = "Executa a verificação climática para todas as regiões monitoradas (sem envio automático)")
    public ResponseEntity<List<ResultadoMonitoramentoResponse>> verificarTodas(
            @RequestParam(required = false) Long idResponsavel
    ) {
        return ResponseEntity.ok(monitoramentoService.verificarClimaTodasRegioes(idResponsavel));
    }
}
