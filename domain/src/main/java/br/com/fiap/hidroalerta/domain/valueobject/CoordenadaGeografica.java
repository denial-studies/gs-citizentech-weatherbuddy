package br.com.fiap.hidroalerta.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public record CoordenadaGeografica(BigDecimal latitude, BigDecimal longitude) {

    private static final BigDecimal LAT_MIN = new BigDecimal("-90.000000");
    private static final BigDecimal LAT_MAX = new BigDecimal("90.000000");
    private static final BigDecimal LON_MIN = new BigDecimal("-180.000000");
    private static final BigDecimal LON_MAX = new BigDecimal("180.000000");

    public CoordenadaGeografica {
        Objects.requireNonNull(latitude, "A latitude é obrigatória.");
        Objects.requireNonNull(longitude, "A longitude é obrigatória.");
        if (latitude.compareTo(LAT_MIN) < 0 || latitude.compareTo(LAT_MAX) > 0) {
            throw new IllegalArgumentException("Latitude deve estar entre -90 e 90 graus.");
        }
        if (longitude.compareTo(LON_MIN) < 0 || longitude.compareTo(LON_MAX) > 0) {
            throw new IllegalArgumentException("Longitude deve estar entre -180 e 180 graus.");
        }
    }

    public String formatada() {
        return String.format("%s, %s", latitude.toPlainString(), longitude.toPlainString());
    }
}

