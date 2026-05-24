package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.application.dto.request.MovimentarEstoqueRequest;
import com.raizesdonordeste.application.dto.response.*;
import com.raizesdonordeste.application.service.EstoqueService;
import com.raizesdonordeste.application.service.UsuarioService;
import com.raizesdonordeste.domain.enums.TipoMovimentacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Controle de estoque por unidade")
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final UsuarioService usuarioService;

    @GetMapping("/unidade/{unidadeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Consultar estoque de uma unidade (paginado)")
    public ResponseEntity<Page<EstoqueResponse>> consultarPorUnidade(@PathVariable Long unidadeId, Pageable pageable) {
        return ResponseEntity.ok(estoqueService.consultarPorUnidade(unidadeId, pageable));
    }

    @GetMapping("/unidade/{unidadeId}/produto/{produtoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Consultar estoque de um produto em uma unidade")
    public ResponseEntity<EstoqueResponse> consultarPorUnidadeEProduto(@PathVariable Long unidadeId, @PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueService.consultarPorUnidadeEProduto(unidadeId, produtoId));
    }

    @PostMapping("/entrada")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Registrar entrada de estoque")
    public ResponseEntity<EstoqueResponse> entrada(@Valid @RequestBody MovimentarEstoqueRequest request, Authentication auth) {
        request.setTipo(TipoMovimentacao.ENTRADA);
        Long usuarioId = usuarioService.buscarEntidadePorEmail(auth.getName()).getId();
        return ResponseEntity.ok(estoqueService.movimentar(request, usuarioId));
    }

    @PostMapping("/saida")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Registrar saída de estoque")
    public ResponseEntity<EstoqueResponse> saida(@Valid @RequestBody MovimentarEstoqueRequest request, Authentication auth) {
        request.setTipo(TipoMovimentacao.SAIDA);
        Long usuarioId = usuarioService.buscarEntidadePorEmail(auth.getName()).getId();
        return ResponseEntity.ok(estoqueService.movimentar(request, usuarioId));
    }

    @GetMapping("/movimentacoes/{unidadeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar movimentações de estoque de uma unidade")
    public ResponseEntity<Page<MovimentacaoEstoqueResponse>> listarMovimentacoes(@PathVariable Long unidadeId, Pageable pageable) {
        return ResponseEntity.ok(estoqueService.listarMovimentacoes(unidadeId, pageable));
    }
}
