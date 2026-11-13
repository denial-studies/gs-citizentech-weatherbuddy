package br.com.fiap.hidroalerta.presentation.controller.rest;

import br.com.fiap.hidroalerta.application.dto.request.CriarResponsavelRequest;
import br.com.fiap.hidroalerta.application.dto.request.LoginRequest;
import br.com.fiap.hidroalerta.application.dto.response.ResponsavelResponse;
import br.com.fiap.hidroalerta.application.exception.RecursoNaoEncontradoException;
import br.com.fiap.hidroalerta.application.exception.RegraNegocioException;
import br.com.fiap.hidroalerta.domain.model.Responsavel;
import br.com.fiap.hidroalerta.domain.repository.ResponsavelRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação e Responsáveis", description = "Autenticação de operadores comunitários e gestão de responsáveis")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final ResponsavelRepository responsavelRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @PostMapping("/login")
    @Operation(summary = "Autentica um responsável comunitário com e-mail e senha e cria sessão HTTP")
    public ResponseEntity<Map<String, Object>> autenticar(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            Responsavel responsavel = responsavelRepository.buscarPorEmailLogin(auth.getName())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado."));

            return ResponseEntity.ok(Map.of(
                    "autenticado", true,
                    "idResponsavel", responsavel.getIdResponsavel(),
                    "nome", responsavel.getNomeResponsavel(),
                    "email", responsavel.getEmailLogin(),
                    "cargo", responsavel.getCargo()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "autenticado", false,
                    "mensagem", "Credenciais inválidas: verifique o e-mail e a senha."));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Retorna os dados do responsável autenticado na sessão atual")
    public ResponseEntity<ResponsavelResponse> usuarioAtual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RegraNegocioException("Nenhum responsável autenticado na sessão atual.");
        }
        Responsavel responsavel = responsavelRepository.buscarPorEmailLogin(authentication.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Responsável não encontrado."));
        return ResponseEntity.ok(toResponse(responsavel));
    }

    @PostMapping("/responsaveis")
    @Operation(summary = "Cadastra um novo responsável comunitário com senha criptografada em BCrypt")
    public ResponseEntity<ResponsavelResponse> cadastrarResponsavel(
            @Valid @RequestBody CriarResponsavelRequest request) {
        responsavelRepository.buscarPorEmailLogin(request.emailLogin()).ifPresent(r -> {
            throw new RegraNegocioException("Já existe um responsável cadastrado com o e-mail informado.");
        });

        Responsavel novo = Responsavel.builder()
                .nomeResponsavel(request.nomeResponsavel().trim())
                .emailLogin(request.emailLogin().trim().toLowerCase())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .cargo(request.cargo().trim())
                .ativo("S")
                .dataCadastro(LocalDate.now())
                .build();

        Responsavel salvo = responsavelRepository.salvar(novo);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvo));
    }

    @GetMapping("/responsaveis")
    @Operation(summary = "Lista todos os responsáveis comunitários cadastrados")
    public ResponseEntity<List<ResponsavelResponse>> listarResponsaveis() {
        List<ResponsavelResponse> lista = responsavelRepository.listarTodos().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(lista);
    }

    private ResponsavelResponse toResponse(Responsavel r) {
        return new ResponsavelResponse(
                r.getIdResponsavel(),
                r.getNomeResponsavel(),
                r.getEmailLogin(),
                r.getCargo(),
                r.getAtivo(),
                r.getDataCadastro());
    }
}
