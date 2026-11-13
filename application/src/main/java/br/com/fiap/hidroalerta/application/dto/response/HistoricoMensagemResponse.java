package br.com.fiap.hidroalerta.application.dto.response;

import br.com.fiap.hidroalerta.domain.model.SeveridadeAlerta;

import java.time.LocalDateTime;

public record HistoricoMensagemResponse(
        Long idMensagem,
        Long idRegiao,
        String nomeRegiao,
        String bairroRegiao,
        Long idResponsavel,
        String nomeResponsavel,
        SeveridadeAlerta severidade,
        String conteudo,
        Integer quantidadeEnviosSucesso,
        LocalDateTime dataEnvio
) {
}

