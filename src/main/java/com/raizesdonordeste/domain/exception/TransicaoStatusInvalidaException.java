package com.raizesdonordeste.domain.exception;

/** Exceção lançada quando uma transição de status do pedido não é permitida */
public class TransicaoStatusInvalidaException extends RuntimeException {
    public TransicaoStatusInvalidaException(String mensagem) {
        super(mensagem);
    }
}
