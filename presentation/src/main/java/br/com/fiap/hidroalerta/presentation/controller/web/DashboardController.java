package br.com.fiap.hidroalerta.presentation.controller.web;

import br.com.fiap.hidroalerta.application.dto.request.AtualizarInscritoRequest;
import br.com.fiap.hidroalerta.application.dto.request.CriarInscritoRequest;
import br.com.fiap.hidroalerta.application.dto.request.RegiaoRequest;
import br.com.fiap.hidroalerta.application.dto.response.AlertaPendenteResponse;
import br.com.fiap.hidroalerta.application.dto.response.ResultadoMonitoramentoResponse;
import br.com.fiap.hidroalerta.application.port.WhatsappSessionPort;
import br.com.fiap.hidroalerta.application.service.HistoricoService;
import br.com.fiap.hidroalerta.application.service.InscritoService;
import br.com.fiap.hidroalerta.application.service.MonitoramentoService;
import br.com.fiap.hidroalerta.application.service.RegiaoService;
import br.com.fiap.hidroalerta.domain.model.NivelRisco;
import br.com.fiap.hidroalerta.domain.model.Responsavel;
import br.com.fiap.hidroalerta.domain.model.SeveridadeAlerta;
import br.com.fiap.hidroalerta.domain.repository.ResponsavelRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final RegiaoService regiaoService;
    private final InscritoService inscritoService;
    private final HistoricoService historicoService;
    private final MonitoramentoService monitoramentoService;
    private final ResponsavelRepository responsavelRepository;
    private final PasswordEncoder passwordEncoder;
    private final WhatsappSessionPort whatsappSessionPort;

    // =========================================================================
    // TELA 1: MONITORAMENTO CLIMÁTICO E REGIÕES (/dashboard)
    // =========================================================================

    @GetMapping
    public String exibirDashboard(Model model, Authentication authentication) {
        try {
            List<AlertaPendenteResponse> pendentes = monitoramentoService.listarAlertasPendentesConfirmacao();
            model.addAttribute("totalRegioes", regiaoService.contarTotal());
            model.addAttribute("totalInscritos", inscritoService.contarAtivos());
            model.addAttribute("totalAlertas", historicoService.contarTotalAlertasEmitidos());
            model.addAttribute("totalPendentes", pendentes.size());
            model.addAttribute("alertasPendentes", pendentes);
            model.addAttribute("regioes", regiaoService.listarTodas());
            model.addAttribute("historicoChuvas", historicoService.listarHistoricoChuvas());
        } catch (Exception ex) {
            log.warn("Erro ao carregar dados do dashboard: {}", ex.getMessage());
            model.addAttribute("totalRegioes", 0L);
            model.addAttribute("totalInscritos", 0L);
            model.addAttribute("totalAlertas", 0L);
            model.addAttribute("totalPendentes", 0);
            model.addAttribute("alertasPendentes", Collections.emptyList());
            model.addAttribute("regioes", Collections.emptyList());
            model.addAttribute("historicoChuvas", Collections.emptyList());
            model.addAttribute("erroPainel", "Não foi possível carregar alguns dados: " + ex.getMessage());
        }

        if (!model.containsAttribute("novaRegiaoForm")) {
            model.addAttribute("novaRegiaoForm", new RegiaoRequest(
                    "", "", new BigDecimal("-23.490000"), new BigDecimal("-46.410000"), new BigDecimal("25.00"), NivelRisco.ALTO
            ));
        }
        model.addAttribute("niveisRisco", NivelRisco.values());
        model.addAttribute("severidades", SeveridadeAlerta.values());
        model.addAttribute("abaAtiva", "monitoramento");
        model.addAttribute("usuarioLogado", authentication != null ? authentication.getName() : "Operador");
        return "dashboard";
    }

    @PostMapping("/enviar-regiao/{idRegiao}")
    public String enviarParaTodosDaRegiao(
            @PathVariable Long idRegiao,
            @RequestParam(required = false) SeveridadeAlerta severidade,
            @RequestParam(required = false) String mensagemCustomizada,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Long idResponsavel = obterIdResponsavelLogado(authentication);
            ResultadoMonitoramentoResponse resultado = monitoramentoService.confirmarEnvioAlerta(
                    idRegiao, idResponsavel, severidade, mensagemCustomizada
            );
            redirectAttributes.addFlashAttribute("sucessoPainel", resultado.mensagemResumo());
        } catch (Exception ex) {
            log.error("Erro ao enviar alerta para região {}: {}", idRegiao, ex.getMessage());
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao enviar alerta para a região: " + ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/monitorar/{idRegiao}")
    public String verificarClimaAgora(
            @PathVariable Long idRegiao,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Long idResponsavel = obterIdResponsavelLogado(authentication);
            ResultadoMonitoramentoResponse resultado =
                    monitoramentoService.verificarClimaRegiao(idRegiao, idResponsavel);

            if (resultado.alertaDisparado()) {
                redirectAttributes.addFlashAttribute("alertaPainel", resultado.mensagemResumo());
            } else {
                redirectAttributes.addFlashAttribute("sucessoPainel", resultado.mensagemResumo());
            }
        } catch (Exception ex) {
            log.error("Falha ao verificar clima da região {}: {}", idRegiao, ex.getMessage());
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao consultar clima da região: " + ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/simular/{idRegiao}")
    public String simularClimaRuim(
            @PathVariable Long idRegiao,
            @RequestParam(required = false) BigDecimal precipitacaoMm,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ResultadoMonitoramentoResponse resultado =
                    monitoramentoService.simularClimaRuimRegiao(idRegiao, precipitacaoMm);
            redirectAttributes.addFlashAttribute("alertaPainel", resultado.mensagemResumo());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao simular leitura de chuva: " + ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/regioes")
    public String cadastrarNovaRegiao(
            @Valid @ModelAttribute("novaRegiaoForm") RegiaoRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("erroPainel", "Verifique os campos obrigatórios para cadastrar a nova região.");
            return "redirect:/dashboard";
        }
        try {
            regiaoService.criar(request);
            redirectAttributes.addFlashAttribute("sucessoPainel", "Região '" + request.nomeRegiao() + "' cadastrada com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao salvar região: " + ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    // =========================================================================
    // TELA 2: GERENCIAR NÚMEROS CADASTRADOS (/dashboard/numeros)
    // =========================================================================

    @GetMapping("/numeros")
    public String exibirTelaNumeros(
            @RequestParam(required = false) Long editarId,
            Model model,
            Authentication authentication
    ) {
        model.addAttribute("inscritos", inscritoService.listarTodos());
        model.addAttribute("regioes", regiaoService.listarTodas());
        model.addAttribute("abaAtiva", "numeros");
        model.addAttribute("usuarioLogado", authentication != null ? authentication.getName() : "Operador");

        if (editarId != null) {
            try {
                model.addAttribute("inscritoEmEdicao", inscritoService.buscarPorId(editarId));
            } catch (Exception ex) {
                model.addAttribute("erroPainel", "Número/morador não encontrado para edição.");
            }
        }
        return "numeros";
    }

    @PostMapping("/numeros/criar")
    public String criarNumero(
            @RequestParam String nomeInscrito,
            @RequestParam String telefone,
            @RequestParam Long idRegiao,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inscritoService.inscrever(new CriarInscritoRequest(nomeInscrito, telefone, idRegiao));
            redirectAttributes.addFlashAttribute("sucessoPainel", "Número " + telefone + " (" + nomeInscrito + ") cadastrado com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao cadastrar número: " + ex.getMessage());
        }
        return "redirect:/dashboard/numeros";
    }

    @PostMapping("/numeros/{id}/editar")
    public String atualizarNumero(
            @PathVariable Long id,
            @RequestParam String nomeInscrito,
            @RequestParam String telefone,
            @RequestParam Long idRegiao,
            @RequestParam String ativo,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inscritoService.atualizar(id, new AtualizarInscritoRequest(nomeInscrito, telefone, idRegiao, ativo));
            redirectAttributes.addFlashAttribute("sucessoPainel", "Cadastro do número de " + nomeInscrito + " atualizado com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao alterar número: " + ex.getMessage());
            return "redirect:/dashboard/numeros?editarId=" + id;
        }
        return "redirect:/dashboard/numeros";
    }

    @PostMapping("/numeros/{id}/status")
    public String alternarStatusNumero(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var atualizado = inscritoService.alternarStatus(id);
            String descStatus = "S".equalsIgnoreCase(atualizado.ativo()) ? "ATIVADO" : "INATIVADO";
            redirectAttributes.addFlashAttribute("sucessoPainel", "Número " + atualizado.telefone() + " foi " + descStatus + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao alterar status: " + ex.getMessage());
        }
        return "redirect:/dashboard/numeros";
    }

    @PostMapping("/numeros/{id}/deletar")
    public String deletarNumero(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inscritoService.deletar(id);
            redirectAttributes.addFlashAttribute("sucessoPainel", "Número removido do sistema com sucesso.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao remover número: " + ex.getMessage());
        }
        return "redirect:/dashboard/numeros";
    }

    // =========================================================================
    // TELA 3: GERENCIAR CONTAS DO SISTEMA (/dashboard/contas)
    // =========================================================================

    @GetMapping("/contas")
    public String exibirTelaContas(
            @RequestParam(required = false) Long editarId,
            Model model,
            Authentication authentication
    ) {
        model.addAttribute("contas", responsavelRepository.listarTodos());
        model.addAttribute("abaAtiva", "contas");
        model.addAttribute("usuarioLogado", authentication != null ? authentication.getName() : "Operador");

        if (editarId != null) {
            responsavelRepository.buscarPorId(editarId).ifPresentOrElse(
                    conta -> model.addAttribute("contaEmEdicao", conta),
                    () -> model.addAttribute("erroPainel", "Conta não encontrada para edição.")
            );
        }
        return "contas";
    }

    @PostMapping("/contas/criar")
    public String criarConta(
            @RequestParam String nomeResponsavel,
            @RequestParam String emailLogin,
            @RequestParam String cargo,
            @RequestParam String senha,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String emailNormalizado = emailLogin.trim().toLowerCase();
            if (responsavelRepository.buscarPorEmailLogin(emailNormalizado).isPresent()) {
                redirectAttributes.addFlashAttribute("erroPainel", "Já existe uma conta cadastrada com o e-mail: " + emailNormalizado);
                return "redirect:/dashboard/contas";
            }

            Responsavel novaConta = Responsavel.builder()
                    .nomeResponsavel(nomeResponsavel.trim())
                    .emailLogin(emailNormalizado)
                    .cargo(cargo.trim())
                    .senhaHash(passwordEncoder.encode(senha))
                    .ativo("S")
                    .dataCadastro(LocalDate.now())
                    .build();

            responsavelRepository.salvar(novaConta);
            redirectAttributes.addFlashAttribute("sucessoPainel", "Conta '" + emailNormalizado + "' cadastrada com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao cadastrar conta: " + ex.getMessage());
        }
        return "redirect:/dashboard/contas";
    }

    @PostMapping("/contas/{id}/editar")
    public String editarConta(
            @PathVariable Long id,
            @RequestParam String nomeResponsavel,
            @RequestParam String emailLogin,
            @RequestParam String cargo,
            @RequestParam String ativo,
            @RequestParam(required = false) String novaSenha,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Responsavel existente = responsavelRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

            String emailNormalizado = emailLogin.trim().toLowerCase();
            responsavelRepository.buscarPorEmailLogin(emailNormalizado).ifPresent(outro -> {
                if (!outro.getIdResponsavel().equals(id)) {
                    throw new IllegalArgumentException("O e-mail informado já está em uso por outra conta.");
                }
            });

            existente.setNomeResponsavel(nomeResponsavel.trim());
            existente.setEmailLogin(emailNormalizado);
            existente.setCargo(cargo.trim());
            existente.setAtivo("S".equalsIgnoreCase(ativo) ? "S" : "N");

            if (novaSenha != null && !novaSenha.isBlank()) {
                existente.setSenhaHash(passwordEncoder.encode(novaSenha.trim()));
            }

            responsavelRepository.salvar(existente);
            redirectAttributes.addFlashAttribute("sucessoPainel", "Conta '" + emailNormalizado + "' alterada com sucesso!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao alterar conta: " + ex.getMessage());
            return "redirect:/dashboard/contas?editarId=" + id;
        }
        return "redirect:/dashboard/contas";
    }

    @PostMapping("/contas/{id}/status")
    public String alternarStatusConta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Responsavel conta = responsavelRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));
            conta.setAtivo(conta.isAtivo() ? "N" : "S");
            responsavelRepository.salvar(conta);
            redirectAttributes.addFlashAttribute("sucessoPainel",
                    "Status da conta '" + conta.getEmailLogin() + "' alterado para " + (conta.isAtivo() ? "ATIVO" : "INATIVO") + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao alterar status da conta: " + ex.getMessage());
        }
        return "redirect:/dashboard/contas";
    }

    @PostMapping("/contas/{id}/deletar")
    public String deletarConta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            responsavelRepository.deletarPorId(id);
            redirectAttributes.addFlashAttribute("sucessoPainel", "Conta removida com sucesso.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Não foi possível excluir a conta (ela pode possuir histórico de alertas vinculado). Inative-a se preferir.");
        }
        return "redirect:/dashboard/contas";
    }

    // =========================================================================
    // TELA 4: TESTAR ENVIOS E CONFIRMAR ALERTAS (/dashboard/envios)
    // =========================================================================

    @GetMapping("/envios")
    public String exibirTelaEnvios(
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long idRegiao,
            Model model,
            Authentication authentication
    ) {
        model.addAttribute("alertasPendentes", monitoramentoService.listarAlertasPendentesConfirmacao());
        model.addAttribute("regioes", regiaoService.listarTodas());
        model.addAttribute("inscritos", inscritoService.listarTodos());
        model.addAttribute("historicoMensagens", historicoService.listarHistoricoMensagens());
        model.addAttribute("severidades", SeveridadeAlerta.values());
        model.addAttribute("telefoneTeste", telefone != null ? telefone : "");
        model.addAttribute("nomeTeste", nome != null ? nome : "");
        model.addAttribute("idRegiaoTeste", idRegiao);
        model.addAttribute("whatsappStatus", whatsappSessionPort.getStatus());
        model.addAttribute("whatsappConectado", whatsappSessionPort.isConectado());
        model.addAttribute("whatsappNumero", whatsappSessionPort.getNumeroConectado());
        model.addAttribute("whatsappCodigoPareamento", whatsappSessionPort.getCodigoPareamento());
        model.addAttribute("whatsappTemQr", whatsappSessionPort.temQrCodeDisponivel());
        model.addAttribute("whatsappErro", whatsappSessionPort.getUltimoErro());
        model.addAttribute("abaAtiva", "envios");
        model.addAttribute("usuarioLogado", authentication != null ? authentication.getName() : "Operador");
        return "envios";
    }

    @PostMapping("/envios/whatsapp/conectar")
    public String conectarWhatsapp(
            @RequestParam String telefoneWhatsapp,
            @RequestParam(defaultValue = "PAIRING") String modo,
            RedirectAttributes redirectAttributes
    ) {
        try {
            boolean usarQr = "QR".equalsIgnoreCase(modo);
            whatsappSessionPort.iniciarConexaoComNumero(telefoneWhatsapp, usarQr);
            redirectAttributes.addFlashAttribute(
                    "alertaPainel",
                    usarQr
                            ? "Gerando QR Code temporário em memória para o número " + telefoneWhatsapp + "..."
                            : "Gerando código de pareamento para o número " + telefoneWhatsapp + "... Aguarde alguns segundos."
            );
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao iniciar conexão do WhatsApp: " + ex.getMessage());
        }
        return "redirect:/dashboard/envios";
    }

    @PostMapping("/envios/whatsapp/desconectar")
    public String desconectarWhatsapp(RedirectAttributes redirectAttributes) {
        whatsappSessionPort.desconectar();
        redirectAttributes.addFlashAttribute("sucessoPainel", "WhatsApp desconectado.");
        return "redirect:/dashboard/envios";
    }

    @PostMapping("/envios/whatsapp/limpar")
    public String limparSessaoWhatsapp(RedirectAttributes redirectAttributes) {
        whatsappSessionPort.limparSessao();
        redirectAttributes.addFlashAttribute("sucessoPainel", "Sessão salva do WhatsApp removida. Você já pode parear um novo número.");
        return "redirect:/dashboard/envios";
    }

    @GetMapping("/envios/whatsapp/qr")
    @ResponseBody
    public ResponseEntity<byte[]> obterQrCodeWhatsapp() {
        byte[] qr = whatsappSessionPort.obterQrCodePng();
        if (qr == null || qr.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.IMAGE_PNG)
                .body(qr);
    }

    @PostMapping("/envios/confirmar/{idRegiao}")
    public String confirmarEnvioRegiao(
            @PathVariable Long idRegiao,
            @RequestParam(required = false) SeveridadeAlerta severidade,
            @RequestParam(required = false) String mensagemCustomizada,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Long idResponsavel = obterIdResponsavelLogado(authentication);
            ResultadoMonitoramentoResponse resultado = monitoramentoService.confirmarEnvioAlerta(
                    idRegiao, idResponsavel, severidade, mensagemCustomizada
            );
            redirectAttributes.addFlashAttribute("sucessoPainel", resultado.mensagemResumo());
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Erro ao confirmar envio de alerta: " + ex.getMessage());
        }
        return "redirect:/dashboard/envios";
    }

    @PostMapping("/envios/testar-numero")
    public String testarEnvioNumeroEspecifico(
            @RequestParam String telefone,
            @RequestParam(required = false) String nomeDestinatario,
            @RequestParam(required = false) Long idRegiao,
            @RequestParam(required = false) String mensagem,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String comprovante = monitoramentoService.testarEnvioNumeroEspecifico(
                    telefone, nomeDestinatario, idRegiao, mensagem
            );
            redirectAttributes.addFlashAttribute("sucessoPainel", comprovante);
            redirectAttributes.addFlashAttribute("ultimoTesteNumero", telefone);
            redirectAttributes.addFlashAttribute("ultimoTesteNome", nomeDestinatario != null && !nomeDestinatario.isBlank() ? nomeDestinatario : "Morador de Teste");
            redirectAttributes.addFlashAttribute("ultimoTesteMensagem", mensagem != null && !mensagem.isBlank() ? mensagem : "[TESTE WEATHER BUDDY] Mensagem de verificação do sistema de alerta preventivo.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("erroPainel", "Falha no teste de envio: " + ex.getMessage());
        }
        return "redirect:/dashboard/envios";
    }

    private Long obterIdResponsavelLogado(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        return responsavelRepository.buscarPorEmailLogin(authentication.getName())
                .map(Responsavel::getIdResponsavel)
                .orElse(null);
    }
}
