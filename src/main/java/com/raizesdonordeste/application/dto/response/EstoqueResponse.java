package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EstoqueResponse {
    private Long id; private Long unidadeId; private String unidadeNome; private Long produtoId;
    private String produtoNome; private Integer quantidade; private LocalDateTime updatedAt;
}
