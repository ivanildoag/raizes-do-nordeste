package com.raizesdonordeste.application.mapper;

import com.raizesdonordeste.application.dto.response.*;
import com.raizesdonordeste.domain.entity.*;

public class PagamentoMapper {
    private PagamentoMapper() {}
    public static PagamentoResponse toResponse(Pagamento p) {
        return PagamentoResponse.builder().id(p.getId()).pedidoId(p.getPedido().getId())
                .formaPagamento(p.getFormaPagamento()).status(p.getStatus().name())
                .valor(p.getValor()).transacaoId(p.getTransacaoId()).mensagem(p.getMensagem())
                .createdAt(p.getCreatedAt()).build();
    }
}
