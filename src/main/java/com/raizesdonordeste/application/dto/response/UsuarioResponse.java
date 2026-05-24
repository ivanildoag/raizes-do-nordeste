package com.raizesdonordeste.application.dto.response;

import lombok.*; import java.time.LocalDateTime;

/** LGPD: Esta resposta nunca inclui senhaHash. O CPF é mascarado. */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    /** LGPD: CPF mascarado no formato ***.***.XXX-XX */
    private String cpfMascarado;
    private String perfil;
    private Boolean ativo;
    private LocalDateTime createdAt;
}
