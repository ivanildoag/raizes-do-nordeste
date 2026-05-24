package com.raizesdonordeste.application.dto.request;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AtualizarUnidadeRequest {
    private String nome;
    private String endereco;
    private String cidade;
    private String estado;
    private String telefone;
}
