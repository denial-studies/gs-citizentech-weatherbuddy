package br.com.fiap.hidroalerta.presentation.controller.rest;

import br.com.fiap.hidroalerta.application.dto.request.AtualizarInscritoRequest;
import br.com.fiap.hidroalerta.application.dto.request.CriarInscritoRequest;
import br.com.fiap.hidroalerta.application.dto.response.InscritoResponse;
import br.com.fiap.hidroalerta.application.service.InscritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inscritos")
@RequiredArgsConstructor
@Tag(name = "Inscritos (Moradores)", description = "CRUD de moradores cadastrados para recebimento de alertas preventivos")
public class InscritoRestController {

    private final InscritoService inscritoService;

    @PostMapping
    @Operation(summary = "Inscreve um novo morador em uma região de risco (Rota Pública)")
    public ResponseEntity<InscritoResponse> inscrever(@Valid @RequestBody CriarInscritoRequest request) {
        InscritoResponse criado = inscritoService.inscrever(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    @Operation(summary = "Lista todos os moradores inscritos")
    public ResponseEntity<List<InscritoResponse>> listarTodos() {
        return ResponseEntity.ok(inscritoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um morador inscrito por ID")
    public ResponseEntity<InscritoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inscritoService.buscarPorId(id));
    }

    @GetMapping("/regiao/{idRegiao}/ativos")
    @Operation(summary = "Lista os moradores ativos vinculados a uma região específica")
    public ResponseEntity<List<InscritoResponse>> listarAtivosPorRegiao(@PathVariable Long idRegiao) {
        return ResponseEntity.ok(inscritoService.listarAtivosPorRegiao(idRegiao));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados cadastrais de um morador inscrito")
    public ResponseEntity<InscritoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarInscritoRequest request
    ) {
        return ResponseEntity.ok(inscritoService.atualizar(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancela (inativa com st_ativo='N') a inscrição de um morador")
    public ResponseEntity<InscritoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(inscritoService.cancelar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove fisicamente o cadastro de um morador")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        inscritoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

