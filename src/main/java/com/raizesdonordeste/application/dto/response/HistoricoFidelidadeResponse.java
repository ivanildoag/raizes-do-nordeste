package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class HistoricoFidelidadeResponse {
    private Long id; private Long pedidoId; private Integer pontos; private String tipo;
    private String descricao; private LocalDateTime createdAt;
}
