package com.raizesdonordeste.application.dto.response;

import lombok.*; 

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private UsuarioResponse usuario;
}
