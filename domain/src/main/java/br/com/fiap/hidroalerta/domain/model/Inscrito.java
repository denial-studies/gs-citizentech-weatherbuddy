package br.com.fiap.hidroalerta.domain.model;

import br.com.fiap.hidroalerta.domain.valueobject.TelefoneContato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inscrito {

    private Long idInscrito;
    private Long idRegiao;
    private String nomeRegiao;
    private String bairroRegiao;
    private String nomeInscrito;
    private String telefone;
    private String ativo;
    private LocalDate dataInscricao;

    public boolean isAtivo() {
        return "S".equalsIgnoreCase(this.ativo);
    }

    public void ativarInscricao() {
        this.ativo = "S";
        if (this.dataInscricao == null) {
            this.dataInscricao = LocalDate.now();
        }
    }

    public void cancelarInscricao() {
        this.ativo = "N";
    }

    public TelefoneContato obterTelefoneContato() {
        return new TelefoneContato(telefone);
    }
}

