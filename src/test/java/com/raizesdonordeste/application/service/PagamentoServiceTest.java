package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.ProcessarPagamentoRequest;
import com.raizesdonordeste.application.dto.response.PagamentoResponse;
import com.raizesdonordeste.application.port.PagamentoGatewayPort;
import com.raizesdonordeste.domain.entity.*;
import com.raizesdonordeste.domain.enums.*;
import com.raizesdonordeste.domain.exception.RegraDeNegocioException;
import com.raizesdonordeste.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagamentoService - Testes do pagamento mock")
class PagamentoServiceTest {

    @Mock private PagamentoRepository pagamentoRepository;
    @Mock private PedidoRepository pedidoRepository;
    @Mock private PagamentoGatewayPort pagamentoGateway;
    @Mock private FidelidadeService fidelidadeService;
    @InjectMocks private PagamentoService pagamentoService;

    private Pedido pedido;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        Usuario cliente = Usuario.builder().id(1L).nome("Maria").email("maria@teste.com")
                .perfil(PerfilUsuario.CLIENTE).ativo(true).cpf("123").senhaHash("h")
                .consentimentoLgpd(true).createdAt(LocalDateTime.now()).build();
        Unidade unidade = Unidade.builder().id(1L).nome("Fortaleza").endereco("R").cidade("F").estado("CE")
                .ativa(true).createdAt(LocalDateTime.now()).build();
        pedido = Pedido.builder().id(1L).cliente(cliente).unidade(unidade).canalPedido(CanalPedido.APP)
                .status(StatusPedido.AGUARDANDO_PAGAMENTO).total(new BigDecimal("50.00"))
                .itens(new ArrayList<>()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        pagamento = Pagamento.builder().id(1L).pedido(pedido).formaPagamento("PIX")
                .status(StatusPagamento.APROVADO).valor(new BigDecimal("50.00"))
                .transacaoId("txn-123").mensagem("Aprovado")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("Deve processar pagamento aprovado e atualizar status do pedido para PAGO")
    void deveProcessarPagamentoAprovadoEAtualizarStatusParaPago() {
        // Given
        ProcessarPagamentoRequest request = ProcessarPagamentoRequest.builder().pedidoId(1L).formaPagamento("PIX").build();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pagamentoGateway.processar(any(), any())).thenReturn(
                new PagamentoGatewayPort.ResultadoPagamento(StatusPagamento.APROVADO, "txn-123", "Aprovado"));
        when(pagamentoRepository.save(any())).thenReturn(pagamento);
        when(pedidoRepository.save(any())).thenReturn(pedido);

        // When
        PagamentoResponse response = pagamentoService.processarPagamento(request);

        // Then
        assertNotNull(response);
        assertEquals("APROVADO", response.getStatus());
        verify(pedidoRepository).save(any()); // Status atualizado
        verify(fidelidadeService).acumularPontos(1L, 1L, new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("Deve registrar pagamento recusado sem alterar status do pedido")
    void deveRegistrarPagamentoRecusadoSemAlterarStatus() {
        // Given
        ProcessarPagamentoRequest request = ProcessarPagamentoRequest.builder().pedidoId(1L).formaPagamento("CARTAO").build();
        pagamento.setStatus(StatusPagamento.RECUSADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pagamentoGateway.processar(any(), any())).thenReturn(
                new PagamentoGatewayPort.ResultadoPagamento(StatusPagamento.RECUSADO, "txn-456", "Recusado"));
        when(pagamentoRepository.save(any())).thenReturn(pagamento);

        // When
        PagamentoResponse response = pagamentoService.processarPagamento(request);

        // Then
        assertNotNull(response);
        assertEquals("RECUSADO", response.getStatus());
        verify(pedidoRepository, never()).save(any()); // Não altera status
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não está aguardando pagamento")
    void deveLancarExcecaoQuandoPedidoNaoAguardandoPagamento() {
        // Given
        pedido.setStatus(StatusPedido.PAGO);
        ProcessarPagamentoRequest request = ProcessarPagamentoRequest.builder().pedidoId(1L).formaPagamento("PIX").build();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When/Then
        assertThrows(RegraDeNegocioException.class, () -> pagamentoService.processarPagamento(request));
    }
}
