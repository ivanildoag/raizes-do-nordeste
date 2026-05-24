package com.raizesdonordeste.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CriarUnidadeRequest {
    @NotBlank(message = "Nome é obrigatório") private String nome;
    @NotBlank(message = "Endereço é obrigatório") private String endereco;
    @NotBlank(message = "Cidade é obrigatória") private String cidade;
    @NotBlank(message = "Estado é obrigatório") @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres") private String estado;
    private String telefone;
}
