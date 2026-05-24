package com.raizesdonordeste.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidade que gerencia os pontos de fidelidade do cliente.
 * Regra: a cada R$1 gasto em pedidos pagos, o cliente acumula 1 ponto.
 * O saldo é calculado como pontosAcumulados - pontosResgatados.
 */
@Entity
@Table(name = "fidelidade")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Builder.Default
    @Column(name = "pontos_acumulados", nullable = false)
    private Integer pontosAcumulados = 0;

    @Builder.Default
    @Column(name = "pontos_resgatados", nullable = false)
    private Integer pontosResgatados = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer saldo = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
