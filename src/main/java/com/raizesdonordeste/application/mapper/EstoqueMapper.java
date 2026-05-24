package com.raizesdonordeste.application.mapper;

import com.raizesdonordeste.application.dto.response.*;
import com.raizesdonordeste.domain.entity.*;

public class EstoqueMapper {
    private EstoqueMapper() {}
    public static EstoqueResponse toResponse(Estoque e) {
        return EstoqueResponse.builder().id(e.getId()).unidadeId(e.getUnidade().getId())
                .unidadeNome(e.getUnidade().getNome()).produtoId(e.getProduto().getId())
                .produtoNome(e.getProduto().getNome()).quantidade(e.getQuantidade())
                .updatedAt(e.getUpdatedAt()).build();
    }
    public static MovimentacaoEstoqueResponse toMovimentacaoResponse(MovimentacaoEstoque m) {
        return MovimentacaoEstoqueResponse.builder().id(m.getId()).estoqueId(m.getEstoque().getId())
                .tipo(m.getTipo().name()).quantidade(m.getQuantidade()).motivo(m.getMotivo())
                .usuarioNome(m.getUsuario() != null ? m.getUsuario().getNome() : null)
                .createdAt(m.getCreatedAt()).build();
    }
}
