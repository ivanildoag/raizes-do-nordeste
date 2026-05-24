package com.raizesdonordeste.domain.entity;

import com.raizesdonordeste.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um pagamento associado a um pedido.
 * 
 * Integração mock: O pagamento é processado por um gateway simulado
 * que retorna aprovação, recusa ou timeout.
 * Em produção, seria substituído por integração real com gateway de pagamento.
 */
@Entity
@Table(name = "pagamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "forma_pagamento", nullable = false, length = 30)
    private String formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPagamento status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /** ID da transação retornado pelo gateway (mock) */
    @Column(name = "transacao_id", length = 100)
    private String transacaoId;

    @Column(length = 255)
    private String mensagem;

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
