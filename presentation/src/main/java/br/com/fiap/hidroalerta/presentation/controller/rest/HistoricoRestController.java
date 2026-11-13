package br.com.fiap.hidroalerta.presentation.controller.rest;

import br.com.fiap.hidroalerta.application.dto.response.HistoricoChuvaResponse;
import br.com.fiap.hidroalerta.application.dto.response.HistoricoMensagemResponse;
import br.com.fiap.hidroalerta.application.service.HistoricoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historico")
@RequiredArgsConstructor
@Tag(name = "Históricos de Chuva e Mensagens", description = "Consultas de auditoria de medições pluviométricas e alertas enviados")
public class HistoricoRestController {

    private final HistoricoService historicoService;

    @GetMapping("/chuvas")
    @Operation(summary = "Lista todo o histórico de medições de chuva ordenado da mais recente para a mais antiga")
    public ResponseEntity<List<HistoricoChuvaResponse>> listarChuvas() {
        return ResponseEntity.ok(historicoService.listarHistoricoChuvas());
    }

    @GetMapping("/chuvas/regiao/{idRegiao}")
    @Operation(summary = "Lista o histórico de medições de chuva de uma região específica")
    public ResponseEntity<List<HistoricoChuvaResponse>> listarChuvasPorRegiao(@PathVariable Long idRegiao) {
        return ResponseEntity.ok(historicoService.listarHistoricoChuvasPorRegiao(idRegiao));
    }

    @DeleteMapping("/chuvas/{idChuva}")
    @Operation(summary = "Remove um registro de medição de chuva")
    public ResponseEntity<Void> deletarChuva(@PathVariable Long idChuva) {
        historicoService.deletarHistoricoChuva(idChuva);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mensagens")
    @Operation(summary = "Lista todo o histórico de alertas e mensagens preventivas enviadas")
    public ResponseEntity<List<HistoricoMensagemResponse>> listarMensagens() {
        return ResponseEntity.ok(historicoService.listarHistoricoMensagens());
    }

    @GetMapping("/mensagens/regiao/{idRegiao}")
    @Operation(summary = "Lista o histórico de alertas enviados para uma região específica")
    public ResponseEntity<List<HistoricoMensagemResponse>> listarMensagensPorRegiao(@PathVariable Long idRegiao) {
        return ResponseEntity.ok(historicoService.listarHistoricoMensagensPorRegiao(idRegiao));
    }

    @DeleteMapping("/mensagens/{idMensagem}")
    @Operation(summary = "Remove um registro do histórico de mensagens")
    public ResponseEntity<Void> deletarMensagem(@PathVariable Long idMensagem) {
        historicoService.deletarHistoricoMensagem(idMensagem);
        return ResponseEntity.noContent().build();
    }
}

