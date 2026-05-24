package com.raizesdonordeste.domain.exception;

/** Exceção lançada quando uma entidade não é encontrada no banco de dados */
public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
