package com.raizesdonordeste.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarUsuarioRequest {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    private String telefone;

    /** LGPD: O consentimento do titular é obrigatório para tratamento de dados */
    @NotNull(message = "Consentimento LGPD é obrigatório")
    private Boolean consentimentoLgpd;
}
