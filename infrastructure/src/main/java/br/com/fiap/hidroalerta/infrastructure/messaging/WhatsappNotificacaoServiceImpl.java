package br.com.fiap.hidroalerta.infrastructure.messaging;

import br.com.fiap.hidroalerta.application.port.NotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class WhatsappNotificacaoServiceImpl implements NotificacaoService {

    private final CobaltWhatsappManager whatsappManager;
    private final ConsoleNotificacaoServiceImpl consoleFallback;

    @Override
    public boolean enviarAlerta(String telefoneDestino, String nomeDestinatario, String nomeRegiao, String conteudoMensagem) {
        if (!whatsappManager.isConectado()) {
            log.info("WhatsApp não conectado. Usando simulação em console para {}", telefoneDestino);
            return consoleFallback.enviarAlerta(telefoneDestino, nomeDestinatario, nomeRegiao, conteudoMensagem);
        }

        String mensagemFormatada = String.format(
                "*[WEATHER BUDDY | ALERTA %s]*%nOlá, %s!%n%n%s",
                nomeRegiao,
                nomeDestinatario != null && !nomeDestinatario.isBlank() ? nomeDestinatario : "Morador",
                conteudoMensagem
        );

        try {
            return whatsappManager.enviarMensagemTexto(telefoneDestino, mensagemFormatada);
        } catch (Exception ex) {
            log.error("Falha ao enviar WhatsApp via Cobalt para {}: {}", telefoneDestino, ex.getMessage(), ex);
            return false;
        }
    }
}

