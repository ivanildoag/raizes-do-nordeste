package com.raizesdonordeste.domain.entity;

import com.raizesdonordeste.domain.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Registra cada movimentação (entrada/saída) de estoque.
 * Serve como log de auditoria para rastreabilidade das operações.
 */
@Entity
@Table(name = "movimentacoes_estoque")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_id", nullable = false)
    private Estoque estoque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(length = 255)
    private String motivo;

    /** Usuário que realizou a movimentação - auditoria */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
