package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.CriarPedidoRequest;
import com.raizesdonordeste.application.dto.request.ItemPedidoRequest;
import com.raizesdonordeste.application.dto.response.PedidoResponse;
import com.raizesdonordeste.domain.entity.*;
import com.raizesdonordeste.domain.enums.*;
import com.raizesdonordeste.domain.exception.*;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do serviço de pedidos — fluxo crítico do sistema.
 * Cobre criação, validação de estoque, transições de status e cancelamento.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoService - Testes do fluxo de pedidos")
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UnidadeRepository unidadeRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private EstoqueService estoqueService;
    @InjectMocks private PedidoService pedidoService;

    private Usuario cliente;
    private Unidade unidade;
    private Produto produto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        cliente = Usuario.builder().id(1L).nome("Maria").email("maria@teste.com")
                .perfil(PerfilUsuario.CLIENTE).ativo(true).cpf("123").senhaHash("hash")
                .consentimentoLgpd(true).createdAt(LocalDateTime.now()).build();
        unidade = Unidade.builder().id(1L).nome("Fortaleza Centro").ativa(true)
                .endereco("Rua A").cidade("Fortaleza").estado("CE")
                .createdAt(LocalDateTime.now()).build();
        produto = Produto.builder().id(1L).nome("Acarajé").preco(new BigDecimal("15.90"))
                .categoria("SALGADOS").ativo(true).createdAt(LocalDateTime.now()).build();
        pedido = Pedido.builder().id(1L).cliente(cliente).unidade(unidade)
                .canalPedido(CanalPedido.APP).status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .total(new BigDecimal("31.80")).itens(new ArrayList<>())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("Deve criar pedido quando estoque for suficiente")
    void deveCriarPedidoQuandoEstoqueForSuficiente() {
        // Given
        CriarPedidoRequest request = CriarPedidoRequest.builder()
                .unidadeId(1L).canalPedido(CanalPedido.APP)
                .itens(List.of(ItemPedidoRequest.builder().produtoId(1L).quantidade(2).build()))
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueService.verificarDisponibilidade(1L, 1L, 2)).thenReturn(true);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(estoqueService.movimentar(any(), anyLong())).thenReturn(null);

        // When
        PedidoResponse response = pedidoService.criar(request, 1L);

        // Then
        assertNotNull(response);
        assertEquals("AGUARDANDO_PAGAMENTO", response.getStatus());
        assertEquals("APP", response.getCanalPedido());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque for insuficiente")
    void deveLancarExcecaoQuandoEstoqueForInsuficiente() {
        // Given
        CriarPedidoRequest request = CriarPedidoRequest.builder()
                .unidadeId(1L).canalPedido(CanalPedido.TOTEM)
                .itens(List.of(ItemPedidoRequest.builder().produtoId(1L).quantidade(100).build()))
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueService.verificarDisponibilidade(1L, 1L, 100)).thenReturn(false);

        // When/Then
        assertThrows(EstoqueInsuficienteException.class, () -> pedidoService.criar(request, 1L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando unidade não encontrada")
    void deveLancarExcecaoQuandoUnidadeNaoEncontrada() {
        // Given
        CriarPedidoRequest request = CriarPedidoRequest.builder()
                .unidadeId(999L).canalPedido(CanalPedido.WEB)
                .itens(List.of(ItemPedidoRequest.builder().produtoId(1L).quantidade(1).build()))
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(unidadeRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntidadeNaoEncontradaException.class, () -> pedidoService.criar(request, 1L));
    }

    @Test
    @DisplayName("Deve atualizar status quando transição é válida")
    void deveAtualizarStatusQuandoTransicaoValida() {
        // Given
        pedido.setStatus(StatusPedido.PAGO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // When
        PedidoResponse response = pedidoService.atualizarStatus(1L, StatusPedido.EM_PREPARO);

        // Then
        assertNotNull(response);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando transição de status é inválida")
    void deveLancarExcecaoQuandoTransicaoStatusInvalida() {
        // Given
        pedido.setStatus(StatusPedido.ENTREGUE); // Estado final
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When/Then
        assertThrows(TransicaoStatusInvalidaException.class,
                () -> pedidoService.atualizarStatus(1L, StatusPedido.PAGO));
    }

    @Test
    @DisplayName("Deve cancelar pedido e restaurar estoque")
    void deveCancelarPedidoERestaurarEstoque() {
        // Given
        ItemPedido item = ItemPedido.builder().id(1L).produto(produto).quantidade(2)
                .precoUnitario(produto.getPreco()).subtotal(new BigDecimal("31.80")).pedido(pedido).build();
        pedido.setItens(List.of(item));
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(estoqueService.movimentar(any(), anyLong())).thenReturn(null);

        // When
        PedidoResponse response = pedidoService.cancelar(1L, 1L);

        // Then
        assertNotNull(response);
        verify(estoqueService).movimentar(any(), anyLong()); // Estoque restaurado
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar pedido já entregue")
    void deveLancarExcecaoAoCancelarPedidoJaEntregue() {
        // Given
        pedido.setStatus(StatusPedido.ENTREGUE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // When/Then
        assertThrows(TransicaoStatusInvalidaException.class, () -> pedidoService.cancelar(1L, 1L));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado ao criar pedido")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // Given
        CriarPedidoRequest request = CriarPedidoRequest.builder()
                .unidadeId(1L).canalPedido(CanalPedido.BALCAO)
                .itens(List.of(ItemPedidoRequest.builder().produtoId(999L).quantidade(1).build()))
                .build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntidadeNaoEncontradaException.class, () -> pedidoService.criar(request, 1L));
    }
}
