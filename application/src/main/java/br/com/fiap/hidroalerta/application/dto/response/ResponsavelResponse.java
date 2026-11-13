package br.com.fiap.hidroalerta.application.dto.response;

import java.time.LocalDate;

public record ResponsavelResponse(
        Long idResponsavel,
        String nomeResponsavel,
        String emailLogin,
        String cargo,
        String ativo,
        LocalDate dataCadastro
) {
}

