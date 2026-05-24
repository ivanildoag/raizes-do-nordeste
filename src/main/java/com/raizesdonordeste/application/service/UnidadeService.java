package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.request.*;
import com.raizesdonordeste.application.dto.response.UnidadeResponse;
import com.raizesdonordeste.application.mapper.UnidadeMapper;
import com.raizesdonordeste.domain.entity.Unidade;
import com.raizesdonordeste.domain.exception.EntidadeNaoEncontradaException;
import com.raizesdonordeste.domain.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j @Service @RequiredArgsConstructor
public class UnidadeService {
    private final UnidadeRepository unidadeRepository;

    @Transactional
    public UnidadeResponse criar(CriarUnidadeRequest request) {
        Unidade unidade = UnidadeMapper.toEntity(request);
        unidade = unidadeRepository.save(unidade);
        log.info("Unidade criada: {} (id: {})", unidade.getNome(), unidade.getId());
        return UnidadeMapper.toResponse(unidade);
    }

    @Transactional(readOnly = true)
    public UnidadeResponse buscarPorId(Long id) {
        return UnidadeMapper.toResponse(buscarUnidade(id));
    }

    @Transactional(readOnly = true)
    public Page<UnidadeResponse> listarTodas(Pageable pageable) {
        return unidadeRepository.findByAtivaTrue(pageable).map(UnidadeMapper::toResponse);
    }

    @Transactional
    public UnidadeResponse atualizar(Long id, AtualizarUnidadeRequest request) {
        Unidade unidade = buscarUnidade(id);
        if (request.getNome() != null) unidade.setNome(request.getNome());
        if (request.getEndereco() != null) unidade.setEndereco(request.getEndereco());
        if (request.getCidade() != null) unidade.setCidade(request.getCidade());
        if (request.getEstado() != null) unidade.setEstado(request.getEstado());
        if (request.getTelefone() != null) unidade.setTelefone(request.getTelefone());
        unidade = unidadeRepository.save(unidade);
        log.info("Unidade atualizada: {} (id: {})", unidade.getNome(), id);
        return UnidadeMapper.toResponse(unidade);
    }

    @Transactional
    public void desativar(Long id) {
        Unidade unidade = buscarUnidade(id);
        unidade.setAtiva(false);
        unidadeRepository.save(unidade);
        log.info("Unidade desativada: {} (id: {})", unidade.getNome(), id);
    }

    public Unidade buscarUnidade(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Unidade não encontrada com id: " + id));
    }
}
