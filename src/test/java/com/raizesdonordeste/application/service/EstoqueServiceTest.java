package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.MovimentarEstoqueRequest;
import com.raizesdonordeste.application.dto.response.EstoqueResponse;
import com.raizesdonordeste.domain.entity.*;
import com.raizesdonordeste.domain.enums.TipoMovimentacao;
import com.raizesdonordeste.domain.exception.EstoqueInsuficienteException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EstoqueService - Testes de controle de estoque")
class EstoqueServiceTest {

    @Mock private EstoqueRepository estoqueRepository;
    @Mock private MovimentacaoEstoqueRepository movimentacaoRepository;
    @Mock private UnidadeRepository unidadeRepository;
    @Mock private ProdutoRepository produtoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @InjectMocks private EstoqueService estoqueService;

    private Estoque estoque;
    private Unidade unidade;
    private Produto produto;

    @BeforeEach
    void setUp() {
        unidade = Unidade.builder().id(1L).nome("Fortaleza").endereco("R").cidade("F").estado("CE")
                .ativa(true).createdAt(LocalDateTime.now()).build();
        produto = Produto.builder().id(1L).nome("Acarajé").preco(new BigDecimal("15.90"))
                .categoria("SALGADOS").ativo(true).createdAt(LocalDateTime.now()).build();
        estoque = Estoque.builder().id(1L).unidade(unidade).produto(produto)
                .quantidade(50).updatedAt(LocalDateTime.now()).build();
    }

    @Test
    @DisplayName("Deve realizar entrada de estoque com sucesso")
    void deveRealizarEntradaDeEstoqueComSucesso() {
        // Given
        MovimentarEstoqueRequest request = MovimentarEstoqueRequest.builder()
                .unidadeId(1L).produtoId(1L).quantidade(10)
                .tipo(TipoMovimentacao.ENTRADA).motivo("Reposição").build();
        when(estoqueRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any())).thenReturn(estoque);
        when(movimentacaoRepository.save(any())).thenReturn(null);
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        EstoqueResponse response = estoqueService.movimentar(request, 1L);

        // Then
        assertNotNull(response);
        assertEquals(60, estoque.getQuantidade()); // 50 + 10
    }

    @Test
    @DisplayName("Deve realizar saída de estoque quando há quantidade suficiente")
    void deveRealizarSaidaDeEstoqueQuandoQuantidadeSuficiente() {
        // Given
        MovimentarEstoqueRequest request = MovimentarEstoqueRequest.builder()
                .unidadeId(1L).produtoId(1L).quantidade(10)
                .tipo(TipoMovimentacao.SAIDA).motivo("Venda").build();
        when(estoqueRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any())).thenReturn(estoque);
        when(movimentacaoRepository.save(any())).thenReturn(null);
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        EstoqueResponse response = estoqueService.movimentar(request, 1L);

        // Then
        assertNotNull(response);
        assertEquals(40, estoque.getQuantidade()); // 50 - 10
    }

    @Test
    @DisplayName("Deve lançar exceção quando saída de estoque excede quantidade disponível")
    void deveLancarExcecaoQuandoSaidaExcedeQuantidadeDisponivel() {
        // Given
        MovimentarEstoqueRequest request = MovimentarEstoqueRequest.builder()
                .unidadeId(1L).produtoId(1L).quantidade(100)
                .tipo(TipoMovimentacao.SAIDA).motivo("Venda").build();
        when(estoqueRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoque));

        // When/Then
        assertThrows(EstoqueInsuficienteException.class, () -> estoqueService.movimentar(request, 1L));
    }

    @Test
    @DisplayName("Deve verificar disponibilidade de estoque corretamente")
    void deveVerificarDisponibilidadeDeEstoque() {
        // Given
        when(estoqueRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoque));

        // When/Then
        assertTrue(estoqueService.verificarDisponibilidade(1L, 1L, 50));
        assertFalse(estoqueService.verificarDisponibilidade(1L, 1L, 51));
    }
}
