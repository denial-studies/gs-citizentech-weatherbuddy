package br.com.fiap.hidroalerta.infrastructure.messaging;

import br.com.fiap.hidroalerta.application.port.NotificacaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class ConsoleNotificacaoServiceImpl implements NotificacaoService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public boolean enviarAlerta(String telefoneDestino, String nomeDestinatario, String nomeRegiao, String conteudoMensagem) {
        String horario = LocalDateTime.now().format(FORMATTER);
        log.info("""
                
                ========================================================================
                [WEATHER BUDDY | DESPACHO DE MENSAGEM PREVENTIVA - TIME HIDRO-ALERTA]
                Data/Hora   : {}
                Região      : {}
                Morador     : {}
                Telefone    : {}
                Mensagem    : {}
                Status      : ENTREGUE COM SUCESSO (SIMULAÇÃO MENSAGERIA)
                ========================================================================
                """, horario, nomeRegiao, nomeDestinatario, telefoneDestino, conteudoMensagem);
        return true;
    }
}

