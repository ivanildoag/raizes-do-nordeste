package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PedidoResponse {
    private Long id; private Long clienteId; private String clienteNome; private Long unidadeId;
    private String unidadeNome; private String canalPedido; private String status;
    private BigDecimal total; private String observacao; private List<ItemPedidoResponse> itens;
    private LocalDateTime createdAt; private LocalDateTime updatedAt;
}
