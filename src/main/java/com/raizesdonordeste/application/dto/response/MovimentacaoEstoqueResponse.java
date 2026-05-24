package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MovimentacaoEstoqueResponse {
    private Long id; private Long estoqueId; private String tipo; private Integer quantidade;
    private String motivo; private String usuarioNome; private LocalDateTime createdAt;
}
