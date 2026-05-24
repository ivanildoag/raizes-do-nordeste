package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.ProcessarPagamentoRequest;
import com.raizesdonordeste.application.dto.response.PagamentoResponse;
import com.raizesdonordeste.application.mapper.PagamentoMapper;
import com.raizesdonordeste.application.port.PagamentoGatewayPort;
import com.raizesdonordeste.domain.entity.Pagamento;
import com.raizesdonordeste.domain.entity.Pedido;
import com.raizesdonordeste.domain.enums.StatusPagamento;
import com.raizesdonordeste.domain.enums.StatusPedido;
import com.raizesdonordeste.domain.exception.EntidadeNaoEncontradaException;
import com.raizesdonordeste.domain.exception.RegraDeNegocioException;
import com.raizesdonordeste.domain.repository.PagamentoRepository;
import com.raizesdonordeste.domain.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de processamento de pagamento mock.
 * 
 * Fluxo:
 * 1. Valida que o pedido está AGUARDANDO_PAGAMENTO
 * 2. Envia para o gateway mock
 * 3. Se APROVADO: atualiza pedido para PAGO + acumula pontos de fidelidade
 * 4. Se RECUSADO/TIMEOUT: mantém AGUARDANDO_PAGAMENTO, registra falha
 */
@Slf4j @Service @RequiredArgsConstructor
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoGatewayPort pagamentoGateway;
    private final FidelidadeService fidelidadeService;

    @Transactional
    public PagamentoResponse processarPagamento(ProcessarPagamentoRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pedido não encontrado"));

        // Valida que o pedido está aguardando pagamento
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new RegraDeNegocioException(
                    "Pedido não está aguardando pagamento. Status atual: " + pedido.getStatus());
        }

        // Processa pagamento no gateway mock
        PagamentoGatewayPort.ResultadoPagamento resultado =
                pagamentoGateway.processar(pedido.getTotal(), request.getFormaPagamento());

        // Salva o registro de pagamento
        Pagamento pagamento = Pagamento.builder()
                .pedido(pedido)
                .formaPagamento(request.getFormaPagamento())
                .status(resultado.status())
                .valor(pedido.getTotal())
                .transacaoId(resultado.transacaoId())
                .mensagem(resultado.mensagem())
                .build();
        pagamento = pagamentoRepository.save(pagamento);

        // Se aprovado, atualiza status do pedido e acumula pontos
        if (resultado.status() == StatusPagamento.APROVADO) {
            pedido.setStatus(StatusPedido.PAGO);
            pedidoRepository.save(pedido);

            // Acumula pontos de fidelidade
            fidelidadeService.acumularPontos(pedido.getCliente().getId(), pedido.getId(), pedido.getTotal());

            log.info("Pagamento APROVADO para pedido #{} - Valor: R${}", pedido.getId(), pedido.getTotal());
        } else {
            log.warn("Pagamento {} para pedido #{} - Motivo: {}",
                    resultado.status(), pedido.getId(), resultado.mensagem());
        }

        return PagamentoMapper.toResponse(pagamento);
    }

    @Transactional(readOnly = true)
    public PagamentoResponse consultarPorPedido(Long pedidoId) {
        Pagamento pagamento = pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Pagamento não encontrado para o pedido: " + pedidoId));
        return PagamentoMapper.toResponse(pagamento);
    }
}
