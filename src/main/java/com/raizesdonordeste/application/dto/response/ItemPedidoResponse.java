package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemPedidoResponse {
    private Long id; private Long produtoId; private String produtoNome;
    private Integer quantidade; private BigDecimal precoUnitario; private BigDecimal subtotal;
}
