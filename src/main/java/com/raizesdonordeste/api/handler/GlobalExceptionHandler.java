package com.raizesdonordeste.api.handler;

import com.raizesdonordeste.application.dto.response.ErroResponse;
import com.raizesdonordeste.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tratamento global de exceções da API.
 * Garante que todas as respostas de erro seguem o padrão JSON definido.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex, HttpServletRequest request) {
        log.warn("Entidade não encontrada: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.NOT_FOUND, "ENTIDADE_NAO_ENCONTRADA", ex.getMessage(), List.of(), request);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ErroResponse> handleEstoqueInsuficiente(EstoqueInsuficienteException ex, HttpServletRequest request) {
        log.warn("Estoque insuficiente: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        List<ErroResponse.DetalheErro> details = ex.getDetalhes().stream()
                .map(d -> ErroResponse.DetalheErro.builder().field(d.campo()).issue(d.problema()).build())
                .collect(Collectors.toList());
        return buildResponse(HttpStatus.CONFLICT, "ESTOQUE_INSUFICIENTE", ex.getMessage(), details, request);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResponse> handleCredenciaisInvalidas(CredenciaisInvalidasException ex, HttpServletRequest request) {
        log.warn("Credenciais inválidas - Path: {}", request.getRequestURI());
        return buildResponse(HttpStatus.UNAUTHORIZED, "CREDENCIAIS_INVALIDAS", ex.getMessage(), List.of(), request);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraDeNegocio(RegraDeNegocioException ex, HttpServletRequest request) {
        log.warn("Regra de negócio violada: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.CONFLICT, "REGRA_DE_NEGOCIO", ex.getMessage(), List.of(), request);
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<ErroResponse> handleTransicaoStatusInvalida(TransicaoStatusInvalidaException ex, HttpServletRequest request) {
        log.warn("Transição de status inválida: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "TRANSICAO_STATUS_INVALIDA", ex.getMessage(), List.of(), request);
    }

    @ExceptionHandler(PagamentoRecusadoException.class)
    public ResponseEntity<ErroResponse> handlePagamentoRecusado(PagamentoRecusadoException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "PAGAMENTO_RECUSADO", ex.getMessage(), List.of(), request);
    }

    /** Trata erros de validação Bean Validation (campos inválidos) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErroResponse.DetalheErro> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> ErroResponse.DetalheErro.builder().field(e.getField()).issue(e.getDefaultMessage()).build())
                .collect(Collectors.toList());
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDACAO_ERRO", "Erro de validação nos campos enviados", details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA", "Corpo da requisição inválido ou malformado", List.of(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "ACESSO_NEGADO", "Você não tem permissão para acessar este recurso", List.of(), request);
    }

    /** Tratamento genérico para exceções não previstas */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Erro interno não tratado: {} - Path: {}", ex.getMessage(), request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO", "Erro interno do servidor", List.of(), request);
    }

    private ResponseEntity<ErroResponse> buildResponse(HttpStatus status, String error, String message,
                                                         List<ErroResponse.DetalheErro> details, HttpServletRequest request) {
        ErroResponse response = ErroResponse.builder()
                .error(error).message(message).details(details)
                .timestamp(LocalDateTime.now()).path(request.getRequestURI()).build();
        return ResponseEntity.status(status).body(response);
    }
}
