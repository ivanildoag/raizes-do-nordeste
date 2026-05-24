package com.raizesdonordeste.application.mapper;

import com.raizesdonordeste.application.dto.request.CriarProdutoRequest;
import com.raizesdonordeste.application.dto.response.ProdutoResponse;
import com.raizesdonordeste.domain.entity.Produto;

public class ProdutoMapper {
    private ProdutoMapper() {}

    public static ProdutoResponse toResponse(Produto p) {
        return ProdutoResponse.builder().id(p.getId()).nome(p.getNome()).descricao(p.getDescricao())
                .categoria(p.getCategoria()).preco(p.getPreco()).imagemUrl(p.getImagemUrl())
                .ativo(p.getAtivo()).createdAt(p.getCreatedAt()).build();
    }

    public static Produto toEntity(CriarProdutoRequest r) {
        return Produto.builder().nome(r.getNome()).descricao(r.getDescricao()).categoria(r.getCategoria())
                .preco(r.getPreco()).imagemUrl(r.getImagemUrl()).ativo(true).build();
    }
}
