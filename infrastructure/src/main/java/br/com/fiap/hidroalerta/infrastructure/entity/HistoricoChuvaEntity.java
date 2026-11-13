package br.com.fiap.hidroalerta.infrastructure.entity;

import br.com.fiap.hidroalerta.domain.model.IntensidadeChuva;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_historico_chuva")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoChuvaEntity {

    @Id
    @Column(name = "id_chuva", nullable = false)
    private Long idChuva;

    @ManyToOne
    @JoinColumn(name = "id_regiao", nullable = false)
    private RegiaoEntity regiao;

    @Column(name = "vl_precipitacao_mm", nullable = false, precision = 6, scale = 2)
    private BigDecimal precipitacaoMm;

    @Enumerated(EnumType.STRING)
    @Column(name = "ds_intensidade", nullable = false, length = 20)
    private IntensidadeChuva intensidade;

    @Column(name = "dt_medicao", nullable = false)
    private LocalDateTime dataMedicao;
}
