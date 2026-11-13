package br.com.fiap.hidroalerta.presentation.config;

import br.com.fiap.hidroalerta.domain.model.IntensidadeChuva;
import br.com.fiap.hidroalerta.domain.model.NivelRisco;
import br.com.fiap.hidroalerta.domain.model.SeveridadeAlerta;
import br.com.fiap.hidroalerta.infrastructure.entity.HistoricoChuvaEntity;
import br.com.fiap.hidroalerta.infrastructure.entity.HistoricoMensagemEntity;
import br.com.fiap.hidroalerta.infrastructure.entity.InscritoEntity;
import br.com.fiap.hidroalerta.infrastructure.entity.RegiaoEntity;
import br.com.fiap.hidroalerta.infrastructure.entity.ResponsavelEntity;
import br.com.fiap.hidroalerta.infrastructure.repository.HistoricoChuvaRepository;
import br.com.fiap.hidroalerta.infrastructure.repository.HistoricoMensagemRepository;
import br.com.fiap.hidroalerta.infrastructure.repository.InscritoRepository;
import br.com.fiap.hidroalerta.infrastructure.repository.RegiaoRepository;
import br.com.fiap.hidroalerta.infrastructure.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MockDataInitializer implements CommandLineRunner {

    private final RegiaoRepository regiaoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final InscritoRepository inscritoRepository;
    private final HistoricoChuvaRepository historicoChuvaRepository;
    private final HistoricoMensagemRepository historicoMensagemRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${db-env:false}")
    private String dbEnv;

    @Override
    public void run(String... args) {
        if (!isSeedEnabled(dbEnv)) {
            log.info("[MockDataInitializer] Seed desabilitado (db-env: '{}'). Para popular dados mockados, defina db-env: true, on ou 1.", dbEnv);
            return;
        }

        log.info("[MockDataInitializer] db-env ativo ('{}'). Verificando e populando dados mockados...", dbEnv);

        try {
            popularDados();
            log.info("[MockDataInitializer] Inicialização de dados mockados concluída com sucesso!");
        } catch (Exception ex) {
            log.warn("[MockDataInitializer] Não foi possível executar a carga de dados mockados (o banco pode estar inacessível ou sem tabelas): {}", ex.getMessage());
        }
    }

    private boolean isSeedEnabled(String valor) {
        if (valor == null) return false;
        String v = valor.trim().toLowerCase();
        return "true".equals(v) || "on".equals(v) || "1".equals(v) || "yes".equals(v) || "sim".equals(v);
    }

    private void popularDados() {
        // 1. Regiões de Risco
        if (regiaoRepository.count() == 0) {
            log.info("[MockDataInitializer] Inserindo regiões de risco mockadas...");
            RegiaoEntity r1 = RegiaoEntity.builder()
                    .idRegiao(1L)
                    .nomeRegiao("Jardim Pantanal")
                    .bairro("São Miguel Paulista - Zona Leste SP")
                    .latitude(new BigDecimal("-23.485600"))
                    .longitude(new BigDecimal("-46.418200"))
                    .limiteChuvaMm(new BigDecimal("25.00"))
                    .nivelRisco(NivelRisco.CRITICO)
                    .build();

            RegiaoEntity r2 = RegiaoEntity.builder()
                    .idRegiao(2L)
                    .nomeRegiao("Centro Franco da Rocha")
                    .bairro("Bacia do Rio Juquery - Franco da Rocha")
                    .latitude(new BigDecimal("-23.321700"))
                    .longitude(new BigDecimal("-46.726900"))
                    .limiteChuvaMm(new BigDecimal("30.00"))
                    .nivelRisco(NivelRisco.ALTO)
                    .build();

            RegiaoEntity r3 = RegiaoEntity.builder()
                    .idRegiao(3L)
                    .nomeRegiao("Várzea de Jandira")
                    .bairro("Centro - Jandira")
                    .latitude(new BigDecimal("-23.527500"))
                    .longitude(new BigDecimal("-46.902800"))
                    .limiteChuvaMm(new BigDecimal("28.00"))
                    .nivelRisco(NivelRisco.ALTO)
                    .build();

            RegiaoEntity r4 = RegiaoEntity.builder()
                    .idRegiao(4L)
                    .nomeRegiao("Jardim Belval")
                    .bairro("Barueri")
                    .latitude(new BigDecimal("-23.511400"))
                    .longitude(new BigDecimal("-46.876100"))
                    .limiteChuvaMm(new BigDecimal("35.00"))
                    .nivelRisco(NivelRisco.MODERADO)
                    .build();

            regiaoRepository.saveAll(List.of(r1, r2, r3, r4));
        }

        // 2. Responsáveis / Usuários de acesso ao sistema
        if (responsavelRepository.count() == 0) {
            log.info("[MockDataInitializer] Inserindo responsáveis comunitários mockados...");
            ResponsavelEntity resp1 = ResponsavelEntity.builder()
                    .idResponsavel(1L)
                    .nomeResponsavel("Operador Defesa Civil")
                    .emailLogin("operador@hidroalerta.com.br")
                    .senhaHash(passwordEncoder.encode("admin123"))
                    .cargo("Coordenador Comunitário")
                    .ativo("S")
                    .dataCadastro(LocalDate.now())
                    .build();

            ResponsavelEntity resp2 = ResponsavelEntity.builder()
                    .idResponsavel(2L)
                    .nomeResponsavel("Maria Coordenadora")
                    .emailLogin("maria@defesacivil.sp.gov.br")
                    .senhaHash(passwordEncoder.encode("admin123"))
                    .cargo("Agente de Campo")
                    .ativo("S")
                    .dataCadastro(LocalDate.now())
                    .build();

            responsavelRepository.saveAll(List.of(resp1, resp2));
        }

        RegiaoEntity regiaoPantanal = regiaoRepository.findById(1L).orElse(null);
        RegiaoEntity regiaoFranco = regiaoRepository.findById(2L).orElse(null);
        ResponsavelEntity respPadrao = responsavelRepository.findById(1L).orElse(null);

        // 3. Moradores Inscritos
        if (inscritoRepository.count() == 0 && regiaoPantanal != null) {
            log.info("[MockDataInitializer] Inserindo moradores inscritos mockados...");
            InscritoEntity i1 = InscritoEntity.builder()
                    .idInscrito(1L)
                    .regiao(regiaoPantanal)
                    .nomeInscrito("João da Silva")
                    .telefone("(11) 98765-4321")
                    .ativo("S")
                    .dataInscricao(LocalDate.now().minusDays(15))
                    .build();

            InscritoEntity i2 = InscritoEntity.builder()
                    .idInscrito(2L)
                    .regiao(regiaoPantanal)
                    .nomeInscrito("Ana Clara Souza")
                    .telefone("(11) 99876-5432")
                    .ativo("S")
                    .dataInscricao(LocalDate.now().minusDays(10))
                    .build();

            InscritoEntity i3 = InscritoEntity.builder()
                    .idInscrito(3L)
                    .regiao(regiaoFranco != null ? regiaoFranco : regiaoPantanal)
                    .nomeInscrito("Carlos Eduardo Santos")
                    .telefone("(11) 97654-3210")
                    .ativo("S")
                    .dataInscricao(LocalDate.now().minusDays(5))
                    .build();

            inscritoRepository.saveAll(List.of(i1, i2, i3));
        }

        // 4. Histórico de Chuvas
        if (historicoChuvaRepository.count() == 0 && regiaoPantanal != null) {
            log.info("[MockDataInitializer] Inserindo medições pluviométricas mockadas...");
            HistoricoChuvaEntity c1 = HistoricoChuvaEntity.builder()
                    .idChuva(1L)
                    .regiao(regiaoPantanal)
                    .precipitacaoMm(new BigDecimal("12.50"))
                    .intensidade(IntensidadeChuva.MODERADA)
                    .dataMedicao(LocalDateTime.now().minusHours(4))
                    .build();

            HistoricoChuvaEntity c2 = HistoricoChuvaEntity.builder()
                    .idChuva(2L)
                    .regiao(regiaoPantanal)
                    .precipitacaoMm(new BigDecimal("32.80"))
                    .intensidade(IntensidadeChuva.FORTE)
                    .dataMedicao(LocalDateTime.now().minusHours(2))
                    .build();

            if (regiaoFranco != null) {
                HistoricoChuvaEntity c3 = HistoricoChuvaEntity.builder()
                        .idChuva(3L)
                        .regiao(regiaoFranco)
                        .precipitacaoMm(new BigDecimal("36.50"))
                        .intensidade(IntensidadeChuva.FORTE)
                        .dataMedicao(LocalDateTime.now().minusMinutes(20))
                        .build();
                historicoChuvaRepository.saveAll(List.of(c1, c2, c3));
            } else {
                historicoChuvaRepository.saveAll(List.of(c1, c2));
            }
        }

        // 5. Histórico de Mensagens / Alertas
        if (historicoMensagemRepository.count() == 0 && regiaoPantanal != null && respPadrao != null) {
            log.info("[MockDataInitializer] Inserindo histórico de mensagens mockadas...");
            HistoricoMensagemEntity m1 = HistoricoMensagemEntity.builder()
                    .idMensagem(1L)
                    .regiao(regiaoPantanal)
                    .responsavel(respPadrao)
                    .severidade(SeveridadeAlerta.ALERTA)
                    .conteudo("[WEATHER BUDDY | ALERTA] Jardim Pantanal: Precipitação de 32.80 mm atingiu o limite de segurança (25.00 mm). Risco CRITICO de alagamento.")
                    .quantidadeEnviosSucesso(2)
                    .dataEnvio(LocalDateTime.now().minusHours(1))
                    .build();

            historicoMensagemRepository.save(m1);
        }
    }
}
