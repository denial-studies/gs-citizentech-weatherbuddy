package br.com.fiap.hidroalerta.presentation.controller.rest;

import br.com.fiap.hidroalerta.application.dto.request.RegiaoRequest;
import br.com.fiap.hidroalerta.application.dto.response.RegiaoResponse;
import br.com.fiap.hidroalerta.application.service.RegiaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regioes")
@RequiredArgsConstructor
@Tag(name = "Regiões de Risco", description = "CRUD completo das regiões e bairros monitorados pelo Weather Buddy")
public class RegiaoRestController {

    private final RegiaoService regiaoService;

    @GetMapping
    @Operation(summary = "Lista todas as regiões monitoradas")
    public ResponseEntity<List<RegiaoResponse>> listarTodas() {
        return ResponseEntity.ok(regiaoService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma região monitorada por ID")
    public ResponseEntity<RegiaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(regiaoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma nova região de risco com coordenadas e limiar de chuva")
    public ResponseEntity<RegiaoResponse> criar(@Valid @RequestBody RegiaoRequest request) {
        RegiaoResponse criada = regiaoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados e limiares pluviométricos de uma região")
    public ResponseEntity<RegiaoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody RegiaoRequest request
    ) {
        return ResponseEntity.ok(regiaoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma região cadastrada")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        regiaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

