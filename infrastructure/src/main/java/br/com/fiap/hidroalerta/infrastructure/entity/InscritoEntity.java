package br.com.fiap.hidroalerta.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tb_inscrito")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscritoEntity {

    @Id
    @Column(name = "id_inscrito", nullable = false)
    private Long idInscrito;

    @ManyToOne
    @JoinColumn(name = "id_regiao", nullable = false)
    private RegiaoEntity regiao;

    @Column(name = "nm_inscrito", nullable = false, length = 120)
    private String nomeInscrito;

    @Column(name = "nr_telefone", nullable = false, unique = true, length = 20)
    private String telefone;

    @Column(name = "st_ativo", nullable = false, length = 1)
    private String ativo;

    @Column(name = "dt_inscricao", nullable = false)
    private LocalDate dataInscricao;
}
