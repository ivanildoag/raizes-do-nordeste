package com.raizesdonordeste.application.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AtualizarProdutoRequest {
    private String nome;
    private String descricao;
    private String categoria;
    private BigDecimal preco;
    private String imagemUrl;
}
