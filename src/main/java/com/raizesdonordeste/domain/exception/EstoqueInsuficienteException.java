package com.raizesdonordeste.domain.exception;

import java.util.List;

/**
 * Exceção lançada quando o estoque é insuficiente para atender um pedido.
 * Contém detalhes dos itens com estoque insuficiente.
 */
public class EstoqueInsuficienteException extends RuntimeException {
    private final List<DetalheEstoque> detalhes;

    public EstoqueInsuficienteException(String mensagem, List<DetalheEstoque> detalhes) {
        super(mensagem);
        this.detalhes = detalhes;
    }

    public List<DetalheEstoque> getDetalhes() {
        return detalhes;
    }

    public record DetalheEstoque(String campo, String problema) {}
}
