package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.application.dto.response.UsuarioResponse;
import com.raizesdonordeste.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários do sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os usuários (paginado)", description = "Acesso restrito a ADMIN")
    public ResponseEntity<Page<UsuarioResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(usuarioService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Buscar perfil do usuário logado")
    public ResponseEntity<UsuarioResponse> perfil(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar usuário")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
