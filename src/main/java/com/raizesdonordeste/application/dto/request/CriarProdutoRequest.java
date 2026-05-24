package com.raizesdonordeste.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CriarProdutoRequest {
    @NotBlank(message = "Nome é obrigatório") private String nome;
    private String descricao;
    @NotBlank(message = "Categoria é obrigatória") private String categoria;
    @NotNull(message = "Preço é obrigatório") @Positive(message = "Preço deve ser positivo") private BigDecimal preco;
    private String imagemUrl;
}
