package br.com.fiap.hidroalerta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMensagem {

    private Long idMensagem;
    private Long idRegiao;
    private String nomeRegiao;
    private String bairroRegiao;
    private Long idResponsavel;
    private String nomeResponsavel;
    private SeveridadeAlerta severidade;
    private String conteudo;
    private Integer quantidadeEnviosSucesso;
    private LocalDateTime dataEnvio;
}

