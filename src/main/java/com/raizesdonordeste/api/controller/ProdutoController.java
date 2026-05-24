package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.application.dto.request.*;
import com.raizesdonordeste.application.dto.response.ProdutoResponse;
import com.raizesdonordeste.application.service.ProdutoService;
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
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento do cardápio de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Criar novo produto")
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody CriarProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar produtos (paginado)", description = "Filtro opcional por categoria via ?categoria=SALGADOS")
    public ResponseEntity<Page<ProdutoResponse>> listar(Pageable pageable, @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(produtoService.listarTodos(pageable, categoria));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Atualizar produto")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar produto")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        produtoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
