package com.raizesdonordeste.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Padrão de resposta de erro da API.
 * Todas as exceções devem retornar este formato para consistência.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ErroResponse {
    private String error;
    private String message;
    private List<DetalheErro> details;
    private LocalDateTime timestamp;
    private String path;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DetalheErro {
        private String field;
        private String issue;
    }
}
