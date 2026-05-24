package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FidelidadeResponse {
    private Long id; private Long usuarioId; private String usuarioNome;
    private Integer pontosAcumulados; private Integer pontosResgatados; private Integer saldo;
    private LocalDateTime updatedAt;
}
