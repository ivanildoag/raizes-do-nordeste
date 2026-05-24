package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UnidadeResponse {
    private Long id; private String nome; private String endereco; private String cidade;
    private String estado; private String telefone; private Boolean ativa; private LocalDateTime createdAt;
}
