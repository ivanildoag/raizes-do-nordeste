package com.raizesdonordeste.application.dto.request;

import com.raizesdonordeste.domain.enums.StatusPedido;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AtualizarStatusPedidoRequest {
    @NotNull(message = "Novo status é obrigatório") private StatusPedido status;
}
