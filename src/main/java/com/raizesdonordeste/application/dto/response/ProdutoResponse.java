package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.math.BigDecimal; import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProdutoResponse {
    private Long id; private String nome; private String descricao; private String categoria;
    private BigDecimal preco; private String imagemUrl; private Boolean ativo; private LocalDateTime createdAt;
}
