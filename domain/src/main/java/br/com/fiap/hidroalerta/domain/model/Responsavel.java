package br.com.fiap.hidroalerta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Responsavel {

    private Long idResponsavel;
    private String nomeResponsavel;
    private String emailLogin;
    private String senhaHash;
    private String cargo;
    private String ativo;
    private LocalDate dataCadastro;

    public boolean isAtivo() {
        return "S".equalsIgnoreCase(this.ativo);
    }

    public void ativar() {
        this.ativo = "S";
    }

    public void inativar() {
        this.ativo = "N";
    }
}

