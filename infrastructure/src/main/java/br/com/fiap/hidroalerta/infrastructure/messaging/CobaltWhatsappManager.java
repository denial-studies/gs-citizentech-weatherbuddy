package br.com.fiap.hidroalerta.infrastructure.messaging;

import br.com.fiap.hidroalerta.application.port.WhatsappSessionPort;
import com.github.auties00.cobalt.client.linked.LinkedWhatsAppClient;
import com.github.auties00.cobalt.client.linked.LinkedWhatsAppClientPasskeyAuthenticator;
import com.github.auties00.cobalt.client.linked.LinkedWhatsAppClientVerificationHandler;
import com.github.auties00.cobalt.store.linked.protobuf.temporary.TemporaryLinkedWhatsAppStoreFactory;
import com.github.auties00.cobalt.wire.core.jid.Jid;
import com.github.auties00.cobalt.wire.linked.message.LinkedMessageContainer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

@Slf4j
@Service
public class CobaltWhatsappManager implements WhatsappSessionPort {

    private static final Path LEGACY_SESSION_DIR = Path.of(".cobalt-session");

    private volatile LinkedWhatsAppClient client;
    private volatile String status = "DESCONECTADO";
    private volatile String numeroConectado;
    private volatile String codigoPareamento;
    private volatile String ultimoErro;
    private volatile byte[] qrCodePng;

