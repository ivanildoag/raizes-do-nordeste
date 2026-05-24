package com.raizesdonordeste.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Histórico de movimentações do programa de fidelidade.
 * Registra acúmulos e resgates de pontos para auditoria.
 */
@Entity
@Table(name = "historico_fidelidade")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoFidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fidelidade_id", nullable = false)
    private Fidelidade fidelidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(nullable = false)
    private Integer pontos;

    /** Tipo da movimentação: ACUMULO ou RESGATE */
    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(length = 255)
    private String descricao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
