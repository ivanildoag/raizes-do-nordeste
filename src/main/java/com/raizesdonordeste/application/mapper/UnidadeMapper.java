package com.raizesdonordeste.application.mapper;

import com.raizesdonordeste.application.dto.request.CriarUnidadeRequest;
import com.raizesdonordeste.application.dto.response.UnidadeResponse;
import com.raizesdonordeste.domain.entity.Unidade;

public class UnidadeMapper {
    private UnidadeMapper() {}

    public static UnidadeResponse toResponse(Unidade u) {
        return UnidadeResponse.builder().id(u.getId()).nome(u.getNome()).endereco(u.getEndereco())
                .cidade(u.getCidade()).estado(u.getEstado()).telefone(u.getTelefone())
                .ativa(u.getAtiva()).createdAt(u.getCreatedAt()).build();
    }

    public static Unidade toEntity(CriarUnidadeRequest r) {
        return Unidade.builder().nome(r.getNome()).endereco(r.getEndereco()).cidade(r.getCidade())
                .estado(r.getEstado()).telefone(r.getTelefone()).ativa(true).build();
    }
}
