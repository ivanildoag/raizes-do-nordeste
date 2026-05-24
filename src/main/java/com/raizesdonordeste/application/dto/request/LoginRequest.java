package com.raizesdonordeste.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ter formato válido")
    private String email;
    @NotBlank(message = "Senha é obrigatória")
    private String senha;
}
