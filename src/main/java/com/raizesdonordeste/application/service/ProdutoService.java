package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.*;
import com.raizesdonordeste.application.dto.response.ProdutoResponse;
import com.raizesdonordeste.application.mapper.ProdutoMapper;
import com.raizesdonordeste.domain.entity.Produto;
import com.raizesdonordeste.domain.exception.EntidadeNaoEncontradaException;
import com.raizesdonordeste.domain.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j @Service @RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    @Transactional
    public ProdutoResponse criar(CriarProdutoRequest request) {
        Produto produto = ProdutoMapper.toEntity(request);
        produto = produtoRepository.save(produto);
        log.info("Produto criado: {} (id: {})", produto.getNome(), produto.getId());
        return ProdutoMapper.toResponse(produto);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return ProdutoMapper.toResponse(buscarProduto(id));
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listarTodos(Pageable pageable, String categoria) {
        if (categoria != null && !categoria.isBlank()) {
            return produtoRepository.findByCategoriaAndAtivoTrue(categoria, pageable).map(ProdutoMapper::toResponse);
        }
        return produtoRepository.findByAtivoTrue(pageable).map(ProdutoMapper::toResponse);
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, AtualizarProdutoRequest request) {
        Produto produto = buscarProduto(id);
        if (request.getNome() != null) produto.setNome(request.getNome());
        if (request.getDescricao() != null) produto.setDescricao(request.getDescricao());
        if (request.getCategoria() != null) produto.setCategoria(request.getCategoria());
        if (request.getPreco() != null) produto.setPreco(request.getPreco());
        if (request.getImagemUrl() != null) produto.setImagemUrl(request.getImagemUrl());
        produto = produtoRepository.save(produto);
        log.info("Produto atualizado: {} (id: {})", produto.getNome(), id);
        return ProdutoMapper.toResponse(produto);
    }

    @Transactional
    public void desativar(Long id) {
        Produto produto = buscarProduto(id);
        produto.setAtivo(false);
        produtoRepository.save(produto);
        log.info("Produto desativado: {} (id: {})", produto.getNome(), id);
    }

    public Produto buscarProduto(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Produto não encontrado com id: " + id));
    }
}
