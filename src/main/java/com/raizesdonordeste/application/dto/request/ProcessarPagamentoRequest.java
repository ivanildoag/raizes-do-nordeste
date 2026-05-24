package com.raizesdonordeste.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProcessarPagamentoRequest {
    @NotNull(message = "ID do pedido é obrigatório") private Long pedidoId;
    @NotBlank(message = "Forma de pagamento é obrigatória") private String formaPagamento;
}
