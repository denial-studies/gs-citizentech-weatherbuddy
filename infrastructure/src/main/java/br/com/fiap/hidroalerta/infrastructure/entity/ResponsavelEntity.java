package br.com.fiap.hidroalerta.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tb_responsavel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsavelEntity {

    @Id
    @Column(name = "id_responsavel", nullable = false)
    private Long idResponsavel;

    @Column(name = "nm_responsavel", nullable = false, length = 120)
    private String nomeResponsavel;

    @Column(name = "ds_email_login", nullable = false, unique = true, length = 120)
    private String emailLogin;

    @Column(name = "ds_senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "ds_cargo", nullable = false, length = 60)
    private String cargo;

    @Column(name = "st_ativo", nullable = false, length = 1)
    private String ativo;

    @Column(name = "dt_cadastro", nullable = false)
    private LocalDate dataCadastro;
}
