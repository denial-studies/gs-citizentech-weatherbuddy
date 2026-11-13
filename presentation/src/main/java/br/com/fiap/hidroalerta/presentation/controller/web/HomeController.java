package br.com.fiap.hidroalerta.presentation.controller.web;

import br.com.fiap.hidroalerta.application.dto.request.CriarInscritoRequest;
import br.com.fiap.hidroalerta.application.dto.response.RegiaoResponse;
import br.com.fiap.hidroalerta.application.service.InscritoService;
import br.com.fiap.hidroalerta.application.service.RegiaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RegiaoService regiaoService;
    private final InscritoService inscritoService;

    @GetMapping("/login")
    public String exibirLogin() {
        return "login";
    }

    @GetMapping({"/", "/index"})
    public String exibirPaginaPublica(Model model) {
        if (!model.containsAttribute("inscritoForm")) {
            model.addAttribute("inscritoForm", new CriarInscritoRequest("", "", null));
        }
        model.addAttribute("regioes", carregarRegioesComSeguranca());
        return "index";
    }

    @PostMapping("/inscricao")
    public String realizarInscricao(
            @Valid @ModelAttribute("inscritoForm") CriarInscritoRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("regioes", carregarRegioesComSeguranca());
            model.addAttribute("erroMensagem", "Por favor, preencha corretamente todos os campos obrigatórios.");
            return "index";
        }

        try {
            inscritoService.inscrever(request);
            redirectAttributes.addFlashAttribute(
                    "sucessoMensagem",
                    "Cadastro realizado! Você receberá avisos quando houver risco de alagamento no seu bairro."
            );
            return "redirect:/";
        } catch (Exception ex) {
            log.warn("Falha ao cadastrar morador: {}", ex.getMessage());
            model.addAttribute("regioes", carregarRegioesComSeguranca());
            model.addAttribute("erroMensagem", ex.getMessage());
            return "index";
        }
    }

    private List<RegiaoResponse> carregarRegioesComSeguranca() {
        try {
            return regiaoService.listarTodas();
        } catch (Exception ex) {
            log.warn("Não foi possível consultar regiões no banco: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}
