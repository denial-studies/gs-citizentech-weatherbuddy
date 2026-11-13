package br.com.fiap.hidroalerta.domain.valueobject;

import java.util.Objects;

public record TelefoneContato(String numero) {

    public TelefoneContato {
        Objects.requireNonNull(numero, "O número de telefone é obrigatório.");
        String limpo = numero.trim();
        if (limpo.isEmpty() || limpo.length() > 20) {
            throw new IllegalArgumentException("O número de telefone deve conter até 20 caracteres.");
        }
    }

    public String somenteDigitos() {
        return numero.replaceAll("\\D", "");
    }
}

