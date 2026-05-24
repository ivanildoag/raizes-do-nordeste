package com.raizesdonordeste.domain.exception;

/** Exceção lançada quando as credenciais de login são inválidas */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
