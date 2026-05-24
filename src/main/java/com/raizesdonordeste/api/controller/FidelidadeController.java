package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.application.dto.response.*;
import com.raizesdonordeste.application.service.FidelidadeService;
import com.raizesdonordeste.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fidelidade")
@RequiredArgsConstructor
@Tag(name = "Fidelidade", description = "Programa de fidelidade e pontos")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;
    private final UsuarioService usuarioService;

    @GetMapping("/saldo")
    @Operation(summary = "Consultar saldo de pontos de fidelidade")
    public ResponseEntity<FidelidadeResponse> consultarSaldo(Authentication auth) {
        Long usuarioId = usuarioService.buscarEntidadePorEmail(auth.getName()).getId();
        return ResponseEntity.ok(fidelidadeService.consultarSaldo(usuarioId));
    }

    @GetMapping("/historico")
    @Operation(summary = "Listar histórico de pontos (paginado)")
    public ResponseEntity<Page<HistoricoFidelidadeResponse>> listarHistorico(Authentication auth, Pageable pageable) {
        Long usuarioId = usuarioService.buscarEntidadePorEmail(auth.getName()).getId();
        return ResponseEntity.ok(fidelidadeService.listarHistorico(usuarioId, pageable));
    }
}