    @EventListener(ApplicationReadyEvent.class)
    public void solicitarNovoNumeroAoIniciar() {
        removerPastaSessaoAntigaSeExistir();

        Thread.ofVirtual().name("cobalt-console-prompt").start(() -> {
            System.out.println("""
                    
                    ========================================================================
                    [WEATHER BUDDY | COBALT WHATSAPP - SESSÃO TEMPORÁRIA EM MEMÓRIA]
                    Nenhum número fica salvo no projeto.
                    Sempre que o backend iniciar, informe um novo número para conectar:
                      1) Pela tela web: http://localhost:8080/dashboard/envios
                      2) Ou digite o número aqui no terminal (ex: 11999999999) e tecle ENTER:
                    ========================================================================
                    """);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String linha = reader.readLine();
                if (linha != null && !linha.isBlank() && "DESCONECTADO".equals(status)) {
                    log.info("Número recebido pelo terminal: {}. Iniciando pareamento...", linha.trim());
                    iniciarConexaoComNumero(linha.trim(), false);
                }
            } catch (Exception ignored) {
                // Caso o terminal não seja interativo, o usuário informa pela tela web.
            }
        });
    }

    @Override
    public synchronized void iniciarConexao() {
        throw new IllegalArgumentException("Informe um número de WhatsApp para iniciar a conexão desta execução.");
    }

    @Override
    public synchronized void iniciarConexaoComNumero(String telefone, boolean usarQrCode) {
        String digitos = normalizarDigitosBrasil(telefone);
        long numeroLong = Long.parseLong(digitos);

        desconectar();

        this.ultimoErro = null;
        this.qrCodePng = null;
        this.codigoPareamento = null;
        this.numeroConectado = "+" + digitos;
        this.status = "CONECTANDO";

        Thread.ofVirtual().name("cobalt-whatsapp-connect").start(() -> {
            try {
                // Usa exclusivamente TemporaryLinkedWhatsAppStoreFactory (100% em memória, nada salvo em disco)
                var options = LinkedWhatsAppClient.builder()
                        .webClient(TemporaryLinkedWhatsAppStoreFactory.INSTANCE)
                        .createConnection()
                        .discardHistory();

                LinkedWhatsAppClient newClient;
                if (usarQrCode) {
                    Path tempQr = Files.createTempFile("weatherbuddy-qr-", ".png");
                    newClient = options.unregistered(LinkedWhatsAppClientVerificationHandler.Web.QrCode.toFile(tempQr, path -> {
                        try {
                            this.qrCodePng = Files.readAllBytes(path);
                            this.status = "AGUARDANDO_QR";
                            log.info("QR Code temporário gerado em memória para o número +{}", digitos);
                        } catch (IOException e) {
                            log.error("Erro ao ler QR Code temporário: {}", e.getMessage());
                        } finally {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                            }
                        }
                    }));
                } else {
                    newClient = options.unregistered(numeroLong, new LinkedWhatsAppClientVerificationHandler.Web.PairingCode() {
                        @Override
                        public void handle(String code) {
                            codigoPareamento = code != null ? code.trim() : "";
                            status = "AGUARDANDO_CODIGO";
                            System.out.println("\n========================================================================");
                            System.out.println("[COBALT WHATSAPP] NÚMERO: +" + digitos);
                            System.out.println("[COBALT WHATSAPP] CÓDIGO DE PAREAMENTO: " + codigoPareamento);
                            System.out.println("No seu celular, abra: WhatsApp -> Dispositivos conectados -> Conectar dispositivo -> Conectar com número de telefone");
                            System.out.println("========================================================================\n");
                        }

                        @Override
                        public LinkedWhatsAppClientPasskeyAuthenticator passkeyAuthenticator() {
                            return LinkedWhatsAppClientPasskeyAuthenticator.toTerminal();
                        }
                    });
                }

                newClient.addLoggedInListener(c -> {
                    this.status = "CONECTADO";
                    this.qrCodePng = null;
                    this.codigoPareamento = null;
                    this.ultimoErro = null;
                    log.info("WhatsApp conectado com sucesso (somente em memória) para o número {}", this.numeroConectado);
                });

                newClient.addDisconnectedListener((c, reason) -> {
                    log.warn("WhatsApp desconectado ({}). Sessão em memória encerrada.", reason);
                    this.status = "DESCONECTADO";
                    this.qrCodePng = null;
                    this.codigoPareamento = null;
                });

                this.client = newClient;
                newClient.connect();
            } catch (Exception ex) {
                log.error("Erro ao conectar WhatsApp via Cobalt: {}", ex.getMessage(), ex);
                this.status = "ERRO";
                this.ultimoErro = ex.getMessage();
            }
        });
    }

    @Override
    public synchronized void desconectar() {
        try {
            if (client != null) {
                client.disconnect();
            }
        } catch (Exception ex) {
            log.warn("Erro ao desconectar WhatsApp: {}", ex.getMessage());
        } finally {
            client = null;
            status = "DESCONECTADO";
            qrCodePng = null;
            codigoPareamento = null;
            numeroConectado = null;
        }
    }

    @Override
    public synchronized void limparSessao() {
        desconectar();
        ultimoErro = null;
        removerPastaSessaoAntigaSeExistir();
    }

    public boolean enviarMensagemTexto(String telefoneDestino, String texto) {
        if (!isConectado()) {
            return false;
        }
        String digitos = normalizarDigitosBrasil(telefoneDestino);
        String jidDestino = digitos + "@s.whatsapp.net";
        client.sendMessage(Jid.of(jidDestino), LinkedMessageContainer.of(texto));
        log.info("Mensagem WhatsApp enviada via Cobalt para {}", jidDestino);
        return true;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public String getNumeroConectado() {
        return numeroConectado;
    }

    @Override
    public String getCodigoPareamento() {
        return codigoPareamento;
    }

    @Override
    public String getUltimoErro() {
        return ultimoErro;
    }

    @Override
    public boolean isConectado() {
        return "CONECTADO".equals(status) && client != null && client.isConnected();
    }

    @Override
    public boolean temQrCodeDisponivel() {
        return qrCodePng != null && qrCodePng.length > 0;
    }

    @Override
    public byte[] obterQrCodePng() {
        return qrCodePng;
    }

    @PreDestroy
    public void encerrarAoDesligar() {
        desconectar();
        removerPastaSessaoAntigaSeExistir();
    }

    private String normalizarDigitosBrasil(String telefone) {
        String digitos = telefone == null ? "" : telefone.replaceAll("\\D", "");
        if (digitos.length() < 10) {
            throw new IllegalArgumentException("Informe um número de WhatsApp válido com DDD (ex.: 11987654321).");
        }
        if (digitos.length() == 10 || digitos.length() == 11) {
            digitos = "55" + digitos;
        }
        return digitos;
    }

    private void removerPastaSessaoAntigaSeExistir() {
        if (Files.exists(LEGACY_SESSION_DIR)) {
            try (Stream<Path> paths = Files.walk(LEGACY_SESSION_DIR)) {
                paths.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.deleteIfExists(p);
                            } catch (IOException ignored) {
                            }
                        });
            } catch (IOException ignored) {
            }
        }
    }
}
