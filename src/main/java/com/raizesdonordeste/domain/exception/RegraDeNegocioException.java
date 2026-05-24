package com.raizesdonordeste.domain.exception;

/** Exceção genérica para violações de regras de negócio */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
