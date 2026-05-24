package com.raizesdonordeste.application.dto.request;

import com.raizesdonordeste.domain.enums.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

/**
 * DTO para criação de pedido.
 * O campo canalPedido é OBRIGATÓRIO conforme requisito de multicanalidade.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CriarPedidoRequest {
    @NotNull(message = "ID da unidade é obrigatório") private Long unidadeId;

    /** Multicanalidade: canal de origem é obrigatório para rastreabilidade */
    @NotNull(message = "Canal do pedido é obrigatório (APP, TOTEM, BALCAO, PICKUP, WEB)")
    private CanalPedido canalPedido;

    @NotEmpty(message = "O pedido deve conter ao menos um item")
    @Valid
    private List<ItemPedidoRequest> itens;

    private String observacao;
}
