package com.raizesdonordeste.application.dto.request;

import com.raizesdonordeste.domain.enums.TipoMovimentacao;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MovimentarEstoqueRequest {
    @NotNull(message = "ID da unidade é obrigatório") private Long unidadeId;
    @NotNull(message = "ID do produto é obrigatório") private Long produtoId;
    @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade deve ser positiva") private Integer quantidade;
    @NotNull(message = "Tipo de movimentação é obrigatório") private TipoMovimentacao tipo;
    private String motivo;
}
