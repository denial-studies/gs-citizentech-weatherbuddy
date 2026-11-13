package br.com.fiap.hidroalerta.domain.model;

import br.com.fiap.hidroalerta.domain.valueobject.CoordenadaGeografica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RegiaoTest {

    @Test
    @DisplayName("Deve ultrapassar limite de chuva quando precipitação for maior ou igual ao limiar")
    void deveUltrapassarLimiteDeChuva() {
        Regiao regiao = Regiao.builder()
                .idRegiao(1L)
                .nomeRegiao("Jardim Pantanal")
                .bairro("São Miguel Paulista")
                .latitude(new BigDecimal("-23.485600"))
                .longitude(new BigDecimal("-46.418200"))
                .limiteChuvaMm(new BigDecimal("25.00"))
                .nivelRisco(NivelRisco.CRITICO)
                .build();

        assertTrue(regiao.ultrapassouLimiteSeguranca(new BigDecimal("25.00")));
        assertTrue(regiao.ultrapassouLimiteSeguranca(new BigDecimal("35.50")));
        assertFalse(regiao.ultrapassouLimiteSeguranca(new BigDecimal("24.99")));
        assertFalse(regiao.ultrapassouLimiteSeguranca(null));
    }

    @Test
    @DisplayName("Deve determinar corretamente a severidade do alerta")
    void deveDeterminarSeveridade() {
        Regiao regiao = Regiao.builder()
                .limiteChuvaMm(new BigDecimal("25.00"))
                .nivelRisco(NivelRisco.ALTO)
                .build();

        assertEquals(SeveridadeAlerta.ATENCAO, regiao.determinarSeveridade(new BigDecimal("10.00")));
        assertEquals(SeveridadeAlerta.ALERTA, regiao.determinarSeveridade(new BigDecimal("25.00")));
        assertEquals(SeveridadeAlerta.ALERTA, regiao.determinarSeveridade(new BigDecimal("49.90")));
        assertEquals(SeveridadeAlerta.EMERGENCIA, regiao.determinarSeveridade(new BigDecimal("50.00")));
        assertEquals(SeveridadeAlerta.EMERGENCIA, regiao.determinarSeveridade(new BigDecimal("75.00")));
    }

    @Test
    @DisplayName("Deve validar coordenadas no Value Object CoordenadaGeografica")
    void deveValidarCoordenadas() {
        CoordenadaGeografica coordValida = new CoordenadaGeografica(
                new BigDecimal("-23.500000"),
                new BigDecimal("-46.500000")
        );
        assertNotNull(coordValida.formatada());

        assertThrows(IllegalArgumentException.class, () ->
                new CoordenadaGeografica(new BigDecimal("91.000000"), new BigDecimal("0.0"))
        );

        assertThrows(IllegalArgumentException.class, () ->
                new CoordenadaGeografica(new BigDecimal("0.0"), new BigDecimal("181.000000"))
        );
    }
}

