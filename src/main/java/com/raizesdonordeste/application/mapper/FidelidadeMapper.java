package com.raizesdonordeste.application.mapper;

import com.raizesdonordeste.application.dto.response.*;
import com.raizesdonordeste.domain.entity.*;

public class FidelidadeMapper {
    private FidelidadeMapper() {}
    public static FidelidadeResponse toResponse(Fidelidade f) {
        return FidelidadeResponse.builder().id(f.getId()).usuarioId(f.getUsuario().getId())
                .usuarioNome(f.getUsuario().getNome()).pontosAcumulados(f.getPontosAcumulados())
                .pontosResgatados(f.getPontosResgatados()).saldo(f.getSaldo())
                .updatedAt(f.getUpdatedAt()).build();
    }
    public static HistoricoFidelidadeResponse toHistoricoResponse(HistoricoFidelidade h) {
        return HistoricoFidelidadeResponse.builder().id(h.getId())
                .pedidoId(h.getPedido() != null ? h.getPedido().getId() : null)
                .pontos(h.getPontos()).tipo(h.getTipo()).descricao(h.getDescricao())
                .createdAt(h.getCreatedAt()).build();
    }
}
