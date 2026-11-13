package br.com.fiap.hidroalerta.application.port;

public interface NotificacaoService {

    boolean enviarAlerta(String telefoneDestino, String nomeDestinatario, String nomeRegiao, String conteudoMensagem);
}

