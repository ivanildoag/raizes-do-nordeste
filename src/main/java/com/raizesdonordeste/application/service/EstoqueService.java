package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.MovimentarEstoqueRequest;
import com.raizesdonordeste.application.dto.response.EstoqueResponse;
import com.raizesdonordeste.application.dto.response.MovimentacaoEstoqueResponse;
import com.raizesdonordeste.application.mapper.EstoqueMapper;
import com.raizesdonordeste.domain.entity.*;
import com.raizesdonordeste.domain.enums.TipoMovimentacao;
import com.raizesdonordeste.domain.exception.EntidadeNaoEncontradaException;
import com.raizesdonordeste.domain.exception.EstoqueInsuficienteException;
import com.raizesdonordeste.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de controle de estoque por unidade.
 * 
 * Regras de negócio:
 * - Cada unidade possui estoque independente
 * - Saídas de estoque validam disponibilidade (não permite saldo negativo)
 * - Todas as movimentações são registradas para auditoria
 */
@Slf4j @Service @RequiredArgsConstructor
public class EstoqueService {
    private final EstoqueRepository estoqueRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public Page<EstoqueResponse> consultarPorUnidade(Long unidadeId, Pageable pageable) {
        return estoqueRepository.findByUnidadeId(unidadeId, pageable).map(EstoqueMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EstoqueResponse consultarPorUnidadeEProduto(Long unidadeId, Long produtoId) {
        Estoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Estoque não encontrado para unidade " + unidadeId + " e produto " + produtoId));
        return EstoqueMapper.toResponse(estoque);
    }

    /** Realiza entrada ou saída de estoque com registro de auditoria */
    @Transactional
    public EstoqueResponse movimentar(MovimentarEstoqueRequest request, Long usuarioId) {
        Estoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(request.getUnidadeId(), request.getProdutoId())
                .orElseGet(() -> {
                    // Cria registro de estoque se não existir
                    Unidade unidade = unidadeRepository.findById(request.getUnidadeId())
                            .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada"));
                    Produto produto = produtoRepository.findById(request.getProdutoId())
                            .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado"));
                    return estoqueRepository.save(Estoque.builder()
                            .unidade(unidade).produto(produto).quantidade(0).build());
                });

        // Regra de negócio: não permitir saldo negativo
        if (request.getTipo() == TipoMovimentacao.SAIDA) {
            if (estoque.getQuantidade() < request.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o produto: " + estoque.getProduto().getNome(),
                        List.of(new EstoqueInsuficienteException.DetalheEstoque(
                                "quantidade", "Disponível: " + estoque.getQuantidade())));
            }
            estoque.setQuantidade(estoque.getQuantidade() - request.getQuantidade());
        } else {
            estoque.setQuantidade(estoque.getQuantidade() + request.getQuantidade());
        }

        estoque = estoqueRepository.save(estoque);

        // Registra a movimentação para auditoria
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .estoque(estoque).tipo(request.getTipo()).quantidade(request.getQuantidade())
                .motivo(request.getMotivo()).usuario(usuario).build());

        log.info("Movimentação de estoque: {} {} unidades do produto {} na unidade {}",
                request.getTipo(), request.getQuantidade(),
                estoque.getProduto().getNome(), estoque.getUnidade().getNome());

        return EstoqueMapper.toResponse(estoque);
    }

    /** Verifica se há estoque suficiente (usado pelo PedidoService) */
    public boolean verificarDisponibilidade(Long unidadeId, Long produtoId, int quantidade) {
        return estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                .map(e -> e.getQuantidade() >= quantidade)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoEstoqueResponse> listarMovimentacoes(Long unidadeId, Pageable pageable) {
        return movimentacaoRepository.findByEstoqueUnidadeId(unidadeId, pageable)
                .map(EstoqueMapper::toMovimentacaoResponse);
    }
}
