package br.com.fiap.hidroalerta.infrastructure.client;

import br.com.fiap.hidroalerta.application.port.ClimaTelemetryPort;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
public class OpenMeteoClient implements ClimaTelemetryPort {

    private static final String BASE_URL = "https://api.open-meteo.com/v1";

    private final RestClient restClient;

    public OpenMeteoClient() {
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    @Override
    public BigDecimal obterPrecipitacaoAtualMm(BigDecimal latitude, BigDecimal longitude) {
        try {
            log.info("[Open-Meteo] Consultando telemetria climática em latitude={}, longitude={}", latitude, longitude);

            OpenMeteoResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current", "precipitation,rain")
                            .build())
                    .retrieve()
                    .body(OpenMeteoResponse.class);

            if (response != null && response.current() != null) {
                BigDecimal precipitation = response.current().precipitation();
                BigDecimal rain = response.current().rain();
                BigDecimal medida = precipitation != null ? precipitation : (rain != null ? rain : BigDecimal.ZERO);
                return medida.setScale(2, RoundingMode.HALF_UP);
            }
        } catch (Exception ex) {
            log.warn("[Open-Meteo] Falha ao consultar API externa ({}). Retornando 0.00 mm por segurança.", ex.getMessage());
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OpenMeteoResponse(
            BigDecimal latitude,
            BigDecimal longitude,
            CurrentWeather current
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentWeather(
            String time,
            BigDecimal precipitation,
            BigDecimal rain
    ) {
    }
}

