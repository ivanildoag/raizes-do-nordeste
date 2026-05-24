package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.application.dto.request.*;
import com.raizesdonordeste.application.dto.response.UnidadeResponse;
import com.raizesdonordeste.application.service.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Gerenciamento de unidades da rede")
public class UnidadeController {

    private final UnidadeService unidadeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar nova unidade")
    public ResponseEntity<UnidadeResponse> criar(@Valid @RequestBody CriarUnidadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadeService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar unidades ativas (paginado)")
    public ResponseEntity<Page<UnidadeResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(unidadeService.listarTodas(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar unidade por ID")
    public ResponseEntity<UnidadeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Atualizar unidade")
    public ResponseEntity<UnidadeResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarUnidadeRequest request) {
        return ResponseEntity.ok(unidadeService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar unidade")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        unidadeService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
