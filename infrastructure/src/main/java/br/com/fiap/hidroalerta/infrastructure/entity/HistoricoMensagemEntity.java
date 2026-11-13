package br.com.fiap.hidroalerta.infrastructure.entity;

import br.com.fiap.hidroalerta.domain.model.SeveridadeAlerta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_mensagem")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMensagemEntity {

    @Id
    @Column(name = "id_mensagem", nullable = false)
    private Long idMensagem;

    @ManyToOne
    @JoinColumn(name = "id_regiao", nullable = false)
    private RegiaoEntity regiao;

    @ManyToOne
    @JoinColumn(name = "id_responsavel", nullable = false)
    private ResponsavelEntity responsavel;

    @Enumerated(EnumType.STRING)
    @Column(name = "tp_severidade", nullable = false, length = 20)
    private SeveridadeAlerta severidade;

    @Column(name = "ds_conteudo", nullable = false, length = 500)
    private String conteudo;

    @Column(name = "qt_envios_sucesso", nullable = false)
    private Integer quantidadeEnviosSucesso;

    @Column(name = "dt_envio", nullable = false)
    private LocalDateTime dataEnvio;
}
