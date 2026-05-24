package com.raizesdonordeste.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemPedidoRequest {
    @NotNull(message = "ID do produto é obrigatório") private Long produtoId;
    @NotNull(message = "Quantidade é obrigatória") @Min(value = 1, message = "Quantidade mínima é 1") private Integer quantidade;
}
