package com.raizesdonordeste.application.service;

import com.raizesdonordeste.application.dto.response.UsuarioResponse;
import com.raizesdonordeste.application.mapper.UsuarioMapper;
import com.raizesdonordeste.domain.entity.Usuario;
import com.raizesdonordeste.domain.exception.EntidadeNaoEncontradaException;
import com.raizesdonordeste.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioMapper.toResponse(buscarUsuario(id));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(UsuarioMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarUsuario(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuário desativado: {} (id: {})", usuario.getEmail(), id);
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado com id: " + id));
    }
}
