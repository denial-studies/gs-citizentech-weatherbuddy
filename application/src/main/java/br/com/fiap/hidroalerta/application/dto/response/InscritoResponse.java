package br.com.fiap.hidroalerta.application.dto.response;

import java.time.LocalDate;

public record InscritoResponse(
        Long idInscrito,
        Long idRegiao,
        String nomeRegiao,
        String bairroRegiao,
        String nomeInscrito,
        String telefone,
        String ativo,
        LocalDate dataInscricao
) {
}

