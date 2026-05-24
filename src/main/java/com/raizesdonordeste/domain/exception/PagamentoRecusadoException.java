package com.raizesdonordeste.domain.exception;

/** Exceção lançada quando o pagamento é recusado pelo gateway mock */
public class PagamentoRecusadoException extends RuntimeException {
    public PagamentoRecusadoException(String mensagem) {
        super(mensagem);
    }
}
