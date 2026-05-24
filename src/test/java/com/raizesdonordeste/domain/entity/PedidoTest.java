package com.raizesdonordeste.domain.entity;

import com.raizesdonordeste.domain.enums.StatusPedido;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes das regras de negócio de transição de status do Pedido.
 */
@DisplayName("Pedido - Testes de transição de status")
class PedidoTest {

    @Test
    @DisplayName("Deve permitir transição de AGUARDANDO_PAGAMENTO para PAGO")
    void devePermitirTransicaoDeAguardandoPagamentoParaPago() {
        Pedido pedido = Pedido.builder().status(StatusPedido.AGUARDANDO_PAGAMENTO).build();
        assertTrue(pedido.isTransicaoValida(StatusPedido.PAGO));
    }

    @Test
    @DisplayName("Deve permitir transição de AGUARDANDO_PAGAMENTO para CANCELADO")
    void devePermitirTransicaoDeAguardandoPagamentoParaCancelado() {
        Pedido pedido = Pedido.builder().status(StatusPedido.AGUARDANDO_PAGAMENTO).build();
        assertTrue(pedido.isTransicaoValida(StatusPedido.CANCELADO));
    }

    @Test
    @DisplayName("Deve permitir transição de PAGO para EM_PREPARO")
    void devePermitirTransicaoDePagoParaEmPreparo() {
        Pedido pedido = Pedido.builder().status(StatusPedido.PAGO).build();
        assertTrue(pedido.isTransicaoValida(StatusPedido.EM_PREPARO));
    }

    @Test
    @DisplayName("Deve permitir transição de EM_PREPARO para PRONTO")
    void devePermitirTransicaoDeEmPreparoParaPronto() {
        Pedido pedido = Pedido.builder().status(StatusPedido.EM_PREPARO).build();
        assertTrue(pedido.isTransicaoValida(StatusPedido.PRONTO));
    }

    @Test
    @DisplayName("Deve permitir transição de PRONTO para ENTREGUE")
    void devePermitirTransicaoDeProntoParaEntregue() {
        Pedido pedido = Pedido.builder().status(StatusPedido.PRONTO).build();
        assertTrue(pedido.isTransicaoValida(StatusPedido.ENTREGUE));
    }

    @Test
    @DisplayName("Não deve permitir transição de ENTREGUE para qualquer status")
    void naoDevePermitirTransicaoDeEntregue() {
        Pedido pedido = Pedido.builder().status(StatusPedido.ENTREGUE).build();
        assertFalse(pedido.isTransicaoValida(StatusPedido.PAGO));
        assertFalse(pedido.isTransicaoValida(StatusPedido.CANCELADO));
    }

    @Test
    @DisplayName("Não deve permitir transição de CANCELADO para qualquer status")
    void naoDevePermitirTransicaoDeCancelado() {
        Pedido pedido = Pedido.builder().status(StatusPedido.CANCELADO).build();
        assertFalse(pedido.isTransicaoValida(StatusPedido.PAGO));
        assertFalse(pedido.isTransicaoValida(StatusPedido.EM_PREPARO));
    }

    @Test
    @DisplayName("Não deve permitir transição de EM_PREPARO para CANCELADO")
    void naoDevePermitirTransicaoDeEmPreparoParaCancelado() {
        Pedido pedido = Pedido.builder().status(StatusPedido.EM_PREPARO).build();
        assertFalse(pedido.isTransicaoValida(StatusPedido.CANCELADO));
    }
}
