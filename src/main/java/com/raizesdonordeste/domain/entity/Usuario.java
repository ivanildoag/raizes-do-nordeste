package com.raizesdonordeste.domain.entity;

import com.raizesdonordeste.domain.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário do sistema.
 * 
 * LGPD: Esta entidade armazena dados pessoais (nome, email, CPF, telefone).
 * - A senha é armazenada como hash BCrypt, nunca em texto plano.
 * - O CPF deve ser mascarado nas respostas da API (ex: ***.XXX.XXX-**).
 * - O campo consentimentoLgpd registra o consentimento do titular.
 * - O email é utilizado como identificador de login.
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    /** LGPD: Email é dado pessoal, usado como identificador de autenticação */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** Segurança: Armazenado como hash BCrypt - nunca retornar em respostas da API */
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    /** LGPD: CPF é dado pessoal sensível, deve ser mascarado nas respostas */
    @Column(unique = true, length = 14)
    private String cpf;

    @Column(length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PerfilUsuario perfil;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    /** LGPD: Registra o consentimento do titular para tratamento de dados */
    @Column(name = "consentimento_lgpd", nullable = false)
    private Boolean consentimentoLgpd;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
