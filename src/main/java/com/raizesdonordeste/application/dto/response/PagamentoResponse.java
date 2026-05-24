package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PagamentoResponse {
    private Long id; private Long pedidoId; private String formaPagamento; private String status;
    private BigDecimal valor; private String transacaoId; private String mensagem; private LocalDateTime createdAt;
}
