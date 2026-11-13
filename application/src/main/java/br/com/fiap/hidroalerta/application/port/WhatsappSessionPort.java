package br.com.fiap.hidroalerta.application.port;

public interface WhatsappSessionPort {

    String getStatus();

    String getNumeroConectado();

    String getCodigoPareamento();

    String getUltimoErro();

    boolean isConectado();

    boolean temQrCodeDisponivel();

    byte[] obterQrCodePng();

    void iniciarConexaoComNumero(String telefone, boolean usarQrCode);

    void iniciarConexao();

    void desconectar();

    void limparSessao();
}
