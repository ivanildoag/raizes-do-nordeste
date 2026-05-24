package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.application.dto.request.ProcessarPagamentoRequest;
import com.raizesdonordeste.application.dto.response.PagamentoResponse;
import com.raizesdonordeste.application.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Processamento de pagamentos (mock)")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/processar")
    @Operation(summary = "Processar pagamento mock", description = "Simula pagamento: ~70% aprovado, ~20% recusado, ~10% timeout")
    public ResponseEntity<PagamentoResponse> processar(@Valid @RequestBody ProcessarPagamentoRequest request) {
        return ResponseEntity.ok(pagamentoService.processarPagamento(request));
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Consultar pagamento de um pedido")
    public ResponseEntity<PagamentoResponse> consultarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.consultarPorPedido(pedidoId));
    }
}
