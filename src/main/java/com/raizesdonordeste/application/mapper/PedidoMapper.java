package com.raizesdonordeste.application.mapper;

import com.raizesdonordeste.application.dto.response.*;
import com.raizesdonordeste.domain.entity.*;
import java.util.stream.Collectors;

public class PedidoMapper {
    private PedidoMapper() {}

    public static PedidoResponse toResponse(Pedido p) {
        return PedidoResponse.builder().id(p.getId())
                .clienteId(p.getCliente().getId()).clienteNome(p.getCliente().getNome())
                .unidadeId(p.getUnidade().getId()).unidadeNome(p.getUnidade().getNome())
                .canalPedido(p.getCanalPedido().name()).status(p.getStatus().name())
                .total(p.getTotal()).observacao(p.getObservacao())
                .itens(p.getItens().stream().map(PedidoMapper::toItemResponse).collect(Collectors.toList()))
                .createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt()).build();
    }

    public static ItemPedidoResponse toItemResponse(ItemPedido i) {
        return ItemPedidoResponse.builder().id(i.getId()).produtoId(i.getProduto().getId())
                .produtoNome(i.getProduto().getNome()).quantidade(i.getQuantidade())
                .precoUnitario(i.getPrecoUnitario()).subtotal(i.getSubtotal()).build();
    }
}
