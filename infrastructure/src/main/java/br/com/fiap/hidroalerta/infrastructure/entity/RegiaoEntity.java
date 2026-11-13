package br.com.fiap.hidroalerta.infrastructure.entity;

import br.com.fiap.hidroalerta.domain.model.NivelRisco;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_regiao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegiaoEntity {

    @Id
    @Column(name = "id_regiao", nullable = false, precision = 10)
    private Long idRegiao;

    @Column(name = "nm_regiao", nullable = false, length = 100)
    private String nomeRegiao;

    @Column(name = "ds_bairro", nullable = false, length = 100)
    private String bairro;

    @Column(name = "nr_latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "nr_longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "vl_limite_chuva_mm", nullable = false, precision = 5, scale = 2)
    private BigDecimal limiteChuvaMm;

    @Enumerated(EnumType.STRING)
    @Column(name = "ds_nivel_risco", nullable = false, length = 20)
    private NivelRisco nivelRisco;
}
