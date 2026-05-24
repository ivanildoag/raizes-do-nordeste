package com.raizesdonordeste.domain.entity;

import com.raizesdonordeste.domain.enums.CanalPedido;
import com.raizesdonordeste.domain.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um pedido realizado em uma unidade.
 * 
 * Regras de negócio:
 * - O campo canalPedido é OBRIGATÓRIO (requisito de multicanalidade).
 * - O status inicial é AGUARDANDO_PAGAMENTO.
 * - Transições de status válidas são controladas pelo método validarTransicao().
 * - O total é calculado a partir dos itens do pedido.
 */
@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id", nullable = false)
    private Unidade unidade;

    /** Multicanalidade: campo obrigatório que identifica a origem do pedido */
    @Enumerated(EnumType.STRING)
    @Column(name = "canal_pedido", nullable = false, length = 10)
    private CanalPedido canalPedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    @Builder.Default
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(length = 500)
    private String observacao;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPedido> itens = new ArrayList<>();

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

    /**
     * Valida se a transição de status é permitida.
     * Regra de negócio:
     * - AGUARDANDO_PAGAMENTO → PAGO ou CANCELADO
     * - PAGO → EM_PREPARO ou CANCELADO
     * - EM_PREPARO → PRONTO
     * - PRONTO → ENTREGUE
     * - ENTREGUE e CANCELADO são estados finais (sem transição)
     */
    public boolean isTransicaoValida(StatusPedido novoStatus) {
        return switch (this.status) {
            case AGUARDANDO_PAGAMENTO -> novoStatus == StatusPedido.PAGO || novoStatus == StatusPedido.CANCELADO;
            case PAGO -> novoStatus == StatusPedido.EM_PREPARO || novoStatus == StatusPedido.CANCELADO;
            case EM_PREPARO -> novoStatus == StatusPedido.PRONTO;
            case PRONTO -> novoStatus == StatusPedido.ENTREGUE;
            case ENTREGUE, CANCELADO -> false;
        };
    }
}
