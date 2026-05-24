package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.response.FidelidadeResponse;
import com.raizesdonordeste.application.dto.response.HistoricoFidelidadeResponse;
import com.raizesdonordeste.application.mapper.FidelidadeMapper;
import com.raizesdonordeste.domain.entity.*;
import com.raizesdonordeste.domain.exception.EntidadeNaoEncontradaException;
import com.raizesdonordeste.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Serviço do programa de fidelidade.
 * Regra: A cada R$1 gasto em pedidos pagos, o cliente acumula 1 ponto.
 * Os pontos são acumulados automaticamente após pagamento aprovado.
 */
@Slf4j @Service @RequiredArgsConstructor
public class FidelidadeService {
    private final FidelidadeRepository fidelidadeRepository;
    private final HistoricoFidelidadeRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public FidelidadeResponse consultarSaldo(Long usuarioId) {
        Fidelidade fidelidade = buscarOuCriarFidelidade(usuarioId);
        return FidelidadeMapper.toResponse(fidelidade);
    }

    /** Acumula pontos baseado no valor do pedido (1 ponto por R$1) */
    @Transactional
    public void acumularPontos(Long usuarioId, Long pedidoId, BigDecimal valorPedido) {
        Fidelidade fidelidade = buscarOuCriarFidelidade(usuarioId);
        int pontos = valorPedido.intValue(); // 1 ponto por R$1

        fidelidade.setPontosAcumulados(fidelidade.getPontosAcumulados() + pontos);
        fidelidade.setSaldo(fidelidade.getPontosAcumulados() - fidelidade.getPontosResgatados());
        fidelidadeRepository.save(fidelidade);

        // Registra no histórico
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        historicoRepository.save(HistoricoFidelidade.builder()
                .fidelidade(fidelidade).pedido(pedido).pontos(pontos)
                .tipo("ACUMULO")
                .descricao("Acúmulo de " + pontos + " pontos pelo pedido #" + pedidoId)
                .build());

        log.info("Pontos de fidelidade acumulados: {} pontos para usuário {} (pedido #{})",
                pontos, usuarioId, pedidoId);
    }

    @Transactional(readOnly = true)
    public Page<HistoricoFidelidadeResponse> listarHistorico(Long usuarioId, Pageable pageable) {
        Fidelidade fidelidade = buscarOuCriarFidelidade(usuarioId);
        return historicoRepository.findByFidelidadeId(fidelidade.getId(), pageable)
                .map(FidelidadeMapper::toHistoricoResponse);
    }

    private Fidelidade buscarOuCriarFidelidade(Long usuarioId) {
        return fidelidadeRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(usuarioId)
                            .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
                    return fidelidadeRepository.save(Fidelidade.builder()
                            .usuario(usuario).pontosAcumulados(0).pontosResgatados(0).saldo(0).build());
                });
    }
}
