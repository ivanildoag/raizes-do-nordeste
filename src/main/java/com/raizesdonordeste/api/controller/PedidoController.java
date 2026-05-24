package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.application.dto.request.*;
import com.raizesdonordeste.application.dto.response.PedidoResponse;
import com.raizesdonordeste.application.service.PedidoService;
import com.raizesdonordeste.application.service.UsuarioService;
import com.raizesdonordeste.domain.enums.CanalPedido;
import com.raizesdonordeste.domain.enums.StatusPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de pedidos — fluxo crítico do sistema.
 * Suporta multicanalidade: todos os pedidos devem informar o canal de origem.
 * Permite filtrar pedidos por canal e status via query params.
 */
@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Criação e gerenciamento de pedidos multicanal")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Criar pedido", description = "Cria um pedido com validação de estoque. Campo canalPedido é OBRIGATÓRIO.")
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody CriarPedidoRequest request, Authentication auth) {
        Long clienteId = usuarioService.buscarEntidadePorEmail(auth.getName()).getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(request, clienteId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Listar pedidos (paginado)", description = "Filtros opcionais: ?canalPedido=APP&status=PAGO")
    public ResponseEntity<Page<PedidoResponse>> listar(
            @Parameter(description = "Filtro por canal") @RequestParam(required = false) CanalPedido canalPedido,
            @Parameter(description = "Filtro por status") @RequestParam(required = false) StatusPedido status,
            Pageable pageable) {
        return ResponseEntity.ok(pedidoService.listarPedidos(canalPedido, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<PedidoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/meus-pedidos")
    @Operation(summary = "Listar pedidos do cliente logado")
    public ResponseEntity<Page<PedidoResponse>> meusPedidos(Authentication auth, Pageable pageable) {
        Long clienteId = usuarioService.buscarEntidadePorEmail(auth.getName()).getId();
        return ResponseEntity.ok(pedidoService.listarPedidosDoCliente(clienteId, pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Atualizar status do pedido", description = "Valida transições de status conforme máquina de estados.")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id, @Valid @RequestBody AtualizarStatusPedidoRequest request) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido", description = "Cancela o pedido e restaura o estoque.")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id, Authentication auth) {
        Long usuarioId = usuarioService.buscarEntidadePorEmail(auth.getName()).getId();
        return ResponseEntity.ok(pedidoService.cancelar(id, usuarioId));
    }
}
